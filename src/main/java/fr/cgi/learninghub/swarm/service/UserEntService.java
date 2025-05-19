package fr.cgi.learninghub.swarm.service;

import fr.cgi.learninghub.swarm.clients.EntDirectoryClient;
import fr.cgi.learninghub.swarm.clients.EntSwarmConnectorClient;
import fr.cgi.learninghub.swarm.config.AppConfig;
import fr.cgi.learninghub.swarm.exception.ENTGetUsersInfosException;
import fr.cgi.learninghub.swarm.model.User;
import fr.cgi.learninghub.swarm.model.UserInfos;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.util.*;


@ApplicationScoped
public class UserEntService {

    private static final Logger log = Logger.getLogger(UserEntService.class);

    @Inject
    AppConfig appConfig;

    @Inject
    @RestClient
    EntDirectoryClient entDirectoryClient;

    @Inject
    @RestClient
    EntSwarmConnectorClient entSwarmConnectorClient;

    @Inject
    JsonWebToken jwt;

    public Uni<List<User>> listGlobalUsersInfo() {
        String userId = jwt.getName();
        return retrieveGlobalUsersInfoAndAddMe(userId)
                .onFailure().recoverWithUni(err -> {
                    String errorMessage = "[SwarmApi@%s::listGlobalUsersInfo] Failed to fetch connected user infos : %s";
                    log.error(String.format(errorMessage, this.getClass().getSimpleName(), err.getMessage()));
                    return Uni.createFrom().failure(new ENTGetUsersInfosException());
                });
    }

    public Uni<List<User>> retrieveGlobalUsersInfoAndAddMe(String userId) {
        return entSwarmConnectorClient.getUsersByMefId(userId, appConfig.getMefIds(), true)
                .onFailure().recoverWithUni(err -> {
                    String errorMessage = "[SwarmApi@%s::retrieveGlobalUsersInfoAndAddMe] Failed to list all user infos for connected user : %s";
                    log.error(String.format(errorMessage, this.getClass().getSimpleName(), err.getMessage()));
                    return Uni.createFrom().failure(new ENTGetUsersInfosException());
                });
    }

    public Uni<UserInfos> fetchMyUserInfo() {
        String userId = jwt.getName(); // or use getClaim() if userId is stored in an attribute
        return entDirectoryClient.getUserInfos(userId);
    }

}
