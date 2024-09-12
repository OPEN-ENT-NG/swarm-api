package fr.cgi.learninghub.swarm.resource;

import fr.cgi.learninghub.swarm.entity.Service;
import fr.cgi.learninghub.swarm.repository.ServiceRepository;
import fr.cgi.learninghub.swarm.service.ServiceService;
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

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

@Path("/services")
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
@Tag(name = "ServiceResource", description = "This API is used to manage service entities")
@OpenAPIDefinition(
    info = @Info(
        title = "ServiceResource API",
        version = "1.0.0",
        description = "This API is used to manage service entities"
    )
)
public class ServiceResource {

    private static final Logger log = Logger.getLogger(ServiceResource.class);
    private final ServiceService serviceService;

    @Inject
    public ServiceResource(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @GET
    public Uni<Response> list() {
        Service service = new Service();
        service.setId(Long.valueOf(1));
        service.setServiceName("Example Service");

        List<Service> services = new ArrayList<>();
        services.add(service);

        return Uni.createFrom().item(() -> Response.ok(services).build());
    }

    // @GET
    // public Uni<List<Service>> list() {
    //     return serviceService.listAll();
    // }
}
