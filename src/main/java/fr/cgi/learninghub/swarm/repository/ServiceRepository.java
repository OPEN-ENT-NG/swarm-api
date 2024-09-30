package fr.cgi.learninghub.swarm.repository;

import fr.cgi.learning.hub.swarm.common.entities.Service;
import fr.cgi.learning.hub.swarm.common.enums.State;
import fr.cgi.learning.hub.swarm.common.enums.Type;
import fr.cgi.learninghub.swarm.core.enums.Order;
import fr.cgi.learninghub.swarm.exception.CreateServiceException;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@WithSession
@RegisterForReflection
@ApplicationScoped
public class ServiceRepository implements PanacheRepositoryBase<Service, String> {

    private static final Logger log = Logger.getLogger(ServiceRepository.class);

    public Uni<List<Service>> listAllWithFilter(List<String> usersIds, String search, List<Type> types, List<State> hiddenStates) {
        // Init query filtered by users ids and service types
        String query = "SELECT DISTINCT s.userId, s.firstName, s.lastName FROM Service s " +
                "WHERE s.userId IN :usersIds AND s.type IN :types AND state NOT IN :hiddenStates ";
        Parameters params = Parameters.with("usersIds", usersIds).and("types", types).and("hiddenStates", hiddenStates);

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


    public Uni<List<Service>> listByIds(List<String> ids) {
        return list("id IN ?1", ids);
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
        Uni<List<Service>> sequence = Uni.createFrom().item(new ArrayList<>()); // Final object we gonna fill

        for (Service service : services) {
            sequence = sequence
                    .chain(currentList -> checkIfServiceExists(service).flatMap(exists -> {
                        if (exists) {
                            // Skip the existing service and return the current list
                            return Uni.createFrom().item(currentList);
                        } else {
                            return persistAndFlush(service).onItem().transformToUni(persistedService -> {
                                List<Service> newCurrentList = new ArrayList<>(currentList);
                                newCurrentList.add(persistedService);
                                return Uni.createFrom().item(newCurrentList);
                            });
                        }
                    }).onItem().ifNull().continueWith(currentList)); // Ensure we continue with the current list if null
        }

        return sequence
                .onItem().ifNull().continueWith(new ArrayList<>()) // Ensure we return an empty list if no services are added
                .onFailure().recoverWithUni(err -> {
                    log.error(String.format("[SwarmApi@%s::create] Failed to create services in database : %s", this.getClass().getSimpleName(), err.getMessage()));
                    return Uni.createFrom().failure(new CreateServiceException());
                });
    }

    private Uni<Boolean> checkIfServiceExists(Service service) {
        return Service.find("type = ?1 and userId = ?2", service.getType(), service.getUserId())
                .firstResult()
                .map(existingService -> existingService != null);
    }


    @Transactional
    public Uni<Service> create(Service service) {
        return persistAndFlush(service);
    }

    @Transactional
    public Uni<List<Service>> patchServiceName(List<Service> services) {
        if (services.isEmpty()) {
            return Uni.createFrom().item(List.of()); // Return an empty list if services is empty
        }

        List<Uni<Service>> unis = services.stream()
                .map(this::patchServiceName)
                .toList();

        return Uni.combine().all().unis(unis).with(list -> (List<Service>) list);
    }


    @Transactional
    public Uni<Service> patchServiceName(Service service) {
        Parameters params = Parameters.with("id", service.getId()).and("serviceName", service.getServiceName());
        return update("serviceName = :serviceName where id = :id", params).replaceWith(service);
    }

    @Transactional
/*    public Uni<Integer> patch(UpdateServiceBody updateServiceBody) {
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
    }*/

    public Uni<Integer> patchState(List<String> serviceIds, State state) {
        String query = "state = :state WHERE id IN :serviceIds";
        Parameters params = Parameters.with("state", state)
                .and("serviceIds", serviceIds);

        return update(query, params);
    }

    public Uni<Integer> update(List<String> serviceIds, Date deletionDate) {
        String query = "deletionDate = :deletionDate WHERE id IN :serviceIds";
        Parameters params = Parameters.with("deletionDate", deletionDate)
                .and("serviceIds", serviceIds);

        return update(query, params);
    }

    public Uni<Integer> delete(List<String> serviceIds) {
        String query = "state = :state WHERE id IN :serviceIds";
        Parameters params = Parameters.with("state", State.DELETION_SCHEDULED)
                .and("serviceIds", serviceIds);

        return update(query, params);
    }

    public Uni<Integer> reset(List<String> serviceIds, Date deletionDate) {
        String query = "state = :state, deletionDate = :deletionDate WHERE id IN :serviceIds";
        Parameters params = Parameters.with("state", State.RESET_IN_PROGRESS)
                .and("deletionDate", deletionDate)
                .and("serviceIds", serviceIds);

        return update(query, params);
    }

}