package fr.cgi.learninghub.swarm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Schema(description = "User object representing a user in the system")
public class User {

    @Schema(description = "Unique identifier of the user",
            example = "341b3e8e-e06c-4343-afa7-06d7f9214ba6")
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

    @Schema(description = "String describing the mail of the user",
            example = "mail@ng1.support-ent.fr")
    @JsonProperty("mail")
    private String mail;

    @Schema(description = "List of structures associated with the user",
            example = "[{\"id\": \"d2fa72a7-4b7f-4202-813b-4437e342e34f\", \"name\": \"Etablissement Formation 13674\"}]",
            required = true)
    @JsonProperty("structures")
    private List<StructureInfos> structures;

    @Schema(description = "List of groups associated with the user",
            example = "[{\"id\": \"555-1468756944337\", \"name\": \"CLG-DENECOURT-BOIS-LE-ROI-Personnel\"}]")
    @JsonProperty("groups")
    private List<GroupInfos> groups;

    @Schema(description = "List of classes associated with the user",
            example = "[{\"id\": \"42$1TES 2\", \"name\": \"1TES 2\"}]")
    @JsonProperty("classes")
    private List<ClassInfos> classes;

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

    public String getMail() {
        return mail;
    }

    public List<StructureInfos> getStructures() {
        return structures;
    }

    public List<GroupInfos> getGroups() {
        return groups;
    }

    public List<ClassInfos> getClasses() {
        return classes;
    }

    // Setter

    public User setId(String id) {
        this.id = id;
        return this;
    }

    public User setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public User setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public User setMail(String mail) {
        this.mail = mail;
        return this;
    }

    public User setStructures(List<StructureInfos> structures) {
        this.structures = structures;
        return this;
    }

    public User setGroups(List<GroupInfos> groups) {
        this.groups = groups;
        return this;
    }

    public User setClasses(List<ClassInfos> classes) {
        this.classes = classes;
        return this;
    }
}
