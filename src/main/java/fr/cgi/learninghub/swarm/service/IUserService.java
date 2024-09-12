package fr.cgi.learninghub.swarm.service;

import fr.cgi.learninghub.swarm.model.ResponseListUser;
import fr.cgi.learninghub.swarm.model.User;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

public interface IUserService {

    Uni<List<String>> getConnectedUserStructures();
    
    Uni<List<User>> getAllUsers();
    
    Uni<ResponseListUser> getAndFilterUsers();
}
