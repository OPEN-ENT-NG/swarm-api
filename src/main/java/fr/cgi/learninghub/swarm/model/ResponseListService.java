package fr.cgi.learninghub.swarm.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Schema(description = "ResponseListService object representing the response to the route list()")
public class ResponseListService {

    private ResponseListServiceGlobalInfos globalInfos;

    private List<ResponseListServiceUser> filteredUsers;

    // Getter

    public ResponseListServiceGlobalInfos getGlobalInfos() {
        return globalInfos;
    }

    public List<ResponseListServiceUser> getFilteredUsers() {
        return filteredUsers;
    }

    // Setter

    public ResponseListService setGlobalInfos(ResponseListServiceGlobalInfos globalInfos) {
        this.globalInfos = globalInfos;
        return this;
    }

    public ResponseListService setFilteredUsers(List<ResponseListServiceUser> filteredUsers) {
        this.filteredUsers = filteredUsers;
        return this;
    }
}
