package fr.cgi.learninghub.swarm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "GroupInfos object representing a group in the system")
public class GroupInfos {

    @Schema(description = "Id of the group",
            example = "f2e75714-8420-4e4e-aad1-38329afbd366",
            required = true)
    @JsonProperty("id")
    private String id;

    @Schema(description = "Name of the group",
            example = "Personnels joignables",
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

    public GroupInfos setId(String id) {
        this.id = id;
        return this;
    }

    public GroupInfos setName(String name) {
        this.name = name;
        return this;
    }
}
