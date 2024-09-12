package fr.cgi.learninghub.swarm.model;

import java.util.List;
import java.util.ArrayList;

public class ResponseListUser {
    
    private List<User> users;
    
    private List<ClassInfos> classes;
    
    private List<GroupInfos> groups;

    public ResponseListUser(List<User> users, List<ClassInfos> classes, List<GroupInfos> groups) {
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

    public List<ClassInfos> getClasses() {
        return classes;
    }

    public List<GroupInfos> getGroups() {
        return groups;
    }

    // Setter

    public ResponseListUser setUsers(List<User> users) {
        this.users = users;
        return this;
    }

    public ResponseListUser setClasses(List<ClassInfos> classes) {
        this.classes = classes;
        return this;
    }

    public ResponseListUser setGroups(List<GroupInfos> groups) {
        this.groups = groups;
        return this;
    }
}
