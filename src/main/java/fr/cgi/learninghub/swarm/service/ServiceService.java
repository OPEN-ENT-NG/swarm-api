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
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;

@ApplicationScoped
public class ServiceService {

    private static final Logger log = Logger.getLogger(ServiceService.class);

    @Inject
    ServiceRepository serviceRepository;

    @Inject
    UserEntService userEntService;

    @ConfigProperty(name = "mail.domain")
    String mailDomain;

    @ConfigProperty(name = "host")
    String host;
    @Inject
    MailService mailService;

    // Functions

    public Uni<ResponseListService> listAllAndFilter(List<String> structures, List<String> classes, List<String> groups,
                                                     String search, List<Type> types, Order order, int page, int limit) {
        return userEntService.fetchMyUserInfo()
                .chain(userInfos -> userEntService.getUsersByUais(userInfos.getStructuresUais(), Profile.STUDENT)
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
        return userEntService.getAllUsers(Profile.STUDENT)
                .chain(users -> Uni.createFrom().item(createServicesObjects(users, createServiceBody)))
                .chain(serviceRepository::create)
                .chain(services -> {
                    services.forEach(service -> {
                        String path = String.format("%s%s-%s", host, service.getServiceName(), service.getId());
                        service.setServiceName(path);
                    });
                    return serviceRepository.patchServiceName(services);
                })
                .onFailure().recoverWithUni(err -> {
                    log.error(String.format("[SwarmApi@%s::create] Failed to create service in database : %s", this.getClass().getSimpleName(), err.getMessage()));
                    return Uni.createFrom().failure(new CreateServiceException());
                });
    }

    public Uni<Void> update(List<UpdateServiceBody> updateServiceBodyList) {
        Uni<Void> sequence = Uni.createFrom().voidItem();

        for (UpdateServiceBody updateServiceBody : updateServiceBodyList) {
            sequence = sequence.chain(() -> serviceRepository.update(updateServiceBody.getServicesIds(), updateServiceBody.getDeletionDate()))
                    .replaceWithVoid();
        }

        return sequence
                .onFailure().recoverWithUni(err -> {
                    log.error(String.format("[SwarmApi@%s::update] Failed to update services in database : %s", this.getClass().getSimpleName(), err.getMessage()));
                    return Uni.createFrom().failure(new DeleteServiceException());
                });
    }

    public Uni<Integer> reset(ResetServiceBody resetServiceBody) {
        return serviceRepository.reset(resetServiceBody.getServicesIds(), resetServiceBody.getDeletionDate())
                .onFailure().recoverWithUni(err -> {
                    log.error(String.format("[SwarmApi@%s::update] Failed to update services in database : %s", this.getClass().getSimpleName(), err.getMessage()));
                    return Uni.createFrom().failure(new DeleteServiceException());
                });
    }

    public Uni<Void> patchState(List<PatchStateServiceBody> patchStateServiceBodyList) {
        Uni<Void> sequence = Uni.createFrom().voidItem(); // Initial empty Uni

        for (PatchStateServiceBody patchStateServiceBody : patchStateServiceBodyList) {
            sequence = sequence.chain(() -> serviceRepository.patchState(patchStateServiceBody.getServicesIds(), patchStateServiceBody.getState()))
                    .replaceWithVoid(); // Transform the result to Uni<Void>
        }

        return sequence
                .onFailure().recoverWithUni(err -> {
                    log.error(String.format("[SwarmApi@%s::patchState] Failed to patch state in database : %s", this.getClass().getSimpleName(), err.getMessage()));
                    return Uni.createFrom().failure(new DeleteServiceException());
                });
    }


    public Uni<Integer> delete(DeleteServiceBody deleteServiceBody) {
        return serviceRepository.delete(deleteServiceBody.getServicesIds())
                .onFailure().recoverWithUni(err -> {
                    log.error(String.format("[SwarmApi@%s::delete] Failed to delete services in database : %s", this.getClass().getSimpleName(), err.getMessage()));
                    return Uni.createFrom().failure(new DeleteServiceException());
                });
    }

    public Uni<Void> distribute(DistributeServiceBody distributeServiceBody) {
        return serviceRepository.listByIds(distributeServiceBody.getServicesIds())
                .onItem().transformToMulti(services -> Multi.createFrom().iterable(services)) // Convert list to Multi
                .onItem().transformToUniAndConcatenate(this::distributeMail) // Sequential processing of services
                .collect().asList()
                .replaceWithVoid();
    }

    private Uni<Void> distributeMail(Service service) {
        MailBody mailBody = createMailBody(service);
        return sendEmail(mailBody); // Process email sending as a Uni
    }

    private MailBody createMailBody(Service service) {
        String subject = "Notification déploiement service";
        String content = String.format(
                "Bonjour %s %s,\n\nVous êtes notifié à propos du service suivant: %s\nDate: %s\n\nEmail: %s\n",
                service.getFirstName(), service.getLastName(), service.getServiceName(), LocalDate.now(), service.getMail()
        );

        return new MailBody()
                .setTo("sofianebernoussi@gmail.com")
                .setSubject(subject)
                .setContent(content);
    }

    private Uni<Void> sendEmail(MailBody mailBody) {
        return mailService.send(mailBody)
                .onItem().invoke(() -> {})
                .onFailure().invoke(failure -> log.error(String.format("[SwarmApi@%s::distribute] Failed to distribute mails : %s", this.getClass().getSimpleName(), failure.getMessage())));
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
        List<String> failedEmails = new ArrayList<>();

        filteredUsers.stream().forEach(user -> {
            types.stream().forEach(type -> {
                Service service = new Service();
                String email = String.format("%s@%s", user.getId(), mailDomain);
                try {
                    if (isValidUrl(host)) {
                        service.setUserId(user.getId())
                                .setFirstName(user.getFirstName())
                                .setLastName(user.getLastName())
                                .setServiceName(PathType.getValue(type))
                                .setStructureId(user.getStructure())
                                .setType(type)
                                .setMail(email)
                                .setDeletionDate(createServiceBody.getDeletionDate())
                                .setState(State.SCHEDULED);
                        services.add(service);
                    } else {
                        throw new InvalidMailException();
                    }
                } catch (InvalidMailException e) {
                    failedEmails.add(email);
                }
            });
        });

        // Log all failed emails
        if (!failedEmails.isEmpty()) {
            log.error(String.format("[SwarmApi@%s::createServicesObjects] Failed to send mail for the following emails: %s",
                    this.getClass().getSimpleName(), String.join(", ", failedEmails)));
        }

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

    private static boolean isValidUrl(String url) {
        // Define the regular expression to match a valid URL (end with /, can be http(s)
        final String URL_REGEX = "^https?:\\/\\/[^\s\\/$.?#].[^\s]*\\/$";
        Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

        if (url == null || url.isBlank()) {
            return false;
        }

        return URL_PATTERN.matcher(url).matches();
    }

}
