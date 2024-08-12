package fr.cgi.learninghub.swarm.resource;

import fr.cgi.learninghub.swarm.model.Infra;
import fr.cgi.learninghub.swarm.service.EntService;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;

@Path("/ent")
@Produces(MediaType.APPLICATION_JSON)
public class EntResource {

    private final EntService entService;
    private static final Logger log = Logger.getLogger(EntResource.class);



    @Inject
    public EntResource(EntService entService) {
        this.entService = entService;
    }

    @GET
    @Path("/health")
    public Uni<Infra> health() {
        log.info("EntResource.health()");
        return entService.getHealth();
    }
    
}
