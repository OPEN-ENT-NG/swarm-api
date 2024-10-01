package fr.cgi.learninghub.swarm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "ClassInfos object representing a class in the system")
public class ClassInfos {

    @Schema(description = "Id of the class",
            example = "a372ffa0-dae0-4858-9aaa-9035ccf41021",
            required = true)
    @JsonProperty("classId")
    private String id;

    @Schema(description = "Name of the class",
            example = "6EME1",
            required = true)
    @JsonProperty("name")
    private String name;

    @Schema(description = "ID of the class",
            example = "334ca8d2-f36c-46c1-8120-848c5cc8b1fb",
            required = true)
    @JsonProperty("schoolId")
    private String schoolId;

    // Getter

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSchoolId() {
        return schoolId;
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

    public ClassInfos setSchoolId(String schoolId) {
        this.schoolId = schoolId;
        return this;
    }
}
