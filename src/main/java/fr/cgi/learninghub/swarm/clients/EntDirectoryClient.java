package fr.cgi.learninghub.swarm.clients;

import fr.cgi.learninghub.swarm.core.enums.Profile;
import fr.cgi.learninghub.swarm.model.StructureInfos;
import fr.cgi.learninghub.swarm.model.User;
import fr.cgi.learninghub.swarm.model.UserInfos;
import io.quarkus.rest.client.reactive.ClientBasicAuth;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@Path("/directory")
@RegisterRestClient(configKey = "ent-client")
@ClientBasicAuth(username = "${app.rest-client.ent-client.username}", password = "${app.rest-client.ent-client.password}")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface EntDirectoryClient {

    @GET
    @Path("/user/structures/list")
    Uni<List<User>> listUserInStructuresByUAI(@QueryParam("uai") List<String> uais,
                                            @QueryParam("full") @DefaultValue("true") boolean isFullFormat);

    @GET
    @Path("/structures")
    Uni<List<StructureInfos>> listAllStructures();

    @GET
    @Path("/user/{userId}")
    Uni<UserInfos> getUserInfos(@PathParam("userId") String userId);

    @GET
    @Path("/api/classes")
    Uni<String> listClassesInStructuresByIds(@QueryParam("id") List<String> structureIds);

    @GET
    @Path("/class/{classId}/users")
    Uni<List<User>> getUsersByClass(@PathParam("classId") String classId, @QueryParam("type") String type);

    // @GET
    // @Path("/structure/{structureId}/users")
    // Uni<List<User>> getUsersByStructure(@HeaderParam("Cookie") String sessionId,
    //                                     @PathParam("structureId") String structureId);

    // @GET
    // @Path("/class/{classId}")
    // Uni<List<StudentClass>> getClassInfos(@PathParam("classId") String classId);

    // @GET
    // @Path("/group/{groupId}")
    // Uni<List<StudentGroup>> getGroupInfos(@PathParam("groupId") String groupId);
}