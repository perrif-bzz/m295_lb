package com.perrif.m295_lb.util.exceptionHandlers;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Provider
public class NotFoundExceptionHandler implements ExceptionMapper<NotFoundException>
{
    protected final Logger logger = LogManager.getLogger(NotFoundExceptionHandler.class);

    @Override
    public Response toResponse(NotFoundException ex)
    {
        logger.warn(ex.getMessage());
        return Response.status(Response.Status.NOT_FOUND).entity(ex.getMessage()).build();
    }
}
