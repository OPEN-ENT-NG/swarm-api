package fr.cgi.learninghub.swarm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.cgi.learning.hub.swarm.common.enums.State;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;

import java.util.Date;
import java.util.List;

public class PatchStateServiceBody {


    @JsonProperty("services_ids")
    @NotNull
    @NotEmpty
    private List<String> servicesIds;

    @JsonProperty("state")
    @NotNull
    private State state;

    // Getter

    public List<String> getServicesIds() {
        return servicesIds;
    }

    public State getState() {
        return state;
    }

    // Setter

    public PatchStateServiceBody setServicesIds(List<String> servicesIds) {
        this.servicesIds = servicesIds;
        return this;
    }

    public PatchStateServiceBody setState(State state) {
        this.state = state;
        return this;
    }
}
