package fr.cgi.learninghub.swarm.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Schema(description = "ResponseListService object representing the response to the route list()")
public class ResponseListService {

    private Long totalUsers;

    private List<ResponseListServiceUser> users;

    // Getter

    public Long getTotalUsers() {
        return totalUsers;
    }

    public List<ResponseListServiceUser> getUsers() {
        return users;
    }

    // Setter

    public ResponseListService setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
        return this;
    }

    public ResponseListService setUsers(List<ResponseListServiceUser> users) {
        this.users = users;
        return this;
    }
}
