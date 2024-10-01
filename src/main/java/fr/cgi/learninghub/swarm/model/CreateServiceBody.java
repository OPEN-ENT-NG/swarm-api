package fr.cgi.learninghub.swarm.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import fr.cgi.learning.hub.swarm.common.enums.Type;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.List;

public class CreateServiceBody {

    @JsonProperty("types")
    @NotNull
    @NotEmpty
    private List<Type> types;

    @JsonProperty("deletion_date")
    @Future
    @NotNull
    private Date deletionDate;

    @JsonProperty("users")
    private List<String> userIds;

    @JsonProperty("classes")
    private List<String> classIds;


    // Getter

    public List<Type> getTypes() {
        return types;
    }

    public Date getDeletionDate() {
        return deletionDate;
    }

    public List<String> getUsers() {
        return userIds;
    }

    public List<String> getClasses() {
        return classIds;
    }



    // Setter

    public CreateServiceBody setTypes(List<Type> types) {
        this.types = types;
        return this;
    }

    public CreateServiceBody setDeletionDate(Date deletionDate) {
        this.deletionDate = deletionDate;
        return this;
    }

    public CreateServiceBody setUsers(List<String> users) {
        this.userIds = users;
        return this;
    }

    public CreateServiceBody setClasses(List<String> classIds) {
        this.classIds = classIds;
        return this;
    }

}
