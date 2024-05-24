package com.perrif.m295_lb.repository;

import com.perrif.m295_lb.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IOwnerRepository extends JpaRepository<Owner, Integer>
{
    Optional<Owner> findByAhvNr(String ahvNr);
}
