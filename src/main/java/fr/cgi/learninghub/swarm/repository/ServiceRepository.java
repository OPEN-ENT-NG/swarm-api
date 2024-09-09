package fr.cgi.learninghub.swarm.repository;

import java.util.Arrays;
import java.util.List;

import fr.cgi.learninghub.swarm.core.enums.Order;
import fr.cgi.learninghub.swarm.core.enums.Type;
import fr.cgi.learninghub.swarm.entity.Service;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@WithSession
@RegisterForReflection
@ApplicationScoped
public class ServiceRepository implements PanacheRepositoryBase<Service, String> {

    public Uni<List<Service>> listAllWithFilterForCount(List<String> usersIds, String search, List<Type> types, Order order) {
        // Sorting params
        Sort.Direction direction = order.getDirection();
        Sort sorting = Sort.by("lastName", direction).and("firstName", direction);

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
        return Service.find(query, sorting, params).project(Service.class).list();
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

    public Uni<Void> create(List<Service> services) {
        return persist(services)
            .chain(voidItem -> flush())
            .replaceWithVoid();
    }
}