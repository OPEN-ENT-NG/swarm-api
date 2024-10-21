package fr.cgi.learninghub.swarm.model;

import fr.cgi.learning.hub.swarm.common.enums.Type;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "ResponseStatistics object representing the response to the route getting the service statistics")
public class ResponseStatistics {

    @Schema(description = "Name of the service", example = "WORDPRESS", required = true)
    @JsonProperty("type")
    private Type type;

    @Schema(description = "Number of service created", example = "42", required = true)
    @JsonProperty("nbCreatedService")
    private Long nbCreatedService;

    @Schema(description = "Number of active services", example = "40", required = true)
    @JsonProperty("nbActiveService")
    private Long nbActiveService;
    
    @Schema(description = "Number of inactive services", example = "2", required = true)
    @JsonProperty("nbInactiveService")
    private Long nbInactiveService;
    
    @Schema(description = "Number of service deleted soon", example = "8", required = true)
    @JsonProperty("nbDeletedSoonService")
    private Long nbDeletedSoonService;

    // Constructors

    public ResponseStatistics(Type type) {
        this.setType(type);
        this.setNbCreatedService(0L);
        this.setNbActiveService(0L);
        this.setNbInactiveService(0L);
        this.setNbDeletedSoonService(0L);
    }

    // Getter

    public Type getType() {
        return type;
    }

    public Long getNbCreatedService() {
        return nbCreatedService;
    }

    public Long getNbActiveService() {
        return nbActiveService;
    }

    public Long getNbInactiveService() {
        return nbInactiveService;
    }

    public Long getNbDeletedSoonService() {
        return nbDeletedSoonService;
    }

    // Setter

    public ResponseStatistics setType(Type type) {
        this.type = type;
        return this;
    }

    public ResponseStatistics setNbCreatedService(Long nbCreatedService) {
        this.nbCreatedService = nbCreatedService;
        return this;
    }

    public ResponseStatistics setNbActiveService(Long nbActiveService) {
        this.nbActiveService = nbActiveService;
        return this;
    }

    public ResponseStatistics setNbInactiveService(Long nbInactiveService) {
        this.nbInactiveService = nbInactiveService;
        return this;
    }

    public ResponseStatistics setNbDeletedSoonService(Long nbDeletedSoonService) {
        this.nbDeletedSoonService = nbDeletedSoonService;
        return this;
    }
}
