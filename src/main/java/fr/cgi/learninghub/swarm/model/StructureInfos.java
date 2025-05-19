package fr.cgi.learninghub.swarm.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Objects;

@Schema(description = "StructureInfos object representing a structure in the system")
public class StructureInfos {

    @Schema(description = "Id of the structure",
            example = "3d3bba2b-7bba-4e0a-94af-c5d1b3332f77",
            required = true)
    @JsonProperty("id")
    private String id;

    @Schema(description = "Name of the structure",
            example = "Emile Zola",
            required = true)
    @JsonProperty("name")
    private String name;

    //Constructor

    public StructureInfos() {}

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

    // Utils

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StructureInfos structureInfos)) return false;
        return Objects.equals(id, structureInfos.id) && Objects.equals(name, structureInfos.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
