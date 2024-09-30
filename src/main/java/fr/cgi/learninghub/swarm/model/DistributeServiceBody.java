package fr.cgi.learninghub.swarm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class DistributeServiceBody {


    @JsonProperty("services_ids")
    @NotNull
    @NotEmpty
    private List<String> servicesIds;

    // Getter

    public List<String> getServicesIds() {
        return servicesIds;
    }


    // Setter

    public DistributeServiceBody setServicesIds(List<String> servicesIds) {
        this.servicesIds = servicesIds;
        return this;
    }

}
