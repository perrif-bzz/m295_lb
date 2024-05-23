package com.perrif.m295_lb.repository;

import com.perrif.m295_lb.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITodoRepository extends JpaRepository<Todo, Integer>
{
}
