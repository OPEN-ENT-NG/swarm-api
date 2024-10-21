package fr.cgi.learninghub.swarm.resource;

import fr.cgi.learninghub.swarm.model.*;
import fr.cgi.learninghub.swarm.service.StatisticsService;
import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import java.util.List;

@Path("/statistics")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("manager")
@Tag(name = "StatisticsResource", description = "This API is used to manage service statistics")
@OpenAPIDefinition(
        info = @Info(
                title = "StatisticsResource API",
                version = "1.0.0",
                description = "This API is used to manage service statistics"
        )
)
public class StatisticsResource {

    private static final Logger log = Logger.getLogger(StatisticsResource.class);

    @Inject
    StatisticsService statisticsService;

    @GET
    @Operation(summary = "List statistics", description = "List and calculate statistics for each service type")
    @APIResponse(responseCode = "500", description = "Internal server error")
    @APIResponse(responseCode = "200",
                description = "List of statistics successfully retrieved and calculated",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseStatistics.class, type = SchemaType.ARRAY)))
    public Uni<List<ResponseStatistics>> list() {
        return statisticsService.getAllStatistics();
    }
}
