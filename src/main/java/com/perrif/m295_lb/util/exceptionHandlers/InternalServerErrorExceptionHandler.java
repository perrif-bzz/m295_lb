package com.perrif.m295_lb.util.exceptionHandlers;

import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Provider
public class InternalServerErrorExceptionHandler implements ExceptionMapper<InternalServerErrorException>
{
    protected final Logger logger = LogManager.getLogger(InternalServerErrorExceptionHandler.class);

    @Override
    public Response toResponse(InternalServerErrorException ex)
    {
        logger.warn(ex.getMessage());
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
    }
}
