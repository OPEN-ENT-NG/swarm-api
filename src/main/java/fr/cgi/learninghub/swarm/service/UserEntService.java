package fr.cgi.learninghub.swarm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.cgi.learninghub.swarm.clients.EntDirectoryClient;
import fr.cgi.learninghub.swarm.config.AppConfig;
import fr.cgi.learninghub.swarm.core.enums.Profile;
import fr.cgi.learninghub.swarm.exception.ENTGetUsersInfosException;
import fr.cgi.learninghub.swarm.exception.NoClassesProvidedException;
import fr.cgi.learninghub.swarm.model.ClassInfos;
import fr.cgi.learninghub.swarm.model.ResponseListClasses;
import fr.cgi.learninghub.swarm.model.User;
import fr.cgi.learninghub.swarm.model.UserInfos;
import fr.cgi.learninghub.swarm.repository.ServiceRepository;
import io.quarkus.oidc.UserInfo;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@ApplicationScoped
public class UserEntService {

    private static final Logger log = Logger.getLogger(UserEntService.class);

    @Inject
    AppConfig appConfig;

    @Inject
    ServiceRepository serviceRepository;

    @Inject
    @RestClient
    EntDirectoryClient entDirectoryClient;

    @Inject
    JsonWebToken jwt;
    @Inject
    UserInfo userInfo;

    public Uni<List<User>> listGlobalUsersInfo() {
        log.error("Starting listGlobalUsersInfo method");
        log.error("AppConfig Class IDs: " + appConfig.getClassIds());
        log.error("AppConfig Group IDs: " + appConfig.getGroupIds());
        log.error("AppConfig Mail Domain: " + appConfig.getMailDomain());
        log.error("AppConfig Host: " + appConfig.getHost());
        return fetchMyUserInfo()
                .onItem().invoke(userInfos -> log.error("Fetched user structures information: " + userInfos.getStructuresIds()))
                .onFailure().invoke(err -> log.error("Error fetching user info: " + err.getMessage()))

                .chain(userInfos -> getClassesByStructures(userInfos.getStructuresIds()))
                .onItem().invoke(classes -> log.error("Fetched classes by structures: " + classes))
                .onFailure().invoke(err -> log.error("Error fetching classes by structures: " + err.getMessage()))

                .chain(this::filterClassesByConfig)
                .onItem().invoke(filteredClasses -> log.error("Filtered classes by config: " + filteredClasses))
                .onFailure().invoke(err -> log.error("Error filtering classes by config: " + err.getMessage()))

                .chain(this::getUsersByClasses)
                .onItem().invoke(users -> log.error("Fetched users by classes: " + users))
                .onFailure().invoke(err -> log.error("Error fetching users by classes: " + err.getMessage()))

                .onFailure().recoverWithUni(err -> {
                    log.error("Failed to fetch users: " + err.getMessage());
                    return Uni.createFrom().failure(new ENTGetUsersInfosException());
                });
    }


    // 1. Étape pour filtrer les classes par la configuration (appConfig)
    private Uni<List<ClassInfos>> filterClassesByConfig(List<ClassInfos> classes) {
        classes.forEach(classInfo -> log.error("Fetched class: " + classInfo.getId())); // Log de toutes les classes
        return Uni.createFrom().item(() ->
                classes.stream()
                        .filter(userClass -> appConfig.getClassIds().contains(userClass.getId()))
                        .collect(Collectors.toList())
        );
    }

    // Étape pour récupérer tous les utilisateurs à partir des classes (sans grouper par école)
    private Uni<List<User>> getUsersByClasses(List<ClassInfos> classes) {

        if (classes.isEmpty()) {
            log.error(String.format("[SwarmApi@%s::getUsersByClasses] Failed to get classes for user provided", this.getClass().getSimpleName()));
            return Uni.createFrom().failure(new NoClassesProvidedException()); // Lancer une exception
        }

        Map<String, User> userMap = new HashMap<>();
        // Crée une liste d'opérations asynchrones pour chaque classe
        List<Uni<List<User>>> userFetchUnis = classes.stream()
                .map(this::fetchUsersByClass) // Récupère les utilisateurs pour chaque classe
                .collect(Collectors.toList());

        return Uni.combine().all().unis(userFetchUnis)
                .with(userLists -> {
                    List<List<User>> listsOfUsers = (List<List<User>>) userLists;
                    // Aplatir les listes d'utilisateurs et les ajouter à la map
                    listsOfUsers.stream()
                            .flatMap(Collection::stream) // Utilise une expression lambda à la place de List::stream
                            .forEach(user -> userMap.merge(user.getId(), user, (existingUser, newUser) -> {
                                List<ClassInfos> newClasses = Stream.concat(existingUser.getClasses().stream(), newUser.getClasses().stream())
                                        .toList();
                                existingUser.setClassesInfos(newClasses);
                                return existingUser;
                            }));
                    // Retourner la liste des utilisateurs fusionnés
                    return new ArrayList<>(userMap.values());
                });
    }


    // Étape pour récupérer les utilisateurs pour une classe donnée
    private Uni<List<User>> fetchUsersByClass(ClassInfos userClass) {
        return entDirectoryClient.getUsersByClass(userClass.getId(), Profile.STUDENT.getValue())
                .onItem().transform(users -> users.stream()
                        .peek(user -> {
                            user.setClassesInfos(Collections.singletonList(userClass));
                            user.setStructure(userClass.getSchoolId());
                            user.setMail(String.format("%s@%s", user.getId(), appConfig.getMailDomain()));
                        })
                        .collect(Collectors.toList()));
    }

    // Étape pour récupérer les classes par structures
    private Uni<List<ClassInfos>> getClassesByStructures(List<String> structureIds) {
        return entDirectoryClient.listClassesInStructuresByIds(structureIds)
                .onItem().transform(response -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        ResponseListClasses result = mapper.readValue(response, ResponseListClasses.class);
                        return result.getResult().values().stream().collect(Collectors.toList());
                    } catch (Exception e) {
                        log.error(String.format("[SwarmApi@%s::getClassesByStructures] Failed to parse response for structures %s : %s", this.getClass().getSimpleName(), structureIds, e.getMessage()));
                        throw new ENTGetUsersInfosException();
                    }
                })
                .onFailure().recoverWithUni(err -> {
                    log.error(String.format("[SwarmApi@%s::getClassesByStructures] Failed to list classes for structures %s : %s", this.getClass().getSimpleName(), structureIds, err.getMessage()));
                    return Uni.createFrom().failure(new ENTGetUsersInfosException());
                });
    }


    public Uni<UserInfos> fetchMyUserInfo() {
        String userId = jwt.getName(); // or use getClaim() if userId is stored in an attribute
        return entDirectoryClient.getUserInfos(userId);
    }

}
