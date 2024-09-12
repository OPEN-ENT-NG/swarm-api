package fr.cgi.learninghub.swarm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "ClassInfos object representing a class in the system")
public class ClassInfos {

    @Schema(description = "Id of the class",
            example = "e1ef8cb4-d7dc-4c9d-9b98-8c0270cfac0b",
            required = true)
    @JsonProperty("id")
    private String id;

    @Schema(description = "Name of the class",
            example = "1TES2",
            required = true)
    @JsonProperty("name")
    private String name;

    // Getter

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // Setter

    public ClassInfos setId(String id) {
        this.id = id;
        return this;
    }

    public ClassInfos setName(String name) {
        this.name = name;
        return this;
    }
}
