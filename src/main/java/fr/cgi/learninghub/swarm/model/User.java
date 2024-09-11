package fr.cgi.learninghub.swarm.model;

import fr.cgi.learninghub.swarm.model.StudentClass;
import fr.cgi.learninghub.swarm.model.Structure;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    
    @JsonProperty("id")
    private String id;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("structures")
    private List<Structure> structures;

    @JsonProperty("classes")
    private List<StudentClass> classes;

    @JsonProperty("manualGroups")
    private List<Group> groups;

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

    public List<Structure> getStructures() {
        return structures;
    }
    
    public List<StudentClass> getClasses() {
        return classes;
    }
    
    public List<Group> getGroups() {
        return groups;
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

    public User setStructures(List<Structure> structures) {
        this.structures = structures;
        return this;
    }
    
    public User setClasses(List<StudentClass> classes) {
        this.classes = classes;
        return this;
    }
    
    public User setGroups(List<Group> groups) {
        this.groups = groups;
        return this;
    }

    // Functions

    public List<String> getClassIds() {
        return this.classes.stream().map(c -> c.getId()).collect(Collectors.toList());
    }

    public List<String> getGroupIds() {
        return this.groups.stream().map(group -> group.getId()).collect(Collectors.toList());
    }
}
