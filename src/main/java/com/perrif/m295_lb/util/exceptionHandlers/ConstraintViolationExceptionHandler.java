package com.fede.m295.m295_project_setup.util.exceptionHandlers;

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
    @Override
    public Response toResponse(ConstraintViolationException ex) {
        throw new BadRequestException(ex.getMessage());
    }
}
