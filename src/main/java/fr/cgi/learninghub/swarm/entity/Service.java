package fr.cgi.learninghub.swarm.entity;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.CreationTimestamp;

import fr.cgi.learninghub.swarm.core.enums.State;
import fr.cgi.learninghub.swarm.core.enums.Type;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.Date;


@Entity
@Table(name = "service", uniqueConstraints = @UniqueConstraint(columnNames = {"type", "user_id"}))
@Schema(description = "Service entity representing a service in the system")
public class Service {

    @Schema(description = "Auto-generated unique identifier of the service",
            example = "1")
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Schema(description = "Identifier of the user using the service",
            example = "",
            required = true)
    @Column(name = "user_id", nullable = false)
    private String userId;

    @Schema(description = "First name of the user using the service",
            example = "John",
            required = true)
    @Column(name = "first_name")
    private String firstName;

    @Schema(description = "Last name of the user using the service",
            example = "DOE",
            required = true)
    @Column(name = "last_name")
    private String lastName;

    @Schema(description = "Name of the service",
            example = "",
            required = true)
    @Column(name = "service_name")
    private String serviceName;

    @Schema(description = "Structure identifier of the user using the service",
            example = "0561f703-4e72-46fe-a92e-d887fd27439b",
            required = true)
    @Column(name = "structure_id", nullable = false)
    private String structureId;

    @Schema(description = "Type of the service",
            example = "WORDPRESS",
            required = true)
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private Type type;

    @Schema(description = "Auto-generated date of the creation of the service",
            example = "2024-09-13 01:05:43.832594",
            required = true)
    @CreationTimestamp
    @Column(name = "created", nullable = false)
    private Date created;

    @Schema(description = "Date of the deletion of the service",
            example = "2024-09-14 01:05:43.832594",
            required = true)
    @Column(name = "deletion_date")
    private Date deletionDate;

    @Schema(description = "Current state of the service",
            example = "DEPLOYED",
            required = true)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private State state = State.SCHEDULED;

    // Getter

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getStructureId() {
        return structureId;
    }

    public Type getType() {
        return type;
    }

    public Date getCreated() {
        return created;
    }

    public Date getDeletionDate() {
        return deletionDate;
    }

    public State getState() {
        return state;
    }

    // Setter

    public Service setId(Long id) {
        this.id = id;
        return this;
    }

    public Service setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public Service setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public Service setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public Service setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public Service setStructureId(String structureId) {
        this.structureId = structureId;
        return this;
    }

    public Service setType(Type type) {
        this.type = type;
        return this;
    }

    public Service setCreated(Date created) {
        this.created = created;
        return this;
    }

    public Service setDeletionDate(Date deletionDate) {
        this.deletionDate = deletionDate;
        return this;
    }

    public Service setState(State state) {
        this.state = state;
        return this;
    }
}
