package fr.cgi.learninghub.swarm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.cgi.learninghub.swarm.core.enums.Profile;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Schema(description = "Unique login of the user",
            example = "john.doe")
    @JsonProperty("login")
    private String login;

    @Schema(description = "String describing the mail of the user",
            example = "mail@ng1.support-ent.fr")
    @JsonProperty("mail")
    private String mail;

    @Schema(description = "String describing the profile of the user",
            example = "Student")
    @JsonProperty("profiles")
    private Profile profile;

    @Schema(description = "Unique structure identifier",
            example = "42",
            required = true)
    @JsonProperty("structures")
    private String structure;

    @Schema(description = "List of classes associated with the user",
            example = "[{\"id\": \"42$1TES 2\", \"name\": \"1TES 2\"}]")
    @JsonProperty("classes")
    private List<ClassInfos> classes;

    // Constructor

    public User() {}

    public User(UserInfos userInfos) {
        setId(userInfos.getId());
        setFirstName(userInfos.getFirstName());
        setLastName(userInfos.getLastName());
        setLogin(userInfos.getLogin());
        setMail(userInfos.getEmail());
        setStructure("");
        setClassesInfos(new ArrayList<>());
    }

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

    public String getLogin() {
        return login;
    }

    public String getMail() {
        return mail;
    }

    public Profile getProfile() {
        return profile;
    }

    public String getStructure() {
        return structure;
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

    public User setLogin(String login) {
        this.login = login;
        return this;
    }


    public User setMail(String mail) {
        this.mail = mail;
        return this;
    }

    public User setProfile(String profile) {
        this.profile = Profile.getProfile(profile.toUpperCase());
        return this;
    }

    public User setStructure(String structure) {
        this.structure = structure;
        return this;
    }

    public User setClasses(List<String> classes) {
        if (classes != null) {
            this.classes = classes.stream()
                    .map(classId -> new ClassInfos().setId(classId))
                    .collect(Collectors.toList());
        }
        return this;
    }

    public User setClassesInfos(List<ClassInfos> classes) {
        this.classes = classes;
        return this;
    }
}
