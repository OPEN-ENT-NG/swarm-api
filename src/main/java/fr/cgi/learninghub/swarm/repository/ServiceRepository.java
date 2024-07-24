package fr.cgi.learninghub.swarm.repository;

import fr.cgi.learninghub.swarm.model.Service;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ServiceRepository implements PanacheRepository<Service> {
}
