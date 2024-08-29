package fr.cgi.learninghub.swarm.service;

import fr.cgi.learninghub.swarm.clients.EntDirectoryClient;
import fr.cgi.learninghub.swarm.core.constants.AppConfig;
import fr.cgi.learninghub.swarm.exception.ENTGetStructuresException;
import fr.cgi.learninghub.swarm.exception.ENTGetUsersInfosException;
import fr.cgi.learninghub.swarm.exception.ListServiceException;

import java.util.List;
import java.util.ArrayList;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import fr.cgi.learninghub.swarm.model.ResponseListUser;
import fr.cgi.learninghub.swarm.model.Service;
import fr.cgi.learninghub.swarm.model.User;
import fr.cgi.learninghub.swarm.model.Class;
import fr.cgi.learninghub.swarm.model.Group;
import fr.cgi.learninghub.swarm.repository.ServiceRepository;



@ApplicationScoped
public class EntService implements IUserService {
    
    private static final Logger log = Logger.getLogger(EntService.class);
    private final EntDirectoryClient entDirectoryClient;
    private final AppConfig appConfig;
    ServiceRepository serviceRepository;

    @Inject
    public EntService(@RestClient EntDirectoryClient entDirectoryClient,
                        AppConfig appConfig,
                        ServiceRepository serviceRepository) {
        this.entDirectoryClient = entDirectoryClient;
        this.appConfig = appConfig;
        this.serviceRepository = serviceRepository;
    }

    // Functions

    public Uni<ResponseListUser> getAndFilterUsers() {
        List<Class> classInfos = new ArrayList<>();
        List<Group> groupInfos = new ArrayList<>();

        return getAllUsers()
            .chain(users -> {
                List<User> usersFiltered = filterUsersByClassAndGroup(users); // keep only user in class or group predefined
                return serviceRepository.listByUsersIdsMultiple(getUsersIds(usersFiltered)) // get services in BDD for this users
                    .chain(services -> {
                        List<String> userIdsToFilter = services.stream().map(Service::getUserId).toList();
                        List<User> finalUsers = removeUsersByIds(usersFiltered, userIdsToFilter);

                        // We get classInfos and groupInfos from users data
                        finalUsers.stream().forEach(user -> {
                            classInfos.addAll(user.getClasses().stream().filter(c -> !classInfos.stream().map(Class::getId).toList().contains(c.getId())).toList());
                            groupInfos.addAll(user.getGroups().stream().filter(g -> !groupInfos.stream().map(Group::getId).toList().contains(g.getId())).toList());
                        });
                        return Uni.createFrom().item(new ResponseListUser(finalUsers, classInfos, groupInfos)); // return users, classes, groups (id et name each time)
                    })
                    .onFailure().recoverWithUni(err -> {
                        log.error(String.format("[SwarmApi@%s::getAndFilterUsers] Failed to list services : %s", this.getClass().getSimpleName(), err.getMessage()));
                        return Uni.createFrom().failure(new ListServiceException());
                    });
                }
            )
            .onFailure().recoverWithUni(err -> {
                log.error(String.format("[SwarmApi@%s::getAndFilterUsers] Failed to get ENT user infos : %s", this.getClass().getSimpleName(), err.getMessage()));
                return Uni.createFrom().failure(new ENTGetUsersInfosException());
            });
    }

    public Uni<List<User>> getAllUsers() {
        return getConnectedUserStructures()
            .chain(structuresIds -> {
                return Multi.createFrom().iterable(structuresIds)
                    .onItem().transformToUniAndMerge(structureId -> entDirectoryClient.getUsersByStructure(appConfig.getSessionId(), structureId))
                    .onItem().transformToMulti(responses -> Multi.createFrom().items(responses.stream()))
                    .merge()
                    .collect()
                    .asList();
            })
            .onFailure().recoverWithUni(err -> {
                log.error(String.format("[SwarmApi@%s::getAllUsers] Failed to get structures of connected user : %s", this.getClass().getSimpleName(), err.getMessage()));
                return Uni.createFrom().failure(new ENTGetStructuresException());
            });
    }

    public Uni<List<String>> getConnectedUserStructures() {
        // TODO : get userId thanks to Quarkus token and call ENT to get user infos with this id
        List<String> structuresIds = new ArrayList<>();
        structuresIds.add("0561f703-4e72-46fe-a92e-d887fd27439b"); // TODO : remove (this is for tests)
        structuresIds.add("391b8dfa-a7d4-4d9f-90ca-d029a2d64281"); // TODO : remove (this is for tests)
        return Uni.createFrom().item(structuresIds);
    }

    // Utils

    private List<User> removeUsersByIds(List<User> users, List<String> userIdsToFilter) {
        return users.stream().filter(user -> !userIdsToFilter.contains(user.getId())).toList();     
    }

    private List<User> filterUsersByClassAndGroup(List<User> users) {
        return users.stream()
            .filter(user -> appConfig.getClassIds().stream().anyMatch(user.getClassIds()::contains) || appConfig.getGroupIds().stream().anyMatch(user.getGroupIds()::contains))
            .toList();
    }

    private List<String> getUsersIds(List<User> users) {
        return users.stream().map(User::getId).toList();
    }
}
