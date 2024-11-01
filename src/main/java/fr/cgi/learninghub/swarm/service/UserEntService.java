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
    @RestClient
    EntDirectoryClient entDirectoryClient;

    @Inject
    JsonWebToken jwt;

    public Uni<List<User>> listGlobalUsersInfo() {
        return fetchMyUserInfo()
            .chain(this::retrieveGlobalUsersInfoAndAddMe)
            .onFailure().recoverWithUni(err -> {
                String errorMessage = "[SwarmApi@%s::listGlobalUsersInfo] Failed to fetch connected user infos : %s";
                log.error(String.format(errorMessage, this.getClass().getSimpleName(), err.getMessage()));
                return Uni.createFrom().failure(new ENTGetUsersInfosException());
            });
    }

    public Uni<List<User>> retrieveGlobalUsersInfoAndAddMe(UserInfos userInfos) {
        return getClassesByStructures(userInfos.getStructuresIds())
                .chain(this::filterClassesByConfig)
                .chain(this::getUsersByClasses)
                .chain(users -> this.addMeAsServiceUser(users, userInfos))
                .onFailure().recoverWithUni(err -> {
                    String errorMessage = "[SwarmApi@%s::retrieveGlobalUsersInfoAndAddMe] Failed to list all user infos for connected user : %s";
                    log.error(String.format(errorMessage, this.getClass().getSimpleName(), err.getMessage()));
                    return Uni.createFrom().failure(new ENTGetUsersInfosException());
                });
    }

    // 1. Étape pour filtrer les classes par la configuration (appConfig)
    private Uni<List<ClassInfos>> filterClassesByConfig(List<ClassInfos> classes) {
        return Uni.createFrom().item(() ->
                classes.stream()
                        .filter(userClass -> appConfig.getClassIds().contains(userClass.getId()))
                        .collect(Collectors.toList())
        );
    }

    // Étape pour récupérer tous les utilisateurs à partir des classes (sans grouper par école)
    private Uni<List<User>> getUsersByClasses(List<ClassInfos> classes) {

        if (classes.isEmpty()) {
            String errorMessage = "[SwarmApi@%s::getUsersByClasses] No classes provided to get users from";
            log.error(String.format(errorMessage, this.getClass().getSimpleName()));
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
                                List<ClassInfos> newClasses = Stream.concat(existingUser.getClasses().stream(), newUser.getClasses().stream()).toList();
                                existingUser.setClassesInfos(newClasses);
                                return existingUser;
                            }));
                    // Retourner la liste des utilisateurs fusionnés
                    return new ArrayList<>(userMap.values());
                });
    }


    // Étape pour récupérer les utilisateurs pour une classe et un profil donnés
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
        // Créer une liste d'operations asynchrones pour chaque structureId
        List<Uni<List<ClassInfos>>> classFetchUnis = structureIds.stream()
                .map(structureId -> entDirectoryClient.listClassesInStructuresByIds(structureId)
                        .onItem().transform(response -> {
                            try {
                                ObjectMapper mapper = new ObjectMapper();
                                ResponseListClasses result = mapper.readValue(response, ResponseListClasses.class);
                                return result.getResult().values().stream().collect(Collectors.toList());
                            } catch (Exception e) {
                                String errorMessage = "[SwarmApi@%s::getClassesByStructures] Failed to parse response for structure %s : %s";
                                log.error(String.format(errorMessage, this.getClass().getSimpleName(), structureId, e.getMessage()));
                                throw new ENTGetUsersInfosException();
                            }
                        })
                        .onFailure().invoke(err -> {
                            String errorMessage = "[SwarmApi@%s::getClassesByStructures] Failed to list classes for structure %s : %s";
                            log.error(String.format(errorMessage, this.getClass().getSimpleName(), structureId, err.getMessage()));
                        })
                )
                .collect(Collectors.toList());

        // Combiner les résultats des appels asynchrones pour chaque structureId
        return Uni.combine().all().unis(classFetchUnis)
                .with(classLists -> classLists.stream()
                        .flatMap(list -> ((List<ClassInfos>) list).stream())
                        .collect(Collectors.toList()))
                .onFailure().recoverWithUni(err -> {
                    String errorMessage = "[SwarmApi@%s::getClassesByStructures] Failed to retrieve classes for structures %s : %s";
                    log.error(String.format(errorMessage, this.getClass().getSimpleName(), structureIds, err.getMessage()));
                    return Uni.createFrom().failure(new ENTGetUsersInfosException());
                });
    }

    private Uni<List<User>> addMeAsServiceUser(List<User> users, UserInfos userInfos) {
        users.add(new User(userInfos));
        return Uni.createFrom().item(users);
    }


    public Uni<UserInfos> fetchMyUserInfo() {
        String userId = jwt.getName(); // or use getClaim() if userId is stored in an attribute
        return entDirectoryClient.getUserInfos(userId);
    }

}
