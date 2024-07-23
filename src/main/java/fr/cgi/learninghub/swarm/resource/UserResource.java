package fr.cgi.learninghub.swarm.resource;

import fr.cgi.learninghub.swarm.model.User;
import io.quarkus.oidc.UserInfo;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/users/me")
@Authenticated
public class UserResource {

    @Inject
    UserInfo userInfo;
    @GET
    public User me() {
        return new User(userInfo.getName());
    }
}
