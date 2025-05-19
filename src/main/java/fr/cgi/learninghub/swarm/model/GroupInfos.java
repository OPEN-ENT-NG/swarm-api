package fr.cgi.learninghub.swarm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "GroupInfos object representing a group in the system")
public class GroupInfos {

    @Schema(description = "Id of the group",
            example = "555-1468756944337",
            required = true)
    @JsonProperty("id")
    private String id;

    @Schema(description = "Name of the group",
            example = "CLG-DENECOURT-BOIS-LE-ROI-Personnel\n",
            required = true)
    @JsonProperty("name")
    private String name;

    //Constructor

    public GroupInfos() {}

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
