package fr.cgi.learninghub.swarm.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "UserInfos object representing the response to the route getUserInfos()", required = true)
public class UserInfos {

    @Schema(description = "Unique identifier of the user",
            example = "e3685a82-79d2-4c23-89b8-1f8345902266",
            required = true)
    @JsonProperty("id")
    private String id;

    @Schema(description = "First name of the user",
            example = "John",
            required = true)
    @JsonProperty("firstName")
    private String firstName;

    @Schema(description = "Last name of the user",
            example = "DOE",
            required = true)
    @JsonProperty("lastName")
    private String lastName;

    @Schema(description = "List of structures associated with the user",
            example = "[{\"id\": \"d2fa72a7-4b7f-4202-813b-4437e342e34f\", \"uai\": \"0770002J\", \"name\": \"Emile Zola\", \"externalid\": \"3075\"}]",
            required = true)
    @JsonProperty("structureNodes")
    private List<StructureInfos> structures;

    @Schema(description = "Unique login of the user",
            example = "john.doe")
    @JsonProperty("login")
    private String login;

    @Schema(description = "String describing the mail of the user",
            example = "mail@ng1.support-ent.fr")
    @JsonProperty("email")
    private String email;

    // Getter

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public List<StructureInfos> getStructures() {
        return structures;
    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }

    // Setter

    public UserInfos setId(String id) {
        this.id = id;
        return this;
    }

    public UserInfos setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public UserInfos setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public UserInfos setStructures(List<StructureInfos> structures) {
        this.structures = structures;
        return this;
    }

    public UserInfos getLogin(String login) {
        this.login = login;
        return this;
    }

    public UserInfos getEmail(String email) {
        this.email = email;
        return this;
    }

    @JsonIgnore
    public List<String> getStructuresIds() {
        return this.structures.stream().map(StructureInfos::getId).toList();
    }

    @JsonIgnore
    public List<String> getStructuresUais() {
        return this.structures.stream().map(StructureInfos::getUai).toList();
    }


}
