package com.perrif.m295_lb.services;

import com.perrif.m295_lb.model.Car;
import com.perrif.m295_lb.model.Owner;
import com.perrif.m295_lb.repository.ICarRepository;
import com.perrif.m295_lb.repository.IOwnerRepository;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Path("/car")
public class CarController
{
    private final Logger logger = LogManager.getLogger(CarController.class);

    private final ICarRepository carRepository;
    private final IOwnerRepository ownerRepository;

    @Autowired
    public CarController(ICarRepository carRepository, IOwnerRepository ownerRepository)
    {
        this.carRepository = carRepository;
        this.ownerRepository = ownerRepository;
    }

    @GET
    @Path("/ping")
    @Produces(MediaType.TEXT_PLAIN)
    @PermitAll
    public Response ping()
    {
        return Response.status(Response.Status.OK).entity("Car controller is running...").build();
    }

    private List<Car> getAllCars()
    {
        List<Car> cars;

        try
        {
            cars = carRepository.findAll();
        } catch (Exception e)
        {
            throw new InternalServerErrorException(e.getMessage());
        }

        return cars;
    }

    private Optional<Car> getCarById(Integer id)
    {
        Optional<Car> car;

        try
        {
            car = carRepository.findById(id);
        } catch (Exception e)
        {
            throw new InternalServerErrorException(e.getMessage());
        }

        return car;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response getAll()
    {
        List<Car> cars = getAllCars();

        if (cars.isEmpty())
        {
            logger.info("No cars are in the database.");
            return Response.status(Response.Status.NO_CONTENT)
                    .entity("No cars are in the database.")
                    .build();
        }

        logger.info("Returning all cars...");
        return Response.status(Response.Status.OK).entity(cars).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response getById(@PathParam("id") @Valid Integer id)
    {
        Optional<Car> car = getCarById(id);

        if (car.isPresent())
        {
            logger.info("Returning car with id " + id + ".");
            return Response.status(Response.Status.OK).entity(car).build();
        }
        throw new NotFoundException("No car with id " + id + " found.");
    }

    @GET
    @Path("/count")
    @Produces(MediaType.TEXT_PLAIN)
    @PermitAll
    public Response getCount()
    {
        List<Car> cars = getAllCars();

        if (cars.isEmpty())
        {
            logger.info("No cars are in the database.");
            return Response.status(Response.Status.NO_CONTENT).entity("No cars are in the database.").build();
        }

        logger.info("Returning all cars...");
        return Response.status(Response.Status.OK).entity("Datasets present in car table: " + cars.size()).build();
    }

    @GET
    @Path("/isPresent/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    @PermitAll
    public Response getExistsById(@PathParam("id") @Valid Integer id)
    {
        Optional<Car> car = getCarById(id);

        if (car.isPresent())
        {
            logger.info("Car with id " + id + " is present.");
            return Response.status(Response.Status.OK).entity(true).build();
        }

        throw new NotFoundException("No car with id " + id + " found.");
    }

    @GET
    @Path("/make/{make}")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response getByMake(@PathParam("make") @Valid String make)
    {
        List<Car> cars;

        try
        {
            cars = carRepository.findCarsByMake(make);
        } catch (Exception e)
        {
            throw new InternalServerErrorException(e.getMessage());
        }

        if (cars.isEmpty())
        {
            logger.info("No " + make + "s are in the database.");
            return Response.status(Response.Status.NO_CONTENT)
                    .entity("No " + make + "s are in the database.")
                    .build();
        }

        logger.info("Returning all " + make + "s ...");
        return Response.status(Response.Status.OK).entity(cars).build();
    }

    @GET
    @Path("productionDate/{productionDate}")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response getByProductionDate(@PathParam("productionDate") String dateString)
    {
        LocalDate releaseDate;
        try
        {
            releaseDate = LocalDate.parse(dateString);
        } catch (Exception e)
        {
            throw new BadRequestException("Invalid date format | expected yyyy-mm-dd");
        }

        List<Car> cars;
        try
        {
            cars = carRepository.findCarsByProductionDate(releaseDate);
        } catch (Exception e)
        {
            throw new InternalServerErrorException(e.getMessage());
        }

        if (cars.isEmpty())
        {
            logger.info("No cars produced on " + dateString + " are in the database.");
            return Response.status(Response.Status.NO_CONTENT)
                    .entity("No cars produced on " + dateString + " are in the database.")
                    .build();
        }

        logger.info("Returning all cars...");
        return Response.status(Response.Status.OK).entity(cars).build();
    }

    private void saveParentEntity(Car car, Owner owner)
    {
        String ownerAhvNr = owner.getAhvNr();
        Optional<Owner> existingOwner = ownerRepository.findByAhvNr(ownerAhvNr);
        if (existingOwner.isEmpty())
        {
            try
            {
                logger.info("Creating owner with ahvNr " + ownerAhvNr + ".");
                ownerRepository.save(owner);
            } catch (Exception e)
            {
                throw new InternalServerErrorException(e.getMessage());
            }
        } else
        {
            car.setOwner(existingOwner.get());
        }
    }

    private Response saveOrUpdate(Car car)
    {
        Owner owner = car.getOwner();
        saveParentEntity(car, owner);

        try
        {
            logger.info("Adding / updating car with id " + car.getId() + ".");
            return Response.status(Response.Status.OK).entity(carRepository.save(car)).build();
        } catch (Exception e)
        {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "TENANT"})
    public Response add(@Valid Car car)
    {
        if (car.getId() != null)
        {
            Optional<Car> existingCar = carRepository.findById(car.getId());
            if (existingCar.isPresent())
            {
                logger.warn("Car with id " + car.getId() + " already exists.");
                return Response.status(Response.Status.CONFLICT).entity(existingCar).build();
            }
        }

        return saveOrUpdate(car);
    }

    @POST
    @Path("/multiple")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "TENANT"})
    public Response addMultiple(@Valid List<Car> cars)
    {
        List<Car> uniqueCars = new ArrayList<>();

        for (Car car : cars)
        {
            if (car.getId() != null)
            {
                Optional<Car> existingCar = carRepository.findById(car.getId());
                if (existingCar.isPresent())
                {
                    logger.warn("Car with id " + car.getId() + " already exists.");
                    continue;
                }
            }
            saveParentEntity(car, car.getOwner());
            uniqueCars.add(car);
        }

        try
        {
            if (!uniqueCars.isEmpty())
            {
                carRepository.saveAll(uniqueCars);
                logger.info("Saving all non-duplicate cars.");
                return Response.status(Response.Status.OK).entity(uniqueCars).build();
            }
            logger.warn("No non-duplicate cars to save.");
            return Response.status(Response.Status.NO_CONTENT).entity("No non-duplicate cars to save.").build();
        } catch (Exception e)
        {
            logger.error("Error saving all cars: " + e.getMessage());
            throw new InternalServerErrorException(e.getMessage());
        }
    }


    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "TENANT"})
    public Response update(@Valid Car car)
    {
        if (carRepository.findById(car.getId()).isPresent())
        {
            return saveOrUpdate(car);
        }
        throw new NotFoundException("Car with id " + car.getId() + " doesn't exist.");
    }


    @DELETE
    @Path("/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed({"ADMIN"})
    public Response delete(@PathParam("id") @Valid Integer id)
    {
        try
        {
            Optional<Car> optionalCar = carRepository.findById(id);
            if (optionalCar.isPresent())
            {
                Car car = optionalCar.get();
                carRepository.deleteById(car.getId());

                logger.info("Deleting car with id " + id + ".");
                return Response.status(Response.Status.OK).entity("Car with id " + id + " has been deleted.").build();
            }
        } catch (Exception e)
        {
            logger.error("Error deleting car with id " + id + ": " + e.getMessage());
            throw new InternalServerErrorException(e.getMessage());
        }
        throw new NotFoundException("No car with id " + id + " found.");
    }

    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed({"ADMIN"})
    public Response deleteAll()
    {
        try
        {
            carRepository.deleteAll();
            logger.info("Deleting all cars.");
            return Response.status(Response.Status.OK).entity("All cars have been deleted.").build();
        } catch (Exception e)
        {
            logger.error("Error deleting all cars." + e.getMessage());
            throw new InternalServerErrorException(e.getMessage());
        }
    }
}
