package com.perrif.m295_lb.services;

import com.perrif.m295_lb.model.Todo;
import com.perrif.m295_lb.repository.ITodoRepository;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response getAllTodos()
    {
        List<Todo> todos;

        try
        {
            todos = todoRepository.findAll();
        } catch (Exception e)
        {
            throw new InternalServerErrorException(e.getMessage());
        }

        if (todos.isEmpty())
        {
            logger.info("No todos are in the database.");
            return Response.status(Response.Status.NO_CONTENT)
                    .entity("No todos are in the database.")
                    .build();
        }

        logger.info("Returning all todos...");
        return Response.status(Response.Status.OK).entity(todos).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response getTodoById(@PathParam("id") @Valid Integer id)
    {
        Optional<Todo> todo;

        try
        {
            todo = todoRepository.findById(id);
        } catch (Exception e)
        {
            throw new InternalServerErrorException(e.getMessage());
        }

        if (todo.isPresent())
        {
            logger.info("Returning todo with number " + id + ".");
            return Response.status(Response.Status.OK).entity(todo).build();
        }
        throw new NotFoundException("No todo with number " + id + " found.");
    }

    private Response saveOrUpdate(Todo todo)
    {
        try {
            Optional<Todo> optionalTodo = todoRepository.findById(todo.getId());

            if (optionalTodo.isPresent()) {
                Todo foundTodo = optionalTodo.get();
                foundTodo.setTodoValue(todo.getTodoValue());
                todo = foundTodo;
            }

            logger.info("Inserting / updating todo with number " + todo.getId() + ".");

            Todo savedTodo = todoRepository.save(todo);
            return Response.status(Response.Status.OK).entity(savedTodo).build();
        } catch (Exception e) {
            logger.error("Error saving or updating todo", e);
            throw new InternalServerErrorException("An error occurred while saving or updating the todo.");
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN"})
    public Response add(@Valid Todo requestEntity)
    {
        if (todoRepository.findById(requestEntity.getId()).isPresent())
        {
            logger.info("Todo with number " + requestEntity.getId() + " already exists.");
            return Response.status(Response.Status.CONFLICT).entity(requestEntity).build();
        }
        return saveOrUpdate(requestEntity);
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN"})
    public Response update(@Valid Todo requestEntity)
    {
        if (todoRepository.findById(requestEntity.getId()).isPresent())
        {
            return saveOrUpdate(requestEntity);
        }
        throw new NotFoundException("Todo with number " + requestEntity.getId() + " doesn't exist.");
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed({"ADMIN"})
    public Response delete(@PathParam("id") @Valid Integer id)
    {
        try
        {
            Optional<Todo> optionalTodo = todoRepository.findById(id);
            if (optionalTodo.isPresent())
            {
                Todo todo = optionalTodo.get();
                todoRepository.deleteById(todo.getId());

                logger.info("Deleting todo with number " + id + ".");
                return Response.status(Response.Status.OK).entity("Todo with number " + id + " has been deleted.").build();
            }
        } catch (Exception e)
        {
            logger.error("Error deleting todo with number " + id + ": " + e.getMessage());
            throw new InternalServerErrorException(e.getMessage());
        }
        throw new NotFoundException("No todo with number " + id + " found.");
    }
}