package fr.cgi.learninghub.swarm.model;

public class Structure {
    
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

    public Structure setId(String id) {
        this.id = id;
        return this;
    }

    public Structure setName(String name) {
        this.name = name;
        return this;
    }
}
