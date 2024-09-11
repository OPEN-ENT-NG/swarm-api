package fr.cgi.learninghub.swarm.service;

import fr.cgi.learninghub.swarm.entity.Service;
import fr.cgi.learninghub.swarm.repository.ServiceRepository;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

import org.jboss.logging.Logger;

@ApplicationScoped
public class ServiceService {
    
    private static final Logger log = Logger.getLogger(ServiceService.class);
    ServiceRepository serviceRepository;

    @Inject
    public ServiceService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public Uni<List<Service>> listAll() {
        return this.serviceRepository.listAll();
    }
}
