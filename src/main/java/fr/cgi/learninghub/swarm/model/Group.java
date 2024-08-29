package fr.cgi.learninghub.swarm.model;

public class Group {
    
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

    public Group setId(String id) {
        this.id = id;
        return this;
    }

    public Group setName(String name) {
        this.name = name;
        return this;
    }
}
