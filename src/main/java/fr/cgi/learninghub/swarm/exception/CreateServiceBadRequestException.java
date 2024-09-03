package fr.cgi.learninghub.swarm.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;


public class CreateServiceBadRequestException extends WebApplicationException {
    static final Response DEFAULT_RESPONSE = Response.status(Response.Status.BAD_REQUEST)
            .entity("Wrong values given for the services to create")
            .build();

    public CreateServiceBadRequestException() {
        super(DEFAULT_RESPONSE);
    }

}
