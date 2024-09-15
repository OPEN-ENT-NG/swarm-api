package fr.cgi.learninghub.swarm.resource;

import fr.cgi.learninghub.swarm.core.constants.AppConfig;
import io.smallrye.jwt.build.Jwt;
import org.eclipse.microprofile.jwt.JsonWebToken;
import fr.cgi.learninghub.swarm.core.enums.UserServiceName;
import fr.cgi.learninghub.swarm.model.ResponseListUser;
import fr.cgi.learninghub.swarm.service.EntService;
import fr.cgi.learninghub.swarm.service.IUserService;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.ArrayList;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
@Tag(name = "UserResource", description = "This API is used to manage users information")
@OpenAPIDefinition(
    info = @Info(
        title = "UserResource API",
        version = "1.0.0",
        description = "This API is used to manage users information"
    )
)
public class UserResource {

    private static final Logger log = Logger.getLogger(UserResource.class);

    private final IUserService userService;

    @Inject
    JsonWebToken jwt;
    

    @Inject
    public UserResource(EntService entService) {
        this.userService = entService;
    }

    @GET
    @Operation(summary = "List users", description = "Get list of users available to the connected user")
    @APIResponse(responseCode = "200",
                description = "List of users, their classes and groups successfully retrieved",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResponseListUser.class, type = SchemaType.ARRAY)))
    public Uni<ResponseListUser> list() {
        return userService.getAndFilterUsers();
    }

    @GET
    @Path("/test")
    public Uni<String> test() {
        log.info("test: " + jwt.getSubject());
        log.info("testNae: " + jwt.getName());
        log.info("testNae: " + jwt.getName());
        return Uni.createFrom().item("{\"message\":\"OK\"}");
    }
}