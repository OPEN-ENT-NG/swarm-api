package fr.cgi.learninghub.swarm.model;

public class Class {
    
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

    public Class setId(String id) {
        this.id = id;
        return this;
    }

    public Class setName(String name) {
        this.name = name;
        return this;
    }
}
