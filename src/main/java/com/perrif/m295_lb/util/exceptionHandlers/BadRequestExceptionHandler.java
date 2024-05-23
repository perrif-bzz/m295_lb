package com.fede.m295.m295_project_setup.util.exceptionHandlers;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BadRequestExceptionHandler implements ExceptionMapper<BadRequestException>
{
    protected final Logger logger = LogManager.getLogger(BadRequestExceptionHandler.class);

    @Override
    public Response toResponse(BadRequestException ex) {
        logger.warn(ex.getMessage());
        return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
    }
}
