package fr.cgi.learninghub.swarm.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;


public class NoClassesProvidedException extends WebApplicationException {
    static final Response DEFAULT_RESPONSE = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity("No classes provided filtered by config for user retrieval")
            .build();

    public NoClassesProvidedException() {
        super(DEFAULT_RESPONSE);
    }

}
