package fr.cgi.learninghub.swarm.clients;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import fr.cgi.learninghub.swarm.model.Infra;

@Path("/infra")
@RegisterRestClient(configKey = "ent-client")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface EntClient {

    @GET
    @Path("/monitoring/db")
    Uni<Infra> getEnts();
}
