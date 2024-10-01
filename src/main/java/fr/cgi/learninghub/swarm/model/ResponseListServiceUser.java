package fr.cgi.learninghub.swarm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.cgi.learning.hub.swarm.common.entities.Service;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Schema(description = "User object representing a user in the system")
public class ResponseListServiceUser {

    @Schema(description = "",
            example = "",
            required = true)
    @JsonProperty("structures")
    private List<StructureInfos> structures;

    @Schema(description = "List of classes associated with the user",
            example = "",
            required = true)
    @JsonProperty("classes")
    private List<ClassInfos> classes;

    @Schema(description = "",
            example = "",
            required = true)
    @JsonProperty("services")
    private List<Service> services;

    // Getter

    public List<StructureInfos> getStructures() {
        return structures;
    }

    public List<ClassInfos> getClasses() {
        return classes;
    }

    public List<Service> getServices() {
        return services;
    }

    // Setter

    public ResponseListServiceUser setStructures(List<StructureInfos> structures) {
        this.structures = structures;
        return this;
    }

    public ResponseListServiceUser setClasses(List<ClassInfos> classes) {
        this.classes = classes;
        return this;
    }

    public ResponseListServiceUser setServices(List<Service> services) {
        this.services = services;
        return this;
    }
}
