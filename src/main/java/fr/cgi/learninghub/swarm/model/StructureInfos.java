package fr.cgi.learninghub.swarm.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "StructureInfos object representing a structure in the system")
public class StructureInfos {

    @Schema(description = "Id (as UAI) of the structure",
            example = "0123456Z",
            required = true)
    @JsonAlias("UAI")
    private String id;

    @Schema(description = "Name of the structure",
            example = "Emile Zola",
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

    public StructureInfos setId(String id) {
        this.id = id;
        return this;
    }

    public StructureInfos setName(String name) {
        this.name = name;
        return this;
    }
}
