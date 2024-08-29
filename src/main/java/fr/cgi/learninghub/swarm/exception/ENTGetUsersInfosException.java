package fr.cgi.learninghub.swarm.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;


public class ENTGetUsersInfosException extends WebApplicationException {
    static final Response DEFAULT_RESPONSE = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity("Error getting users infos")
            .build();

    public ENTGetUsersInfosException() {
        super(DEFAULT_RESPONSE);
    }

}
