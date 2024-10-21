package fr.cgi.learninghub.swarm.repository;

import fr.cgi.learning.hub.swarm.common.entities.Service;
import fr.cgi.learning.hub.swarm.common.enums.State;
import fr.cgi.learning.hub.swarm.common.enums.Type;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.panache.common.Parameters;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.List;

@WithSession
@RegisterForReflection
@ApplicationScoped
public class StatisticsRepository implements PanacheRepositoryBase<Service, String> {

    private static final Logger log = Logger.getLogger(StatisticsRepository.class);

    public Uni<Long> listServicesByStatesAndUsersIds(List<String> usersIds, Type type, List<State> states, List<State> excludedStates) {
        if (usersIds == null || usersIds.isEmpty()) {
            return Uni.createFrom().item(0L);
        }

        String query = "userId IN :usersIds AND type = :type ";
        Parameters params = Parameters.with("usersIds", usersIds)
                .and("type", type);

        if (states != null && !states.isEmpty()) {
            query += "AND state IN :states ";
            params.and("states", states);
        }

        if (excludedStates != null && !excludedStates.isEmpty()) {
            query += "AND state NOT IN :excludedStates ";
            params.and("excludedStates", excludedStates);
        }

        return count(query, params);
    }

    public Uni<Long> listDeletedSoonServices(List<String> usersIds, Type type, LocalDateTime limitDate) {
        if (usersIds == null || usersIds.isEmpty()) {
            return Uni.createFrom().item(0L);
        }

        String query = "userId IN :usersIds AND type = :type AND deletionDate < :limitDate";
        Parameters params = Parameters.with("usersIds", usersIds)
                .and("type", type)
                .and("limitDate", limitDate);

        return count(query, params);
    }

}