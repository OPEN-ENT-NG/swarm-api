package fr.cgi.learninghub.swarm.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import fr.cgi.learninghub.swarm.exception.UpdateServiceBadRequestException;
import fr.cgi.learninghub.swarm.model.UpdateServiceBody;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import jakarta.ws.rs.NotFoundException;
import org.jboss.logging.Logger;

import fr.cgi.learninghub.swarm.core.enums.Order;
import fr.cgi.learninghub.swarm.exception.CreateServiceException;
import fr.cgi.learning.hub.swarm.common.enums.Type;
import fr.cgi.learning.hub.swarm.common.enums.PathType;
import fr.cgi.learning.hub.swarm.common.enums.State;
import fr.cgi.learning.hub.swarm.common.entities.Service;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@WithSession
@RegisterForReflection
@ApplicationScoped
public class ServiceRepository implements PanacheRepositoryBase<Service, String> {

    private static final Logger log = Logger.getLogger(ServiceRepository.class);

    public Uni<List<Service>> listAllWithFilter(List<String> usersIds, String search, List<Type> types) {
        // Init query filtered by users ids and service types
        String query = "SELECT DISTINCT s.userId, s.firstName, s.lastName FROM Service s " +
                "WHERE s.userId IN :usersIds AND s.type IN :types ";
        Parameters params = Parameters.with("usersIds", usersIds).and("types", types);

        // Search keywords in firstName and lastName columns
        if (!search.isEmpty()) {
            List<String> searchKeywords = Arrays.stream(search.split(" ")).map(keyword -> "%" + keyword.toLowerCase() + "%").toList();
            query += "AND (";

            for (int i = 0; i < searchKeywords.size(); i++) {
                if (i > 0) query += " OR ";
                query += "(LOWER(s.firstName) LIKE :keyword" + i + " OR LOWER(s.lastName) LIKE :keyword" + i + ")";
                params = params.and("keyword" + i, searchKeywords.get(i));
            }
            query += ") ";
        }

        query += "GROUP BY s.userId, s.firstName, s.lastName";
        return Service.find(query, params).project(Service.class).list();
    }

    public Uni<List<Service>> listAllWithFilterAndLimit(List<String> usersIds, Order order, int page, int limit) {
        // Sorting params
        Sort.Direction direction = order.getDirection();
        Sort sorting = Sort.by("lastName", direction).and("firstName", direction);

        // Init query filtered by users ids and service types
        String query = "SELECT s FROM Service s " +
                "WHERE s.userId IN :usersIds ";
        Parameters params = Parameters.with("usersIds", usersIds);

        return Service.find(query, sorting, params).page(Page.of((page - 1), limit)).list(); // Execute query and apply pagination
    }

    public Uni<List<Service>> listByUsersIds(List<String> userIds) {
        return list("userId IN ?1 ORDER BY userId", userIds);
    }

    public Uni<List<Service>> listByUsersIdsMultiple(List<String> userIds) {
        String query = "SELECT s FROM Service s " +
            "WHERE s.userId IN (" +
                "SELECT s2.userId FROM Service s2 " +
                "WHERE s2.userId IN ?1 " +
                "GROUP BY s2.userId " +
                "HAVING COUNT(DISTINCT s2.type) > 1 " +
            ")";
        return list(query, userIds);
    }

    @Transactional
    public Uni<List<Service>> create(List<Service> services) {
        Uni<List<Service>> sequence = Uni.createFrom().item(List.of()); // Final object we gonna fill

        // We need to treat it sequentially to let database know that an object has been created and letting it created the next id
        for (Service service : services) {
            sequence = sequence
                .chain(currentList -> {
                    return persistAndFlush(service).onItem().transformToUni(persistedService -> {  // Persist the service
                        List<Service> newCurrentList = new ArrayList<>(currentList);
                        newCurrentList.add(persistedService);  // Add the service to the list

                        return Uni.createFrom().item(newCurrentList); // Return the updated list wich gonna replace the final object
                    });
                })
                .onFailure().recoverWithUni(err -> {
                    log.error(String.format("[SwarmApi@%s::updateService] Failed to create service in database : %s", this.getClass().getSimpleName(), err.getMessage()));
                    return Uni.createFrom().failure(new CreateServiceException());
                });
        }

        return sequence;
    }

    @Transactional
    public Uni<Service> create(Service service) {
        return persistAndFlush(service);
    }

    @Transactional
    public Uni<List<Service>> patchServiceName(List<Service> services) {
        List<Uni<Service>> unis = services.stream()
                .map(service -> patchServiceName(service))
                .toList();
        return Uni.combine().all().unis(unis).with(list -> (List<Service>) list);
    }

    @Transactional
    public Uni<Service> patchServiceName(Service service) {
        Parameters params = Parameters.with("id", service.getId()).and("serviceName", service.getServiceName());
        return update("serviceName = :serviceName where id = :id", params).replaceWith(service);
    }

    @Transactional
    public Uni<Integer> patch(UpdateServiceBody updateServiceBody) {
        List<String> servicesIds = updateServiceBody.getServicesIds();
        State state = updateServiceBody.getState();
        Date deletionDate = updateServiceBody.getDeletionDate();

        if (servicesIds.isEmpty() || (state == null && deletionDate == null)) {
            return Uni.createFrom().failure(new UpdateServiceBadRequestException());
        }

        List<String> subQueries = new ArrayList<>();
        Parameters params = Parameters.with("servicesIds", servicesIds);

        if (state != null) {
            subQueries.add("state = :state");
            params.and("state", state);
        }
        if (deletionDate != null) {
            subQueries.add("deletionDate = :deletionDate");
            params.and("deletionDate", deletionDate);
        }

        String query = String.join(" AND ", subQueries);
        return update(query + " WHERE id IN :servicesIds", params);
    }
}