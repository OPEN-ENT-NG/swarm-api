package fr.cgi.learninghub.swarm.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.stream.Collectors;

@Provider
public class ConstraintViolationException implements ExceptionMapper<jakarta.validation.ConstraintViolationException> {

    @Override
    public Response toResponse(jakarta.validation.ConstraintViolationException exception) {
        String violations = exception.getConstraintViolations().stream()
                .map(violation -> String.format("%s: %s", violation.getPropertyPath(), violation.getMessage()))
                .collect(Collectors.joining("\n"));

        String errorMessage = String.format("Invalid input:\n%s", violations);

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorMessage)
                .build();
    }
}

