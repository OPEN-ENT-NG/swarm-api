package fr.cgi.learninghub.swarm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Objects;

@Schema(description = "ClassInfos object representing a class in the system")
public class ClassInfos {

    @Schema(description = "Id of the class",
            example = "a372ffa0-dae0-4858-9aaa-9035ccf41021",
            required = true)
    @JsonProperty("id")
    private String id;

    @Schema(description = "Name of the class",
            example = "6EME1",
            required = true)
    @JsonProperty("name")
    private String name;

    //Constructor

    public ClassInfos() {}

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

    // Utils

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassInfos classInfos)) return false;
        return Objects.equals(id, classInfos.id) && Objects.equals(name, classInfos.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
