package fr.cgi.learninghub.swarm.model;

import fr.cgi.learninghub.swarm.model.ClassInfos;
import fr.cgi.learninghub.swarm.model.StructureInfos;

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

    @Schema(description = "String including a structure unique identifier and its name",
            example = "42$TES 1",
            required = true)
    @JsonProperty("structures")
    private String structure;

    @Schema(description = "List of classes associated with the user",
            example = "[{\"id\": \"e1ef8cb4-d7dc-4c9d-9b98-8c0270cfac0b\", \"name\": \"1TES2\"}]",
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

    public User setStructure(String structure) {
        this.structure = structure;
        return this;
    }
    
    public User setClasses(List<String> classes) {
        List<ClassInfos> localClasses = new ArrayList<>();

        if (classes != null) {
            classes.stream().forEach(c -> {
                int dollarIndex = c.indexOf("$");
                String classId = c.substring(0, dollarIndex);
                String className = c.substring(dollarIndex + 1);
                localClasses.add(new ClassInfos().setId(classId).setName(className));
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
