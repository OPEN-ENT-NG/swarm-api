package fr.cgi.learninghub.swarm.model;

public class StudentClass {
    
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

    public StudentClass setId(String id) {
        this.id = id;
        return this;
    }

    public StudentClass setName(String name) {
        this.name = name;
        return this;
    }
}
