package fr.cgi.learninghub.swarm.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;


public class UpdateServiceBadRequestException extends WebApplicationException {
    static final Response DEFAULT_RESPONSE = Response.status(Response.Status.BAD_REQUEST)
            .entity("Wrong values given to update the services")
            .build();

    public UpdateServiceBadRequestException() {
        super(DEFAULT_RESPONSE);
    }

}
