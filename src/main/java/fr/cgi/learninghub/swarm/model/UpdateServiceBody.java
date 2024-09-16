package fr.cgi.learninghub.swarm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.cgi.learning.hub.swarm.common.enums.State;

import java.util.Date;
import java.util.List;

public class UpdateServiceBody {

    @JsonProperty("services_ids")
    private List<String> servicesIds;

    @JsonProperty("deletion_date")
    private Date deletionDate;
    
    @JsonProperty("state")
    private State state;

    // Getter

    public List<String> getServicesIds() {
        return servicesIds;
    }

    public Date getDeletionDate() {
        return deletionDate;
    }

    public State getState() {
        return state;
    }

    // Setter

    public UpdateServiceBody setServicesIds(List<String> servicesIds) {
        this.servicesIds = servicesIds;
        return this;
    }

    public UpdateServiceBody setDeletionDate(Date deletionDate) {
        this.deletionDate = deletionDate;
        return this;
    }

    public UpdateServiceBody setState(State state) {
        this.state = state;
        return this;
    }
}
