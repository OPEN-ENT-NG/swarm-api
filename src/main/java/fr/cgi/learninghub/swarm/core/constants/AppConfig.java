package fr.cgi.learninghub.swarm.core.constants;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class AppConfig {

    @ConfigProperty(name = "app.classaafids")
    private List<String> classIds;
    
    @ConfigProperty(name = "app.groupids")
    private List<String> groupIds;
    
    @ConfigProperty(name = "app.userservice.name")
    private String userServiceName;

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

    // Setters

    // Functions
}