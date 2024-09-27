package fr.cgi.learninghub.swarm.service;

import fr.cgi.learninghub.swarm.clients.EntDirectoryClient;
import fr.cgi.learninghub.swarm.config.AppConfig;
import fr.cgi.learninghub.swarm.core.enums.Profile;
import fr.cgi.learninghub.swarm.exception.ENTGetStructuresException;
import fr.cgi.learninghub.swarm.exception.ENTGetUsersInfosException;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import fr.cgi.learninghub.swarm.model.*;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import fr.cgi.learning.hub.swarm.common.entities.Service;
import fr.cgi.learninghub.swarm.repository.ServiceRepository;


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

    public Uni<ResponseListUser> listGlobalUsersInfo() {
        return fetchMyUserInfo()
                .chain(userInfos -> getUsersByUais(userInfos.getStructuresUais(), Profile.STUDENT))
                .chain(this::filterUsersByClassAndGroup)
                .chain(this::filterUsersByExistingUsersWithServices)
                .chain(this::fetchClassGroupStructureUsersInfo)
                .onFailure().recoverWithUni(err -> {
                    log.error(String.format("[SwarmApi@%s::listGlobalUsersInfo] Failed to list global users info: %s", this.getClass().getSimpleName(), err.getMessage()));
                    return Uni.createFrom().failure(new ENTGetStructuresException());
                });
    }

    public Uni<List<User>> getUsersByUais(List<String> uais, Profile profile) {
        return entDirectoryClient.listUserInStructuresByUAI(uais, true)
                .chain(users -> Uni.createFrom().item(profile == null ? users : filterByProfile(users, profile)))
                .onFailure().recoverWithUni(err -> {
                    log.error(String.format("[SwarmApi@%s::getUsersByUais] Failed to list users for UAIs %s : %s", this.getClass().getSimpleName(), uais, err.getMessage()));
                    return Uni.createFrom().failure(new ENTGetUsersInfosException());
                });
    }
    private Uni<List<User>> filterUsersByClassAndGroup(List<User> users) {
        return Uni.createFrom().item(users.stream()
                .filter(user -> appConfig.getClassIds().stream().anyMatch(user.getClassIds()::contains) || appConfig.getGroupIds().stream().anyMatch(user.getGroupIds()::contains))
                .toList());
    }

    private Uni<List<User>> filterUsersByExistingUsersWithServices(List<User> users) {
        return serviceRepository.listByUsersIdsMultiple(getUsersIds(users))
                .chain(services -> {
                    // fetch all userId in existing services
                    List<String> userIdsToFilter = services.stream().map(Service::getUserId).toList();
                    // filtered users with users without services
                    List<User> filteredUsers = removeUsersByIds(users, userIdsToFilter);
                    return Uni.createFrom().item(filteredUsers);
                })
                .onFailure().recoverWithUni(throwable -> {
                    log.error(String.format("[SwarmApi@%s::filterUsersByExistingUsersWithServices] Failed to filter users" +
                                    " by existing users with services : %s", this.getClass().getSimpleName(), throwable.getMessage()));
                    return Uni.createFrom().failure(new ENTGetUsersInfosException());
                });
    }

    public Uni<ResponseListUser> fetchClassGroupStructureUsersInfo(List<User> users) {
        List<ClassInfos> classInfos = new ArrayList<>();
        List<GroupInfos> groupInfos = new ArrayList<>();

        users.forEach(user -> {
            classInfos.addAll(user.getClasses().stream().filter(c -> !classInfos.stream().map(ClassInfos::getId).toList().contains(c.getId())).toList());
            // TODO MUST IMPLEMENT GROUPS
            // groupInfos.addAll(user.getGroups().stream().filter(g -> !groupInfos.stream().map(StudentGroup::getId).toList().contains(g.getId())).toList());
        });

        // fetch
        List<String> structuresFetchedFromUser = users.stream()
                .map(User::getStructure)
                .distinct()
                .toList();

        // fetch Structures info
        return entDirectoryClient.listAllStructures()
                .chain(structureInfos -> filterAndBuildStructuresWithUserStructureInfo(structureInfos, structuresFetchedFromUser))
                .chain(structures -> Uni.createFrom().item(new ResponseListUser(users, classInfos, groupInfos, structures)))
                .onFailure().recoverWithUni(throwable -> {
                    log.error(String.format("[SwarmApi@%s::fetchClassGroupStructureUsersInfo] Failed to fetch class group structure users info : %s",
                            this.getClass().getSimpleName(), throwable.getMessage()));
                    return Uni.createFrom().failure(new ENTGetUsersInfosException());
                });
    }
    public Uni<List<StructureInfos>> filterAndBuildStructuresWithUserStructureInfo(List<StructureInfos> structuresInfos, List<String> structureFromUser) {
        // store all structureInfos in a Map
        Map<String, StructureInfos> structureMap = structuresInfos.stream()
                .collect(Collectors.toMap(StructureInfos::getExternalId, structure -> structure));

        // Use Map to build StructureInfo with list of structure fetched from users
        return Uni.createFrom().item(structureFromUser.stream()
                .map(structureMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
    }

    public Uni<List<User>> getAllUsers(Profile profile) {
        return fetchMyUserInfo()
            .chain(userInfos -> getUsersByUais(userInfos.getStructuresIds(), profile))
            .onFailure().recoverWithUni(err -> {
                log.error(String.format("[SwarmApi@%s::getAllUsers] Failed to get structures of connected user : %s", this.getClass().getSimpleName(), err.getMessage()));
                return Uni.createFrom().failure(new ENTGetStructuresException());
            });
    }

    public Uni<UserInfos> fetchMyUserInfo() {
        String userId = jwt.getName(); // or use getClaim() if userId is stored in an attribute
        return entDirectoryClient.getUserInfos(userId);
    }

    private List<User> removeUsersByIds(List<User> users, List<String> userIdsToFilter) {
        return users.stream().filter(user -> !userIdsToFilter.contains(user.getId())).toList();     
    }

    private List<User> filterByProfile(List<User> users, Profile profile) {
        return users.stream().filter(user -> user.getProfile() != null && user.getProfile() == profile).toList();
    }

    private List<String> getUsersIds(List<User> users) {
        return users.stream().map(User::getId).toList();
    }
}
