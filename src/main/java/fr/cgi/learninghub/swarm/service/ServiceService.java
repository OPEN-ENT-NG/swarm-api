package fr.cgi.learninghub.swarm.service;

import fr.cgi.learning.hub.swarm.common.entities.Service;
import fr.cgi.learning.hub.swarm.common.enums.PathType;
import fr.cgi.learning.hub.swarm.common.enums.State;
import fr.cgi.learning.hub.swarm.common.enums.Type;
import fr.cgi.learninghub.swarm.config.AppConfig;
import fr.cgi.learninghub.swarm.core.constants.Prestashop;
import fr.cgi.learninghub.swarm.core.constants.Wordpress;
import fr.cgi.learninghub.swarm.core.enums.Order;
import fr.cgi.learninghub.swarm.exception.*;
import fr.cgi.learninghub.swarm.model.*;
import fr.cgi.learninghub.swarm.repository.ServiceRepository;
import fr.cgi.learninghub.swarm.utils.DateUtils;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static fr.cgi.learninghub.swarm.utils.StringUtils.capitalizeOnlyFirstLetter;

@ApplicationScoped
public class ServiceService {

    private static final Logger log = Logger.getLogger(ServiceService.class);

    @Inject
    ServiceRepository serviceRepository;

    @Inject
    UserEntService userEntService;

    @Inject
    MailService mailService;

    @Inject
    AppConfig appConfig;

    @Inject
    Template DistributeMailTemplate;

    // Functions

    public Uni<ResponseListService> listAllAndFilter(List<String> queryStructures, List<String> queryClasses,
                                                     String search, List<Type> types, Order order, int page, int limit) {
        return userEntService.fetchMyUserInfo()
            .chain(userInfos -> userEntService.listGlobalUsersInfo()
                .chain(students -> {
                    // Filtrage structures/classes
                    List<User> filteredStudents = students;
                    if (queryStructures != null && !queryStructures.isEmpty()) {
                        filteredStudents = filteredStudents.stream()
                            .filter(student -> student.getStructures().stream().map(StructureInfos::getId).anyMatch(queryStructures::contains))
                            .collect(Collectors.toList());
                    }
                    if (queryClasses != null && !queryClasses.isEmpty()) {
                        filteredStudents = filteredStudents.stream()
                            .filter(student -> student.getClasses().stream().map(ClassInfos::getId).anyMatch(queryClasses::contains))
                            .collect(Collectors.toList());
                    }

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
        return userEntService.listGlobalUsersInfo()
                .chain(users -> filterUserServices(users, createServiceBody.getClasses(), createServiceBody.getUsers()))
                .chain(users -> buildUserServices(createServiceBody, users))
                .chain(this::createServices)
                .chain(this::patchServicesName)
                .onFailure().recoverWithUni(err -> {
                    log.error(String.format("[SwarmApi@%s::create] Failed to create service in database : %s", this.getClass().getSimpleName(), err.getMessage()));
                    return Uni.createFrom().failure(new CreateServiceException());
                });
    }

    private Uni<List<Service>> createServices(List<Service> services) {
        return Multi.createFrom().iterable(services)
                .onItem()
                .transformToUniAndConcatenate(this::createService)
                .collect().asList();
    }

    private Uni<Service> createService(Service service) {
        return serviceRepository.findUserService(service)
                .flatMap(foundService -> foundService == null
                        ? serviceRepository.persistAndFlush(service)
                        : Uni.createFrom().nullItem());
    }


    private Uni<List<Service>> patchServicesName(List<Service> services) {
        return Multi.createFrom().iterable(services)
                .onItem()
                .transformToUniAndConcatenate(service -> this.updateServiceName(service, getServiceName(service)))
                .collect().asList();
    }

    private String getServiceName(Service service) {
        return String.format("%s-%s", service.getServiceName(), service.getId());
    }

    public Uni<Service> updateServiceName(Service service, String serviceName) {
        return serviceRepository.updateServiceName(service, serviceName);
    }

    private Uni<List<User>> filterUserServices(List<User> users, List<String> classIds, List<String> userIds) {
        List<User> filteredUsers = users.stream()
                .filter(user -> userIds.contains(user.getId()) ||
                        (classIds != null && !classIds.isEmpty() && user.getClasses().stream()
                                .filter(Objects::nonNull)
                                .map(ClassInfos::getId)
                                .anyMatch(classIds::contains)))
                .distinct()
                .collect(Collectors.toList());
        return Uni.createFrom().item(filteredUsers);
    }


    public Uni<Void> update(List<UpdateServiceBody> updateServiceBodyList) {
        return userEntService.fetchMyUserInfo()
                .chain(user -> Multi.createFrom().iterable(updateServiceBodyList)
                        .onItem()
                        .transformToUniAndConcatenate(updateServiceBody ->
                                serviceRepository.update(updateServiceBody.getServicesIds(), updateServiceBody.getDeletionDate(), user.getStructuresIds())
                        )
                        .collect().asList()
                        .replaceWithVoid()
                )
                .onFailure().recoverWithUni(err -> {
                    log.error(String.format("[SwarmApi@%s::update] Failed to update services in database : %s", this.getClass().getSimpleName(), err.getMessage()));
                    return Uni.createFrom().failure(new DeleteServiceException());
                });
    }


    public Uni<Void> reset(ResetServiceBody resetServiceBody) {
        return userEntService.fetchMyUserInfo()
                .chain(user -> serviceRepository.reset(resetServiceBody.getServicesIds(), resetServiceBody.getDeletionDate(), user.getStructuresIds()))
                .onItem().transformToUni(count -> Uni.createFrom().voidItem())
                .onFailure().recoverWithUni(err -> {
                    log.error(String.format("[SwarmApi@%s::update] Failed to update services in database : %s", this.getClass().getSimpleName(), err.getMessage()));
                    return Uni.createFrom().failure(new DeleteServiceException());
                });
    }

    public Uni<Void> patchState(List<PatchStateServiceBody> patchStateServiceBodyList) {
        return userEntService.fetchMyUserInfo()
                .chain(user -> Multi.createFrom().iterable(patchStateServiceBodyList)
                        .onItem()
                        .transformToUniAndConcatenate(patchStateServiceBody ->
                                serviceRepository.patchState(patchStateServiceBody.getServicesIds(), patchStateServiceBody.getState(), user.getStructuresIds())
                        )
                        .collect().asList()
                        .replaceWithVoid()
                )
                .onFailure().recoverWithUni(err -> {
                    log.error(String.format("[SwarmApi@%s::patchState] Failed to patch state in database : %s", this.getClass().getSimpleName(), err.getMessage()));
                    return Uni.createFrom().failure(new DeleteServiceException());
                });
    }


    public Uni<Void> delete(DeleteServiceBody deleteServiceBody) {
        return userEntService.fetchMyUserInfo()
                .chain(user -> serviceRepository.delete(deleteServiceBody.getServicesIds(), user.getStructuresIds())
                        .onItem().transformToUni(count -> Uni.createFrom().voidItem())
                        .onFailure().recoverWithUni(err -> {
                            log.error(String.format("[SwarmApi@%s::delete] Failed to delete services in database : %s", this.getClass().getSimpleName(), err.getMessage()));
                            return Uni.createFrom().failure(new DeleteServiceException());
                        }));
    }

    public Uni<Void> distribute(DistributeServiceBody distributeServiceBody) {
        return userEntService.fetchMyUserInfo()
                .chain(user -> serviceRepository.listByIds(distributeServiceBody.getServicesIds(), user.getStructuresIds()))
                .onItem().transformToMulti(services -> Multi.createFrom().iterable(services))
                .onItem().transformToUniAndConcatenate(this::distributeMail)
                .collect().asList()
                .replaceWithVoid();
    }

    private Uni<Void> distributeMail(Service service) {
        MailBody mailBody = createMailBody(service);
        return sendEmail(mailBody); // Process email sending as a Uni
    }

    private MailBody createMailBody(Service service) {
        String subject = "Notification déploiement service";
        String servicePath = String.format("%s%s", appConfig.getHost(), service.getServiceName());

        TemplateInstance template = DistributeMailTemplate
                .data("serviceType", capitalizeOnlyFirstLetter(service.getType().getValue()))
                .data("serviceUrl", servicePath)
                .data("serviceDeletionDate", DateUtils.formatDate(service.getDeletionDate()));

        String suffix = service.getType() == Type.PRESTASHOP ? Prestashop.ADMIN_PANEL : Wordpress.ADMIN_PANEL;
        String serviceAdminPath = String.format("%s%s/%s", appConfig.getHost(), service.getServiceName(), suffix);
        template = template
                .data("serviceAdminUrl", serviceAdminPath)
                .data("serviceUser", service.getAdminUser())
                .data("servicePassword", service.getAdminPassword());

        String content = template.render();

        return new MailBody()
                .setTo(service.getMail())
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

        return serviceRepository.listAllWithFilter(usersIds, search, types)
                .chain(services -> {
                    // Calculate total users
                    List<String> finalUsersIds = services.stream().map(Service::getUserId).distinct().toList();
                    int totalUsers = finalUsersIds.size();

                    return setResponseListServiceGlobalInfos(students, userInfos, totalUsers)
                            .chain(responseListServiceGlobalInfos -> buildResponseListService(order, page, limit, finalUsersIds, filteredStudents, responseListServiceGlobalInfos))
                            .onFailure().recoverWithUni(err -> {
                                String errorMessage = "[SwarmApi@%s::getServicesFromFilteredUsers] Failed to create responseListServiceGlobalInfos : %s";
                                log.error(String.format(errorMessage, this.getClass().getSimpleName(), err.getMessage()));
                                return Uni.createFrom().failure(new ListServiceException());
                            });
                })
                .onFailure().recoverWithUni(err -> {
                    String errorMessage = "[SwarmApi@%s::getServicesFromFilteredUsers] Failed to filter userIds from database : %s";
                    log.error(String.format(errorMessage, this.getClass().getSimpleName(), err.getMessage()));
                    return Uni.createFrom().failure(new ListServiceException());
                });
    }

    private Uni<ResponseListService> buildResponseListService(Order order, int page, int limit, List<String> usersIds, List<User> filteredStudents, ResponseListServiceGlobalInfos responseListServiceGlobalInfos) {
        return serviceRepository.listByUserIdsAndSort(usersIds, order)
            .chain(services -> {
                List<ResponseListServiceUser> users = new ArrayList<>();
                List<String> usersIdsFromFilteredServices = applyPagination(services, page, limit);
                usersIdsFromFilteredServices.forEach(userId -> {
                    List<Service> userServices = services.stream()
                            .filter(service -> service.getUserId().equals(userId))
                            .peek(service -> {
                                String path = String.format("%s%s", appConfig.getHost(), service.getServiceName());
                                if (validateUrl(path)) {
                                    service.setServiceName(path);
                                } else {
                                    throw new IllegalArgumentException("Invalid service path URL: " + path);
                                }
                            }).toList();

                    User currentStudent = filteredStudents.stream()
                            .filter(student -> student.getId().equals(userId))
                            .findFirst()
                            .orElse(null);

                    ResponseListServiceUser user = new ResponseListServiceUser()
                            .setStructures(currentStudent != null ? currentStudent.getStructures() : new ArrayList<>())
                            .setClasses(currentStudent != null ? currentStudent.getClasses() : new ArrayList<>())
                            .setServices(userServices);
                    users.add(user);
                });

                ResponseListService responseListService = new ResponseListService()
                        .setGlobalInfos(responseListServiceGlobalInfos)
                        .setFilteredUsers(users);

                return Uni.createFrom().item(responseListService);
            })
            .onFailure().recoverWithUni(err -> {
                String errorMessage = "[SwarmApi@%s::buildResponseListService] Failed to list services from database : %s";
                log.error(String.format(errorMessage, this.getClass().getSimpleName(), err.getMessage()));
                return Uni.createFrom().failure(new ListServiceException());
            });
    }

    public Uni<List<Service>> buildUserServices(CreateServiceBody createServiceBody, List<User> users) {
        List<Service> services = new ArrayList<>();
        List<Type> types = createServiceBody.getTypes();

        users.forEach(user -> types.forEach(type -> {
            Service service = new Service();

            String classId = Optional.ofNullable(user.getClasses())
                    .filter(classes -> !classes.isEmpty())
                    .map(List::getFirst) // Si l'utilisateur a plusieurs classes, on prend la premiere. (NORMALEMENT CAS IMPOSSIBLE POUR LES ÉLÈVES)
                    .map(ClassInfos::getId)
                    .orElse(null);

            String structureId = Optional.ofNullable(user.getStructures())
                    .filter(structures -> !structures.isEmpty())
                    .map(List::getFirst) // Si l'utilisateur a plusieurs structures, on prend la premiere. (NORMALEMENT CAS IMPOSSIBLE POUR LES ÉLÈVES)
                    .map(StructureInfos::getId)
                    .orElse(null);

            service.setUserId(user.getId())
                    .setFirstName(user.getFirstName())
                    .setLastName(user.getLastName())
                    .setServiceName(PathType.getValue(type))
                    .setStructureId(structureId)
                    .setType(type)
                    .setMail(user.getMail())
                    .setClassId(classId)
                    .setDeletionDate(createServiceBody.getDeletionDate())
                    .setState(State.SCHEDULED);
            services.add(service);

        }));

        return Uni.createFrom().item(services);
    }


    private Uni<ResponseListServiceGlobalInfos> setResponseListServiceGlobalInfos(List<User> students, UserInfos userInfos, int totalUsers) {
        return serviceRepository.listAll()
                .chain(filteredServices -> filterStudentsByServices(students, filteredServices))
                .chain(filteredStudents -> createResponse(filteredStudents, userInfos, totalUsers))
                .onFailure().recoverWithUni(err -> {
                    log.error(String.format("[SwarmApi@%s::setResponseListServiceGlobalInfos] Failed to list all services from database: %s", this.getClass().getSimpleName(), err.getMessage()));
                    return Uni.createFrom().failure(new ListServiceException());
                });
    }

    private Uni<List<User>> filterStudentsByServices(List<User> students, List<Service> filteredServices) {
        List<String> serviceUserIds = filteredServices.stream()
                .map(Service::getUserId)
                .toList();

        List<User> filteredStudents = students.stream()
                .filter(student -> serviceUserIds.contains(student.getId()))
                .toList();

        return Uni.createFrom().item(filteredStudents);
    }

    private Uni<ResponseListServiceGlobalInfos> createResponse(List<User> filteredStudents, UserInfos userInfos, int totalUsers) {
        List<String> structureIds = filteredStudents.stream()
                .flatMap(student -> student.getStructures().stream())
                .map(StructureInfos::getId)
                .distinct()
                .toList();

        List<StructureInfos> structureInfos = userInfos.getStructures().stream()
                .filter(structure -> structureIds.contains(structure.getId()))
                .distinct()
                .toList();

        List<ClassInfos> classInfos = filteredStudents.stream()
                .flatMap(student -> student.getClasses().stream())
                .distinct()
                .toList();

        ResponseListServiceGlobalInfos response = new ResponseListServiceGlobalInfos()
                .setTotalUsers((long) totalUsers)
                .setStructures(structureInfos)
                .setClasses(classInfos)
                .setUsers(filteredStudents);

        return Uni.createFrom().item(response);
    }


    private List<String> applyPagination(List<Service> services, int page, int limit) {
        return services.stream()
                .map(Service::getUserId)
                .distinct()
                .skip((long) (page - 1) * limit)
                .limit(limit)
                .toList();
    }


    private static boolean validateUrl(String url) {
        final String URL_REGEX = "^https?:\\/\\/[^\s\\/$.?#].[^\s]*$";
        Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

        if (url == null || url.isBlank()) {
            return false;
        }

        return URL_PATTERN.matcher(url).matches();
    }
}
