package fr.cgi.learninghub.swarm.config;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;

@ApplicationScoped
public class AppConfig {
    @ConfigProperty(name = "app.mefids")
    List<String> mefIds;

    @ConfigProperty(name = "mail.domain")
    String mailDomain;

    @ConfigProperty(name = "host")
    String host;

    public List<String> getMefIds() {
        return mefIds;
    }

    public String getMailDomain() {
        return mailDomain;
    }

    public String getHost() { return host; }
}