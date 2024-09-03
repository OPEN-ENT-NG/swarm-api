package fr.cgi.learninghub.swarm.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;


public class CreateServiceException extends WebApplicationException {
    static final Response DEFAULT_RESPONSE = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity("Error creating new services")
            .build();

    public CreateServiceException() {
        super(DEFAULT_RESPONSE);
    }

}
