package com.perrif.m295_lb.services;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.SQLException;

@Component
@Path("/database")
public class DatabaseController
{
    private final Logger logger = LogManager.getLogger(DatabaseController.class);

    @GET
    @Path("/ping")
    @Produces(MediaType.TEXT_PLAIN)
    @PermitAll
    public Response ping()
    {
        return Response.status(Response.Status.OK).entity("Database controller is running...").build();
    }

    private final DataSource dataSource;

    @Autowired
    public DatabaseController(JdbcTemplate jdbcTemplate) {
        this.dataSource = jdbcTemplate.getDataSource();
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed({"ADMIN"})
    public Response createTables() {
        try {
            ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("carDb.sql"));
            logger.info("Creating db tables...");
            return Response.status(Response.Status.OK).entity("Tables created successfully!").build();
        } catch (SQLException e) {
            logger.error("Failed to create db tables: " + e.getMessage());
            throw new InternalServerErrorException(e.getMessage());
        }
    }
}
