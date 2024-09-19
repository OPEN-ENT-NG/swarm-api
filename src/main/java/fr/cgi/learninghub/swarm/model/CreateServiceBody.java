package fr.cgi.learninghub.swarm.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import fr.cgi.learning.hub.swarm.common.enums.Type;
import java.util.Date;
import java.util.List;

public class CreateServiceBody {

    @JsonProperty("types")
    private List<Type> types;

    @JsonProperty("deletion_date")
    private Date deletionDate;

    @JsonProperty("users")
    private List<String> usersIds;
    
    @JsonProperty("classes")
    private List<String> classesIds;
    
    @JsonProperty("groups")
    private List<String> groupsIds;

    // Getter

    public List<Type> getTypes() {
        return types;
    }

    public Date getDeletionDate() {
        return deletionDate;
    }

    public List<String> getUsersIds() {
        return usersIds;
    }

    public List<String> getClassesIds() {
        return classesIds;
    }

    public List<String> getGroupsIds() {
        return groupsIds;
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

    public CreateServiceBody setUsersIds(List<String> usersIds) {
        this.usersIds = usersIds;
        return this;
    }

    public CreateServiceBody setClassesIds(List<String> classesIds) {
        this.classesIds = classesIds;
        return this;
    }

    public CreateServiceBody setGroupsIds(List<String> groupsIds) {
        this.groupsIds = groupsIds;
        return this;
    }
}
