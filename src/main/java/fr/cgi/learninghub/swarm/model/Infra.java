package fr.cgi.learninghub.swarm.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Infra {
    
    @JsonProperty("postgresql")
    private String postgresql;

    @JsonProperty("neo4j")
    private String neo4j;

    @JsonProperty("mongodb")
    private String mongodb;
}
