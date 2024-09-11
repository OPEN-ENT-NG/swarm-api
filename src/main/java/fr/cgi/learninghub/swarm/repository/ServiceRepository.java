package fr.cgi.learninghub.swarm.repository;

import java.util.List;

import fr.cgi.learninghub.swarm.entity.Service;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ServiceRepository implements PanacheRepository<Service> {

    @WithSession
    public Uni<List<Service>> listByUsersIds(List<String> userIds) {
        return list("userId IN ?1 ORDER BY userId", userIds);
    }

    @WithSession
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
}