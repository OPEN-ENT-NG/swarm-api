package fr.cgi.learninghub.swarm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.List;

public class UpdateServiceBody {


    @JsonProperty("services_ids")
    @NotNull
    @NotEmpty
    private List<String> servicesIds;

    @JsonProperty("deletion_date")
    @NotNull
    @Future
    private Date deletionDate;

    // Getter

    public List<String> getServicesIds() {
        return servicesIds;
    }

    public Date getDeletionDate() {
        return deletionDate;
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

}
