package fr.cgi.learninghub.swarm.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StructureInfos {
    
    private String id;

    private String name;

    // Getter

    @JsonProperty("UAI")
    public String getId() {
        return id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    // Setter

    public StructureInfos setId(String id) {
        this.id = id;
        return this;
    }

    public StructureInfos setName(String name) {
        this.name = name;
        return this;
    }
}
