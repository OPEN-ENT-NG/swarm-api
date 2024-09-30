package fr.cgi.learninghub.swarm.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;


public class InvalidMailException extends WebApplicationException {
    static final Response DEFAULT_RESPONSE = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity("Error mail is invalid")
            .build();

    public InvalidMailException() {
        super(DEFAULT_RESPONSE);
    }

}
