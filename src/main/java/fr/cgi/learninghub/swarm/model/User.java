package fr.cgi.learninghub.swarm.model;

import fr.cgi.learninghub.swarm.model.ClassInfos;
import fr.cgi.learninghub.swarm.model.StructureInfos;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {

    @JsonProperty("login")
    private String id;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("structures")
    private String structure;

    @JsonProperty("classes")
    private List<ClassInfos> classes;

    // @JsonProperty("manualGroups")
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

    public List<String> getClassIds() {
        return this.classes.stream().map(c -> c.getId()).toList();
    }

    public List<String> getGroupIds() {
        return new ArrayList();
        // return this.groups.stream().map(group -> group.getId()).toList();
    }
}
