package fr.cgi.learninghub.swarm.service;

import fr.cgi.learninghub.swarm.model.ResponseListUser;
import fr.cgi.learninghub.swarm.model.User;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public interface IUserService {

    public Uni<List<String>> getConnectedUserStructures();
    
    public Uni<List<User>> getAllUsers();
    
    public Uni<ResponseListUser> getAndFilterUsers();
}
