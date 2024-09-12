package fr.cgi.learninghub.swarm.model;

import fr.cgi.learninghub.swarm.model.StructureInfos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserInfos {

    @JsonProperty("id")
    private String id;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("structureNodes")
    private List<StructureInfos> structures;

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

    // Functions

    public List<String> getStructuresIds() {
        return this.structures.stream().map(structure -> structure.getId()).toList();
    }
}
