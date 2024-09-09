package fr.cgi.learninghub.swarm.service;

import fr.cgi.learninghub.swarm.core.enums.PathType;
import fr.cgi.learninghub.swarm.core.enums.Profile;
import fr.cgi.learninghub.swarm.core.enums.Order;
import fr.cgi.learninghub.swarm.core.enums.State;
import fr.cgi.learninghub.swarm.core.enums.Type;
import fr.cgi.learninghub.swarm.exception.CreateServiceException;
import fr.cgi.learninghub.swarm.entity.Service;
import fr.cgi.learninghub.swarm.exception.ENTGetStructuresException;
import fr.cgi.learninghub.swarm.exception.ENTGetUsersInfosException;
import fr.cgi.learninghub.swarm.exception.ListServiceException;
import fr.cgi.learninghub.swarm.model.*;
import fr.cgi.learninghub.swarm.repository.ServiceRepository;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.*;

import org.jboss.logging.Logger;

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
                    if (structures != null && !structures.isEmpty()) {
                        students = students.stream().filter(student -> structures.contains(student.getStructure())).toList();
                    }
                    if (classes != null && !classes.isEmpty()) {
                        students = students.stream().filter(student -> classes.stream().anyMatch(student.getClassIds()::contains)).toList();
                    }
//                if (groups != null && !groups.isEmpty()) {
//                    students = students.stream().filter(student -> groups.stream().anyMatch(student.getGroupIds()::contains)).toList();
//                }
                    List<String> usersIds = students.stream().map(User::getId).toList();
                    return getServicesFromFilteredUsers(search, types, order, page, limit, usersIds, students, userInfos);
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

    public Uni<Void> create(CreateServiceBody createServiceBody) {
        return userService.getAllUsers(Profile.STUDENT)
            .chain(users -> Uni.createFrom().item(createServicesObjects(users, createServiceBody)))
            .chain(this.serviceRepository::create)
            .onFailure().recoverWithUni(err -> {
                log.error(String.format("[SwarmApi@ServiceService::%s] Failed to create service in database : %s", this.getClass().getSimpleName(), err.getMessage()));
                return Uni.createFrom().failure(new CreateServiceException());
            });
    }

    // Utils

    private Uni<ResponseListService> getServicesFromFilteredUsers(String search, List<Type> types, Order order, int page, int limit,
                                    List<String> usersIds, List<User> students, UserInfos userInfos) {
        return this.serviceRepository.listAllWithFilterForCount(usersIds, search, types, order)
            .chain(servicesForCount -> {
                List<String> finalUsersIds = servicesForCount.stream().map(Service::getUserId).distinct().toList();
                int totalUsers = finalUsersIds.size();
                return buildResponseListService(order, page, limit, finalUsersIds, students, userInfos, totalUsers);
            })
            .onFailure().recoverWithUni(err -> {
                log.error(String.format("[SwarmApi@%s::getServicesFromFilteredUsers] Failed to filter userIds from database : %s", this.getClass().getSimpleName(), err.getMessage()));
                return Uni.createFrom().failure(new ListServiceException());
            });
    }

    private Uni<ResponseListService> buildResponseListService(Order order, int page, int limit, List<String> usersIds, List<User> students, UserInfos userInfos, int totalUsers) {
        return this.serviceRepository.listAllWithFilterAndLimit(usersIds, order, page, limit)
            .chain(services -> {
                List<ResponseListServiceUser> users = new ArrayList<>();
                usersIds.forEach(userId -> {
                    List<Service> userServices = services.stream().filter(service -> service.getUserId().equals(userId)).toList();
                    List<ClassInfos> userClasses = students.stream()
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
                        .setTotalUsers((long) totalUsers)
                        .setUsers(users);

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
                String id = UUID.randomUUID().toString(); // We generate it ourselves for serviceName (instead of letting it being auto)
                service.setId(id)
                    .setUserId(user.getId())
                    .setFirstName(user.getFirstName())
                    .setLastName(user.getLastName())
                    .setServiceName(String.format("/%s-%s", PathType.getValue(type), id))
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
}
