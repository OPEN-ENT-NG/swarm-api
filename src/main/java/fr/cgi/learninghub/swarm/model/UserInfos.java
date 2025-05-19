package fr.cgi.learninghub.swarm.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "UserInfos object representing the response to the route getUserInfos()", required = true)
public class UserInfos {

    @Schema(description = "Unique identifier of the user",
            example = "e3685a82-79d2-4c23-89b8-1f8345902266",
            required = true)
    @JsonProperty("id")
    private String id;

    @Schema(description = "List of structures associated with the user",
            example = "[{\"id\": \"d2fa72a7-4b7f-4202-813b-4437e342e34f\", \"uai\": \"0770002J\", \"name\": \"Emile Zola\", \"externalid\": \"3075\"}]",
            required = true)
    @JsonProperty("structureNodes")
    private List<StructureInfos> structures;

    // Getter

    public String getId() {
        return id;
    }

    public List<StructureInfos> getStructures() {
        return structures;
    }

    // Setter

    public UserInfos setId(String id) {
        this.id = id;
        return this;
    }

    public UserInfos setStructures(List<StructureInfos> structures) {
        this.structures = structures;
        return this;
    }

    // Functions

    @JsonIgnore
    public List<String> getStructuresIds() {
        return this.structures.stream().map(StructureInfos::getId).toList();
    }
}
