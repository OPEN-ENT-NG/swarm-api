package fr.cgi.learninghub.swarm.clients;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import fr.cgi.learninghub.swarm.model.Group;
import fr.cgi.learninghub.swarm.model.Class;
import fr.cgi.learninghub.swarm.model.User;

@Path("/directory")
@RegisterRestClient(configKey = "ent-client")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface EntDirectoryClient {

    @GET
    @Path("/structure/{structureId}/users")
    Uni<List<User>> getUsersByStructure(@HeaderParam("Cookie") String sessionId,
                                        @PathParam("structureId") String structureId);

    @GET
    @Path("/class/{classId}")
    Uni<List<Class>> getClassInfos(@HeaderParam("Cookie") String sessionId,
                                   @PathParam("classId") String classId);

    @GET
    @Path("/group/{groupId}")
    Uni<List<Group>> getGroupInfos(@HeaderParam("Cookie") String sessionId,
                                   @PathParam("groupId") String groupId);

    @GET
    @Path("/user/{userId}")
    Uni<List<User>> getUserInfos(@HeaderParam("Cookie") String sessionId,
                                 @PathParam("userId") String userId);
}