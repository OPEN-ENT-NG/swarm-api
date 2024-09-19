package fr.cgi.learninghub.swarm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Schema(description = "ResponseListUser object representing the response to the route listUser()")
public class ResponseListServiceGlobalInfos {

    @Schema(description = "Total of users possible for this response",
            example = "153")
    private Long totalUsers;

    @Schema(description = "List of all structures possible for this response",
            example = "[{\"id\": \"42\", \"name\": \"Emile Zola\"}]")
    @JsonProperty("structures")
    private List<StructureInfos> structures;

    @Schema(description = "List of all classes possible for this response",
            example = "[{\"id\": \"42$1TES 2\", \"name\": \"1TES 2\"}]")
    @JsonProperty("classes")
    private List<ClassInfos> classes;

    @Schema(description = "List of all groups possible for this response",
            example = "[{\"id\": \"0123456Z\", \"name\": \"Élèves du groupe 1TES 2\"}]")
    @JsonProperty("groups")
    private List<GroupInfos> groups;

    @Schema(description = "List of all users possible for this response",
            example = "[{\"id\": \"john.doe\", \"firstName\": \"John\", \"lastName\": \"Doe\", \"structure\": \"42\", \"classes\": [{\"id\": \"42$1TES 2\", \"name\": \"1TES 2\"}]}]")
    @JsonProperty("users")
    private List<UserInfos> users;

    // Getter

    public Long getTotalUsers() {
        return totalUsers;
    }

    public List<StructureInfos> getStructures() {
        return structures;
    }

    public List<ClassInfos> getClasses() {
        return classes;
    }

    public List<GroupInfos> getGroups() {
        return groups;
    }

    public List<UserInfos> getUsers() {
        return users;
    }

    // Setter

    public ResponseListServiceGlobalInfos setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
        return this;
    }

    public ResponseListServiceGlobalInfos setStructures(List<StructureInfos> structures) {
        this.structures = structures;
        return this;
    }

    public ResponseListServiceGlobalInfos setClasses(List<ClassInfos> classes) {
        this.classes = classes;
        return this;
    }

    public ResponseListServiceGlobalInfos setGroups(List<GroupInfos> groups) {
        this.groups = groups;
        return this;
    }

    public ResponseListServiceGlobalInfos setUsers(List<UserInfos> users) {
        this.users = users;
        return this;
    }
}
