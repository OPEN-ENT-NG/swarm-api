package fr.cgi.learninghub.swarm.clients;

import fr.cgi.learninghub.swarm.model.User;
import io.quarkus.rest.client.reactive.ClientBasicAuth;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

import static fr.cgi.learninghub.swarm.core.constants.Fields.*;

@Path("/swarm")
@RegisterRestClient(configKey = "swarm-connector-client")
@ClientBasicAuth(username = "${app.rest-client.swarm-connector-client.username}", password = "${app.rest-client.swarm-connector-client.password}")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface EntSwarmConnectorClient {
    @GET
    @Path("/users")
    Uni<List<User>> getUsersByMefId(@QueryParam(USER_ID) String userId,
                                    @QueryParam(MEF_IDS) List<String> mefIds,
                                    @QueryParam(ADD_MYSELF) Boolean addMyself);
}