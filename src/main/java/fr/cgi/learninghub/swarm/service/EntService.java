package fr.cgi.learninghub.swarm.service;

import fr.cgi.learninghub.swarm.clients.EntClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import fr.cgi.learninghub.swarm.model.Infra;


@ApplicationScoped
public class EntService {

    private final EntClient entClient;

    public EntService(@RestClient EntClient entClient) {
        this.entClient = entClient;
    }

    public Uni<Infra> getHealth() {
        return entClient.getEnts();
    }
}
