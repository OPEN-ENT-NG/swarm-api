package fr.cgi.learninghub.swarm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;
import java.util.ArrayList;

@Schema(description = "ResponseListUser object representing the response to the route listUser()")
public class ResponseListUser {

    @Schema(description = "List of users in the response",
            example = "[{\"id\": \"john.doe\", \"firstName\": \"John\", \"lastName\": \"Doe\", \"structure\": \"42$TES 1\", \"classes\": [{\"id\": \"e1ef8cb4-d7dc-4c9d-9b98-8c0270cfac0b\", \"name\": \"1TES2\"}]}]",
            required = true)
    @JsonProperty("users")
    private List<User> users;

    @Schema(description = "List of classes  in the response",
            example = "[{\"id\": \"e1ef8cb4-d7dc-4c9d-9b98-8c0270cfac0b\", \"name\": \"1TES2\"}]",
            required = true)
    @JsonProperty("classes")
    private List<ClassInfos> classes;

    @Schema(description = "List of groups in the response",
            example = "[{\"id\": \"0123456Z\", \"name\": \"Emile Zola\"}]",
            required = true)
    @JsonProperty("groups")
    private List<GroupInfos> groups;

    public ResponseListUser(List<User> users, List<ClassInfos> classes, List<GroupInfos> groups) {
        this.setUsers(users);
        this.setClasses(classes);
        this.setGroups(groups);
    }

    public ResponseListUser() {
        this.users = new ArrayList<>();
        this.classes = new ArrayList<>();
        this.groups = new ArrayList<>();
    }

    // Getter

    public List<User> getUsers() {
        return users;
    }

    public List<ClassInfos> getClasses() {
        return classes;
    }

    public List<GroupInfos> getGroups() {
        return groups;
    }

    // Setter

    public ResponseListUser setUsers(List<User> users) {
        this.users = users;
        return this;
    }

    public ResponseListUser setClasses(List<ClassInfos> classes) {
        this.classes = classes;
        return this;
    }

    public ResponseListUser setGroups(List<GroupInfos> groups) {
        this.groups = groups;
        return this;
    }
}
