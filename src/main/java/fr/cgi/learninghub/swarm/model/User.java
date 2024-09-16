package fr.cgi.learninghub.swarm.model;

import fr.cgi.learninghub.swarm.core.enums.Profile;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "User object representing a user in the system")
public class User {

    @Schema(description = "Unique identifier (as login) of the user",
            example = "john.doe",
            required = true)
    @JsonAlias("login")
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

    @Schema(description = "String describing the profile of the user",
            example = "Student")
    @JsonProperty("profiles")
    private Profile profile;

    @Schema(description = "String including a structure unique identifier and its name",
            example = "42",
            required = true)
    @JsonProperty("structures")
    private String structure;

    @Schema(description = "List of classes associated with the user",
            example = "[{\"id\": \"42$1TES 2\", \"name\": \"1TES 2\"}]",
            required = true)
    @JsonProperty("classes")
    private List<ClassInfos> classes;

    // @JsonAlias("manualGroups")
    // private List<StudentGroup> groups;

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

    public Profile getProfile() {
        return profile;
    }

    public String getStructure() {
        return structure;
    }
    
    public List<ClassInfos> getClasses() {
        return classes;
    }
    
    // public List<StudentGroup> getGroups() {
    //     return groups;
    // }

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

    public User setProfile(String profile) {
        this.profile = Profile.getProfile(profile.toUpperCase());
        return this;
    }

    public User setStructure(String structure) {
        this.structure = structure;
        return this;
    }
    
    public User setClasses(List<String> classes) {
        List<ClassInfos> localClasses = new ArrayList<>();

        if (classes != null) {
            classes.forEach(c -> {
                String className = c.substring(c.indexOf("$") + 1);
                localClasses.add(new ClassInfos().setId(c).setName(className));
            });
        }

        this.classes = localClasses;
        return this;
    }
    
    // public User setGroups(List<StudentGroup> groups) {
    //     this.groups = groups;
    //     return this;
    // }

    // Functions

    @JsonIgnore
    public List<String> getClassIds() {
        return this.classes.stream().map(c -> c.getId()).toList();
    }

    @JsonIgnore
    public List<String> getGroupIds() {
        return new ArrayList();
        // return this.groups.stream().map(group -> group.getId()).toList();
    }
}
