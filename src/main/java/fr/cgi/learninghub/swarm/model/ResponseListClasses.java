package fr.cgi.learninghub.swarm.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Map;

@RegisterForReflection
@Schema(description = "ResponseListClasses object representing the response to the get classes endpoint")
public class ResponseListClasses {
    private Map<String, ClassInfos> result;
    private String status;


    public Map<String, ClassInfos> getResult() {
        return result;
    }

    public String getStatus() {
        return status;
    }

    public ResponseListClasses setResult(Map<String, ClassInfos> result) {
        this.result = result;
        return this;
    }

    public ResponseListClasses setStatus(String status) {
        this.status = status;
        return this;
    }
}
