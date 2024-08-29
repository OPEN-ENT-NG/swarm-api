package fr.cgi.learninghub.swarm.model;

import java.util.List;
import java.util.ArrayList;

public class ResponseListUser {
    
    private List<User> users;
    
    private List<Class> classes;
    
    private List<Group> groups;

    public ResponseListUser(List<User> users, List<Class> classes, List<Group> groups) {
        this.setUsers(users);
        this.setClasses(classes);
        this.setGroups(groups);
    }

    public ResponseListUser() {
        this.users = new ArrayList<>();
        this.classes = new ArrayList<>();
        this.groups = new ArrayList<>();
    }

    // Getter

    public List<User> getUsers() {
        return users;
    }

    public List<Class> getClasses() {
        return classes;
    }

    public List<Group> getGroups() {
        return groups;
    }

    // Setter

    public ResponseListUser setUsers(List<User> users) {
        this.users = users;
        return this;
    }

    public ResponseListUser setClasses(List<Class> classes) {
        this.classes = classes;
        return this;
    }

    public ResponseListUser setGroups(List<Group> groups) {
        this.groups = groups;
        return this;
    }
}
