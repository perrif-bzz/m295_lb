package com.perrif.m295_lb.util.exceptionHandlers;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ConstraintViolationExceptionHandler implements ExceptionMapper<ConstraintViolationException>
{
    @Override
    public Response toResponse(ConstraintViolationException ex) {
        throw new BadRequestException(ex.getMessage());
    }
}
