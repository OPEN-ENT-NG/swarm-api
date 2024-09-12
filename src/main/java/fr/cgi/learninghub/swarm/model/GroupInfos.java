package fr.cgi.learninghub.swarm.model;

public class GroupInfos {
    
    private String id;

    private String name;

    // Getter

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // Setter

    public GroupInfos setId(String id) {
        this.id = id;
        return this;
    }

    public GroupInfos setName(String name) {
        this.name = name;
        return this;
    }
}
