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
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/services")
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
public class ServiceResource {

    private final ServiceRepository serviceRepository;

    @Inject
    public ServiceResource(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @GET
    public Uni<Response> list() {
        Service service = new Service();
        service.setId(Long.valueOf(1));
        service.setName("Example Service");

        List<Service> services = new ArrayList<>();
        services.add(service);

        return Uni.createFrom().item(() -> Response.ok(services).build());
    }

    // @GET
    // public Uni<List<Service>> list() {
    //     return serviceRepository.listAll();
    // }
}
