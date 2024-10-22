package fr.cgi.learninghub.swarm.repository;

import fr.cgi.learning.hub.swarm.common.entities.Service;
import fr.cgi.learning.hub.swarm.common.enums.State;
import fr.cgi.learning.hub.swarm.common.enums.Type;
import fr.cgi.learninghub.swarm.core.enums.Order;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

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
        return find(query, params).project(Service.class).list();
    }


    public Uni<List<Service>> listByIds(List<String> ids, List<String> structuresIds) {
        return list("id IN ?1 AND structureId IN ?2", ids, structuresIds);
    }

    public Uni<List<Service>> listByUserIdsAndSort(List<String> usersIds, Order order) {
        // Sorting params
        Sort.Direction direction = order.getDirection();
        Sort sorting = Sort.by("lastName", direction).and("firstName", direction);

        // Init query filtered by users ids and service types
        String query = "userId IN :usersIds";
        Parameters params = Parameters.with("usersIds", usersIds);

        return find(query, sorting, params).list(); // Execute query and apply pagination
    }

    public Uni<Service> findUserService(Service service) {
        return Service.find("type = ?1 and userId = ?2 and classId = ?3", service.getType(), service.getUserId(), service.getClassId())
                .firstResult();
    }


    @WithTransaction
    public Uni<Service> create(Service service) {
        return persistAndFlush(service);
    }

    @WithTransaction
    public Uni<Service> updateServiceName(Service service, String name) {
        service.setServiceName(name);
        return Panache.withTransaction(() -> persist(service));

    }

    public Uni<Integer> patchState(List<String> serviceIds, State state, List<String> structureIds) {
        String query = "state = :state WHERE id IN :serviceIds AND structureId IN :structureIds";
        Parameters params = Parameters.with("state", state)
                .and("serviceIds", serviceIds)
                .and("structureIds", structureIds);

        return update(query, params);
    }

    public Uni<Integer> update(List<String> serviceIds, Date deletionDate, List<String> structureIds) {
        String query = "deletionDate = :deletionDate WHERE id IN :serviceIds AND structureId IN :structureIds";
        Parameters params = Parameters.with("deletionDate", deletionDate)
                .and("serviceIds", serviceIds)
                .and("structureIds", structureIds);
        return update(query, params);
    }

    public Uni<Integer> delete(List<String> serviceIds, List<String> structureIds) {
        String query = "state = :state WHERE id IN :serviceIds AND structureId IN :structureIds";
        Parameters params = Parameters.with("state", State.DELETION_SCHEDULED)
                .and("serviceIds", serviceIds)
                .and("structureIds", structureIds);
        return update(query, params);
    }

    public Uni<Integer> reset(List<String> serviceIds, Date deletionDate, List<String> structureIds) {
        String query = "state = :state, deletionDate = :deletionDate WHERE id IN :serviceIds AND structureId IN :structureIds";
        Parameters params = Parameters.with("state", State.RESET_SCHEDULED)
                .and("deletionDate", deletionDate)
                .and("serviceIds", serviceIds)
                .and("structureIds", structureIds);

        return update(query, params);
    }
}