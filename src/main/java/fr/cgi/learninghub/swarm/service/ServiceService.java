package fr.cgi.learninghub.swarm.service;

import fr.cgi.learninghub.swarm.core.enums.PathType;
import fr.cgi.learninghub.swarm.core.enums.Profile;
import fr.cgi.learninghub.swarm.core.enums.State;
import fr.cgi.learninghub.swarm.core.enums.Type;
import fr.cgi.learninghub.swarm.exception.CreateServiceException;
import fr.cgi.learninghub.swarm.entity.Service;
import fr.cgi.learninghub.swarm.model.CreateServiceBody;
import fr.cgi.learninghub.swarm.model.User;
import fr.cgi.learninghub.swarm.model.ClassInfos;
import fr.cgi.learninghub.swarm.repository.ServiceRepository;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

import org.jboss.logging.Logger;

@ApplicationScoped
public class ServiceService {
    
    private static final Logger log = Logger.getLogger(ServiceService.class);
    
    @Inject
    ServiceRepository serviceRepository;

    @Inject
    IUserService userService;

    // Functions

    public Uni<List<Service>> listAll() {
        return this.serviceRepository.listAll();
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
