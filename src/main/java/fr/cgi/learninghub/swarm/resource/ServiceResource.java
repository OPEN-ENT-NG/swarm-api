package fr.cgi.learninghub.swarm.resource;

import fr.cgi.learninghub.swarm.model.Service;
import fr.cgi.learninghub.swarm.repository.ServiceRepository;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/services")
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
public class ServiceResource {

    @Inject
    ServiceRepository serviceRepository;

    @GET
    public Uni<List<Service>> list() {
        return serviceRepository.listAll();
    }
}
