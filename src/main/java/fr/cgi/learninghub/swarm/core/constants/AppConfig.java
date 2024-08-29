package fr.cgi.learninghub.swarm.core.constants;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class AppConfig {

    @ConfigProperty(name = "app.classids")
    private List<String> classIds;
    
    @ConfigProperty(name = "app.groupids")
    private List<String> groupIds;
    
    @ConfigProperty(name = "app.userservice.name")
    private String userServiceName;
    
    @ConfigProperty(name = "app.session.id")
    private String sessionId;

    // Getters

    public List<String> getClassIds() {
        return classIds;
    }

    public List<String> getGroupIds() {
        return groupIds;
    }

    public String getUserServiceName() {
        return userServiceName;
    }

    public String getSessionId() {
        return sessionId;
    }

    // Setters

    public AppConfig setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    // Functions
}