package com.perrif.m295_lb.services;

import com.perrif.m295_lb.repository.ITodoRepository;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/todo")
public class TodoController
{
    private final Logger logger = LogManager.getLogger(TodoController.class);

    private final ITodoRepository todoRepository;

    @Autowired
    public TodoController(ITodoRepository todoRepository)
    {
        this.todoRepository = todoRepository;
    }

    @GET
    @Path("/ping")
    @Produces(MediaType.TEXT_PLAIN)
    @PermitAll
    public Response ping()
    {
        return Response.status(Response.Status.OK).entity("Todo controller is running...").build();
    }
}