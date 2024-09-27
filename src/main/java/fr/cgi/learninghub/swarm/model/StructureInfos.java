package fr.cgi.learninghub.swarm.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "StructureInfos object representing a structure in the system")
public class StructureInfos {

    @Schema(description = "Id of the structure",
            example = "3d3bba2b-7bba-4e0a-94af-c5d1b3332f77",
            required = true)
    @JsonAlias("id")
    private String id;

    @Schema(description = "Id (as externalId) of the structure",
            example = "42",
            required = true)
    @JsonAlias("externalId")
    private String externalId;

    @Schema(description = "Name of the structure",
            example = "Emile Zola",
            required = true)
    @JsonProperty("name")
    private String name;

    @Schema(description = "UAI of the structure",
            example = "0123456Z",
            required = true)
    @JsonAlias("UAI")
    private String uai;

    // Getter

    public String getId() {
        return id;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getName() {
        return name;
    }

    public String getUai() {
        return uai;
    }

    // Setter

    public StructureInfos setId(String id) {
        this.id = id;
        return this;
    }

    public StructureInfos setExternalId(String externalId) {
        this.externalId = externalId;
        return this;
    }

    public StructureInfos setName(String name) {
        this.name = name;
        return this;
    }

    public StructureInfos setUai(String uai) {
        this.uai = uai;
        return this;
    }
}
