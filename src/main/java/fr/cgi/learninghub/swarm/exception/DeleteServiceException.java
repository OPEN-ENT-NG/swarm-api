package fr.cgi.learninghub.swarm.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;


public class DeleteServiceException extends WebApplicationException {
    static final Response DEFAULT_RESPONSE = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity("Error deleting services")
            .build();

    public DeleteServiceException() {
        super(DEFAULT_RESPONSE);
    }

}
