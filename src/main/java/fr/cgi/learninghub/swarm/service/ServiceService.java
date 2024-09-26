package fr.cgi.learninghub.swarm.service;

import fr.cgi.learning.hub.swarm.common.entities.Service;
import fr.cgi.learning.hub.swarm.common.enums.PathType;
import fr.cgi.learning.hub.swarm.common.enums.State;
import fr.cgi.learning.hub.swarm.common.enums.Type;
import fr.cgi.learninghub.swarm.core.enums.Order;
import fr.cgi.learninghub.swarm.core.enums.Profile;
import fr.cgi.learninghub.swarm.exception.*;
import fr.cgi.learninghub.swarm.model.*;
import fr.cgi.learninghub.swarm.repository.ServiceRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.*;

@ApplicationScoped
public class ServiceService {

    private static final Logger log = Logger.getLogger(ServiceService.class);

    @Inject
    ServiceRepository serviceRepository;

    @Inject
    IUserService userService;

    // Functions

    public Uni<ResponseListService> listAllAndFilter(List<String> structures, List<String> classes, List<String> groups,
                                                     String search, List<Type> types, Order order, int page, int limit) {
        return userService.getConnectedUserInfos()
                .chain(userInfos -> userService.getUsersByUais(userInfos.getStructuresIds(), Profile.STUDENT)
                        .chain(students -> {
                            List<User> filteredStudents = students;
                            if (structures != null && !structures.isEmpty()) {
                                filteredStudents = filteredStudents.stream().filter(student -> structures.contains(student.getStructure())).toList();
                            }
                            if (classes != null && !classes.isEmpty()) {
                                filteredStudents = filteredStudents.stream().filter(student -> classes.stream().anyMatch(student.getClassIds()::contains)).toList();
                            }
//                if (groups != null && !groups.isEmpty()) {
//                    filteredStudents = filteredStudents.stream().filter(student -> groups.stream().anyMatch(student.getGroupIds()::contains)).toList();
//                }

                            return getServicesFromFilteredUsers(search, types, order, page, limit, students, filteredStudents, userInfos);
                        })
                        .onFailure().recoverWithUni(err -> {
                            log.error(String.format("[SwarmApi@%s::listAllAndFilter] Failed to list users for UAIs %s : %s", this.getClass().getSimpleName(), userInfos.getStructuresIds(), err.getMessage()));
                            return Uni.createFrom().failure(new ENTGetUsersInfosException());
                        }))
                .onFailure().recoverWithUni(err -> {
                    log.error(String.format("[SwarmApi@%s::listAllAndFilter] Failed to get structures of connected user : %s", this.getClass().getSimpleName(), err.getMessage()));
                    return Uni.createFrom().failure(new ENTGetStructuresException());
                });
    }

    public Uni<List<Service>> create(CreateServiceBody createServiceBody) {
        return userService.getAllUsers(Profile.STUDENT)
                .chain(users -> Uni.createFrom().item(createServicesObjects(users, createServiceBody)))
                .chain(serviceRepository::create)
                .chain(services -> {
                    services.forEach(service -> service.setServiceName(service.getServiceName() + service.getId())); // complete serviceNames with ids
                    return serviceRepository.patchServiceName(services);
                })
                .onFailure().recoverWithUni(err -> {
                    log.error(String.format("[SwarmApi@%s::create] Failed to create service in database : %s", this.getClass().getSimpleName(), err.getMessage()));
                    return Uni.createFrom().failure(new CreateServiceException());
                });
    }

    public Uni<Integer> delete(UpdateServiceBody updateServiceBody) {
        return serviceRepository.patch(updateServiceBody)
                .onFailure().recoverWithUni(err -> {
                    log.error(String.format("[SwarmApi@%s::delete] Failed to delete services in database : %s", this.getClass().getSimpleName(), err.getMessage()));
                    return Uni.createFrom().failure(new DeleteServiceException());
                });
    }

    // Utils

    private Uni<ResponseListService> getServicesFromFilteredUsers(String search, List<Type> types, Order order, int page, int limit,
                                                                  List<User> students, List<User> filteredStudents, UserInfos userInfos) {
        List<String> usersIds = filteredStudents.stream().map(User::getId).toList();
        List<State> hiddenStates = Arrays.asList(State.DEPLOYMENT_IN_ERROR, State.DELETION_SCHEDULED, State.DELETION_IN_PROGRESS, State.DELETION_IN_ERROR, State.RESET_IN_ERROR);

        return serviceRepository.listAllWithFilter(usersIds, search, types, hiddenStates)
                .chain(services -> {
                    // Calculate total users
                    List<String> finalUsersIds = services.stream().map(Service::getUserId).distinct().toList();
                    int totalUsers = finalUsersIds.size();

                    return setResponseListServiceGlobalInfos(students, userInfos, totalUsers, hiddenStates)
                            .chain(responseListServiceGlobalInfos -> buildResponseListService(order, page, limit, finalUsersIds, filteredStudents, userInfos, responseListServiceGlobalInfos))
                            .onFailure().recoverWithUni(err -> {
                                log.error(String.format("[SwarmApi@%s::getServicesFromFilteredUsers] Failed to create responseListServiceGlobalInfos : %s", this.getClass().getSimpleName(), err.getMessage()));
                                return Uni.createFrom().failure(new ListServiceException());
                            });
                })
                .onFailure().recoverWithUni(err -> {
                    log.error(String.format("[SwarmApi@%s::getServicesFromFilteredUsers] Failed to filter userIds from database : %s", this.getClass().getSimpleName(), err.getMessage()));
                    return Uni.createFrom().failure(new ListServiceException());
                });
    }

    private Uni<ResponseListService> buildResponseListService(Order order, int page, int limit, List<String> usersIds, List<User> filteredStudents,
                                                              UserInfos userInfos, ResponseListServiceGlobalInfos responseListServiceGlobalInfos) {
        return serviceRepository.listAllWithFilterAndLimit(usersIds, order, page, limit)
                .chain(services -> {
                    List<ResponseListServiceUser> users = new ArrayList<>();
                    usersIds.forEach(userId -> {
                        List<Service> userServices = services.stream().filter(service -> service.getUserId().equals(userId)).toList();
                        List<ClassInfos> userClasses = filteredStudents.stream()
                                .filter(student -> student.getId().equals(userId))
                                .findFirst()
                                .map(User::getClasses)
                                .orElse(null);

                        ResponseListServiceUser user = new ResponseListServiceUser()
                                .setStructures(userInfos.getStructures())
                                .setClasses(userClasses)
                                .setServices(userServices);
                        users.add(user);
                    });

                    ResponseListService responseListService = new ResponseListService()
                            .setGlobalInfos(responseListServiceGlobalInfos)
                            .setFilteredUsers(users);

                    return Uni.createFrom().item(responseListService);
                })
                .onFailure().recoverWithUni(err -> {
                    log.error(String.format("[SwarmApi@%s::buildResponseListService] Failed to list services from database : %s", this.getClass().getSimpleName(), err.getMessage()));
                    return Uni.createFrom().failure(new ListServiceException());
                });
    }

    private List<Service> createServicesObjects(List<User> users, CreateServiceBody createServiceBody) {
        List<Service> services = new ArrayList<>();
        List<Type> types = createServiceBody.getTypes();
        List<User> filteredUsers = filterUsersNotInBody(users, createServiceBody);

        filteredUsers.stream().forEach(user -> {
            types.stream().forEach(type -> {
                Service service = new Service();
                service.setUserId(user.getId())
                        .setFirstName(user.getFirstName())
                        .setLastName(user.getLastName())
                        .setServiceName(String.format("%s-", PathType.getValue(type))) // FIXME This is bullshit code
                        .setStructureId(user.getStructure())
                        .setType(type)
                        .setDeletionDate(createServiceBody.getDeletionDate())
                        .setState(State.SCHEDULED);
                services.add(service);
            });
        });

        return services;
    }

    private List<User> filterUsersNotInBody(List<User> users, CreateServiceBody createServiceBody) {
        return users.stream()
                .filter(user -> {
                    List<String> classesIds = user.getClasses().stream().map(ClassInfos::getId).toList();
                    // List<String> groupsIds = user.getGroups().stream().map(GroupInfos::getId).toList();

                    return createServiceBody.getUsersIds().contains(user.getId()) ||
                            createServiceBody.getClassesIds().stream().anyMatch(classesIds::contains);
                }).toList();
    }

    private Uni<ResponseListServiceGlobalInfos> setResponseListServiceGlobalInfos(List<User> students, UserInfos userInfos, int totalUsers, List<State> hiddenStates) {
        return serviceRepository.listAll()
                .chain(allServices -> {
                    allServices = allServices.stream().filter(service -> !hiddenStates.contains(service.getState())).toList();
                    List<String> allServicesIds = allServices.stream().map(Service::getUserId).toList();
                    List<User> finalStudents = students.stream().filter(student -> allServicesIds.contains(student.getId())).toList();

                    // Find all structures for filtered users
                    List<String> structuresInfosIds = finalStudents.stream().map(User::getStructure).distinct().toList();
                    List<StructureInfos> structuresInfos = userInfos.getStructures().stream().filter(s -> structuresInfosIds.contains(s.getExternalId())).distinct().toList();

                    // Find all classes infos for filtered users
                    List<ClassInfos> classesInfos = new ArrayList<>();
                    Set<String> seenIds = new HashSet<>();
                    finalStudents.stream()
                            .map(User::getClasses)
                            .flatMap(List::stream)
                            .filter(classInfo -> seenIds.add(classInfo.getId())) // keep objects only once thanks to the 'add' to the Set returning false if the id alreadyexists
                            .forEach(classInfo -> classesInfos.add(classInfo));

                    // Find all groups infos for filtered users
                    // List<ClassInfos> groupsInfos = new ArrayList<>();

                    // Find all user infos for filtered users
                    List<UserInfos> usersInfos = finalStudents.stream()
                            .map(student -> {
                                return new UserInfos()
                                        .setId(student.getId())
                                        .setFirstName(student.getFirstName())
                                        .setLastName(student.getLastName());
                            }).toList();

                    ResponseListServiceGlobalInfos responseListServiceGlobalInfos = new ResponseListServiceGlobalInfos()
                            .setTotalUsers((long) totalUsers)
                            .setStructures(structuresInfos)
                            .setClasses(classesInfos)
                            .setUsers(usersInfos);

                    return Uni.createFrom().item(responseListServiceGlobalInfos);
                })
                .onFailure().recoverWithUni(err -> {
                    log.error(String.format("[SwarmApi@%s::setResponseListServiceGlobalInfos] Failed to list all services from database : %s", this.getClass().getSimpleName(), err.getMessage()));
                    return Uni.createFrom().failure(new ListServiceException());
                });
    }
}
