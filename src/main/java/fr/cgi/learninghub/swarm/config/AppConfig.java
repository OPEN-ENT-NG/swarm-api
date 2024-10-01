package fr.cgi.learninghub.swarm.config;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;

@ApplicationScoped
public class AppConfig {

    @ConfigProperty(name = "app.classaafids")
    List<String> classIds;
    
    @ConfigProperty(name = "app.groupids")
    List<String> groupIds;

    @ConfigProperty(name = "mail.domain")
    String mailDomain;

    @ConfigProperty(name = "host")
    String host;

    public List<String> getClassIds() {
        return classIds;
    }

    public List<String> getGroupIds() {
        return groupIds;
    }

    public String getMailDomain() {
        return mailDomain;
    }

    public String getHost() { return host; }
}