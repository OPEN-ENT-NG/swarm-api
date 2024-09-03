package fr.cgi.learninghub.swarm.resource;

import fr.cgi.learninghub.swarm.model.CreateServiceBody;
import fr.cgi.learninghub.swarm.entity.Service;
import fr.cgi.learninghub.swarm.exception.CreateServiceBadRequestException;
import fr.cgi.learninghub.swarm.service.ServiceService;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

@Path("/services")
@Consumes(MediaType.APPLICATION_JSON)
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
    
    @Inject
    ServiceService serviceService;

    @GET
    public Uni<List<Service>> list() {
        return serviceService.listAll();
    }

    @POST
    @Transactional
    @Operation(summary = "Create service", description = "Create a new service in the databse")
    @APIResponse(responseCode = "400", description = "Wrong values given for services to create")
    @APIResponse(responseCode = "204", description = "Service successfully created")
    public Uni<Void> create(CreateServiceBody createServiceBody) {
        Date now = new Date(); 
        if (now.after(createServiceBody.getDeletionDate())) return Uni.createFrom().failure(new CreateServiceBadRequestException());
        return serviceService.create(createServiceBody);
    }
}
