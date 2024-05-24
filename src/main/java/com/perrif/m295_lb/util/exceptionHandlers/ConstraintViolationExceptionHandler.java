package com.perrif.m295_lb.util.exceptionHandlers;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Provider
public class ConstraintViolationExceptionHandler implements ExceptionMapper<ConstraintViolationException>
{
    protected final Logger logger = LogManager.getLogger(ConstraintViolationExceptionHandler.class);

    @Override
    public Response toResponse(ConstraintViolationException ex) {
        logger.warn(ex.getMessage());
        return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
    }
}
