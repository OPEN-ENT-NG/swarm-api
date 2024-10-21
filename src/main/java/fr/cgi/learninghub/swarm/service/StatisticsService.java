package fr.cgi.learninghub.swarm.service;

import fr.cgi.learning.hub.swarm.common.enums.State;
import fr.cgi.learning.hub.swarm.common.enums.Type;
import fr.cgi.learninghub.swarm.exception.*;
import fr.cgi.learninghub.swarm.model.*;
import fr.cgi.learninghub.swarm.repository.StatisticsRepository;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ApplicationScoped
public class StatisticsService {

    private static final Logger log = Logger.getLogger(StatisticsService.class);

    @Inject
    StatisticsRepository statisticsRepository;

    @Inject
    UserEntService userEntService;

    // Functions

    public Uni<List<ResponseStatistics>> getAllStatistics() {
        return userEntService.fetchMyUserInfo()
            .chain(userInfos -> userEntService.listGlobalUsersInfo())
            .chain(students -> this.buildResponseStatisticsList(students.stream().map(User::getId).toList()))
            .onFailure().recoverWithUni(err -> {
                String errorMessage = "[SwarmApi@%s::getAllStatistics] Failed to get statistics : %s";
                log.error(String.format(errorMessage, this.getClass().getSimpleName(), err.getMessage()));
                return Uni.createFrom().failure(new GetStatisticsException());
            });
    }

    // Utils

    private Uni<List<ResponseStatistics>> buildResponseStatisticsList(List<String> usersIds) {
        List<Uni<ResponseStatistics>> unisResponseStatistics = new ArrayList<>();
        Arrays.asList(Type.PRESTASHOP, Type.WORDPRESS)
                .forEach(type -> unisResponseStatistics.add(buildResponseStatistics(usersIds, type)));

        return Multi.createFrom().iterable(unisResponseStatistics)
                .onItem().transformToMultiAndConcatenate(Uni::toMulti)
                .collect().asList();
    }

    private Uni<ResponseStatistics> buildResponseStatistics(List<String> usersIds, Type type) {
        ResponseStatistics responseStatistics = new ResponseStatistics(type);
        List<State> createdExcludedStates = Arrays.asList(State.DEPLOYMENT_IN_ERROR, State.DELETION_IN_ERROR, State.RESET_IN_ERROR, State.DEACTIVATION_IN_ERROR, State.REACTIVATION_IN_ERROR);
        List<State> activeStates = Arrays.asList(State.DEPLOYED, State.DELETION_SCHEDULED, State.DELETION_IN_PROGRESS, State.RESET_SCHEDULED, State.DEACTIVATION_SCHEDULED);
        List<State> inactiveStates = Arrays.asList(State.SCHEDULED, State.IN_PROGRESS, State.RESET_IN_PROGRESS, State.DISABLED, State.REACTIVATION_SCHEDULED, State.REACTIVATION_IN_PROGRESS);
        LocalDateTime limitDate = LocalDateTime.now().plusDays(4);

        List<Uni<Long>> statsUnis = new ArrayList<>();
        statsUnis.add(statisticsRepository.listServicesByStatesAndUsersIds(usersIds, type, null, createdExcludedStates));
        statsUnis.add(statisticsRepository.listServicesByStatesAndUsersIds(usersIds, type, activeStates, null));
        statsUnis.add(statisticsRepository.listServicesByStatesAndUsersIds(usersIds, type, inactiveStates, null));
        statsUnis.add(statisticsRepository.listDeletedSoonServices(usersIds, type, limitDate));

        return Multi.createFrom().iterable(statsUnis)
                .onItem().transformToMultiAndConcatenate(Uni::toMulti)
                .collect().asList()
                .chain(stats -> {
                    if (stats.size() == 4) {
                        responseStatistics.setNbCreatedService(stats.get(0));
                        responseStatistics.setNbActiveService(stats.get(1));
                        responseStatistics.setNbInactiveService(stats.get(2));
                        responseStatistics.setNbDeletedSoonService(stats.get(3));
                    } else {
                        String errorMessage = "[SwarmApi@%s::buildResponseStatistics] Unexpected number of statistics: expected 4 but got %s";
                        log.error(String.format(errorMessage, this.getClass().getSimpleName(), stats.size()));
                    }
                    return Uni.createFrom().item(responseStatistics);
                });
    }
}
