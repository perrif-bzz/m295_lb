package com.perrif.m295_lb;

import com.perrif.m295_lb.services.CarController;
import com.perrif.m295_lb.services.DatabaseController;
import com.perrif.m295_lb.util.exceptionHandlers.*;
import com.perrif.m295_lb.util.security.AuthenticationFilter;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/resources")
public class RestConfig extends Application
{
    public Set<Class<?>> getClasses() {
        return new HashSet<>(
                Arrays.asList(
                        AuthenticationFilter.class,
                        CarController.class,
                        DatabaseController.class,
                        NotFoundExceptionHandler.class,
                        InternalServerErrorExceptionHandler.class,
                        ConstraintViolationExceptionHandler.class,
                        BadRequestExceptionHandler.class
                ));
    }
}
