package fr.cgi.learninghub.swarm.model;

public class ClassInfos {
    
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

    public ClassInfos setId(String id) {
        this.id = id;
        return this;
    }

    public ClassInfos setName(String name) {
        this.name = name;
        return this;
    }
}
