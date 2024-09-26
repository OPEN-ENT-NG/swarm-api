package fr.cgi.learninghub.swarm.resource;

import fr.cgi.learning.hub.swarm.common.entities.Service;
import fr.cgi.learning.hub.swarm.common.enums.Type;
import fr.cgi.learninghub.swarm.core.enums.Order;
import fr.cgi.learninghub.swarm.exception.CreateServiceBadRequestException;
import fr.cgi.learninghub.swarm.model.CreateServiceBody;
import fr.cgi.learninghub.swarm.model.DeleteServiceBody;
import fr.cgi.learninghub.swarm.model.ResponseListService;
import fr.cgi.learninghub.swarm.model.UpdateServiceBody;
import fr.cgi.learninghub.swarm.service.ServiceService;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
    public Uni<ResponseListService> list(@Parameter(description = "Filter structures") @QueryParam("structures") List<String> structures,
                                         @Parameter(description = "Filter classes") @QueryParam("classes") List<String> classes,
                                         @Parameter(description = "Filter groups") @QueryParam("groups") List<String> groups,
                                         @Parameter(description = "Search keywords") @QueryParam("search") String search,
                                         @Parameter(description = "Filter types of service") @QueryParam("types") List<Type> types,
                                         @Parameter(description = "Names order of the results") @QueryParam("order") Order order,
                                         @Parameter(description = "Number of the requested page") @QueryParam("page") Integer page,
                                         @Parameter(description = "Number of element on the requested page") @QueryParam("limit") Integer limit) {
        if (search == null) search = "";
        if (types == null || types.isEmpty()) types = Arrays.asList(Type.PRESTASHOP, Type.WORDPRESS);
        if (order == null) order = Order.ASCENDANT;
        if (page == null || page < 1) page = 1;
        if (limit == null || limit < 1) limit = 25;

        return serviceService.listAllAndFilter(structures, classes, groups, search, types, order, page, limit);
    }

    @POST
    @Transactional
    @Operation(summary = "Create service", description = "Create a new service in the database")
    @APIResponse(responseCode = "400", description = "Wrong values given for services to create")
    @APIResponse(responseCode = "204", description = "Service successfully created")
    public Uni<List<Service>> create(CreateServiceBody createServiceBody) {
        Date now = new Date();
        if (now.after(createServiceBody.getDeletionDate()))
            return Uni.createFrom().failure(new CreateServiceBadRequestException());
        return serviceService.create(createServiceBody);
    }

    @DELETE
    @Operation(summary = "Delete service", description = "Delete a service in the database")
    @APIResponse(responseCode = "200", description = "Service successfully deleted")
    @APIResponse(responseCode = "400", description = "Wrong values given to delete services")
    @APIResponse(responseCode = "500", description = "Internal server error")
    public Uni<Integer> delete(@Valid DeleteServiceBody deleteServiceBody) {
        return serviceService.delete(deleteServiceBody);
    }

    @PUT
    @Operation(summary = "Update service", description = "Update a service in the database")
    @APIResponse(responseCode = "201", description = "Service successfully updated")
    @APIResponse(responseCode = "400", description = "Wrong values given to update services")
    @APIResponse(responseCode = "500", description = "Internal server error")
    public Response update(UpdateServiceBody updateServiceBody) {
        return Response.status(Response.Status.CREATED).build();
    }

    @PATCH
    @Path("/reset")
    @Operation(summary = "Reset service", description = "Reset a service in the database")
    @APIResponse(responseCode = "201", description = "Service successfully reset")
    @APIResponse(responseCode = "400", description = "Wrong values given to reset services")
    @APIResponse(responseCode = "500", description = "Internal server error")
    public Response reset(@Valid UpdateServiceBody updateServiceBody) {
        return Response.status(Response.Status.CREATED).build();
    }

    @PATCH
    @Operation(summary = "Patch status service", description = "Update a service status in the database")
    @APIResponse(responseCode = "201", description = "Service status successfully updated")
    @APIResponse(responseCode = "400", description = "Wrong values given to update services status")
    @APIResponse(responseCode = "500", description = "Internal server error")
    public Response patchStatus(UpdateServiceBody updateServiceBody) {
        return Response.status(Response.Status.CREATED).build();
    }

    @POST
    @Path("/emails")
    @Operation(summary = "Distribute mails", description = "Distribute mails to users")
    @APIResponse(responseCode = "201", description = "Mails successfully sent")
    @APIResponse(responseCode = "400", description = "Wrong values given to send mails")
    @APIResponse(responseCode = "500", description = "Internal server error")
    public Response distributeMails(UpdateServiceBody updateServiceBody) {
        return Response.status(Response.Status.CREATED).build();
    }

}
