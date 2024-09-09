package fr.cgi.learninghub.swarm.service;

import fr.cgi.learninghub.swarm.core.enums.Profile;
import fr.cgi.learninghub.swarm.model.ResponseListUser;
import fr.cgi.learninghub.swarm.model.User;
import fr.cgi.learninghub.swarm.model.UserInfos;
import io.smallrye.mutiny.Uni;

import java.util.List;

public interface IUserService {

    Uni<UserInfos> getConnectedUserInfos();
    
    Uni<List<User>> getAllUsers();

    Uni<List<User>> getAllUsers(Profile profile);

    Uni<List<User>> getUsersByUais(List<String> uais, Profile profile);
    
    Uni<ResponseListUser> getAndFilterUsers();
}
