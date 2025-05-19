package fr.cgi.learninghub.swarm.clients;

import fr.cgi.learninghub.swarm.model.UserInfos;
import io.quarkus.rest.client.reactive.ClientBasicAuth;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/directory")
@RegisterRestClient(configKey = "ent-client")
@ClientBasicAuth(username = "${app.rest-client.ent-client.username}", password = "${app.rest-client.ent-client.password}")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface EntDirectoryClient {

    @GET
    @Path("/user/{userId}")
    Uni<UserInfos> getUserInfos(@PathParam("userId") String userId);

}