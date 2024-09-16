package fr.cgi.learninghub.swarm.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;


public class DeleteServiceBadRequestException extends WebApplicationException {
    static final Response DEFAULT_RESPONSE = Response.status(Response.Status.BAD_REQUEST)
            .entity("Wrong values given to delete the services")
            .build();

    public DeleteServiceBadRequestException() {
        super(DEFAULT_RESPONSE);
    }

}
