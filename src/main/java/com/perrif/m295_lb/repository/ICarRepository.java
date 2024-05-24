package com.perrif.m295_lb.repository;

import com.perrif.m295_lb.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ICarRepository extends JpaRepository<Car, Integer>
{
    List<Car> findCarsByProductionDate(LocalDate productionDate);
    List<Car> findCarsByMake(String make);
}
