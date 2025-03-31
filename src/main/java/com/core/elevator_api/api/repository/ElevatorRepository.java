package com.core.elevator_api.api.repository;

import com.core.elevator_api.api.model.Elevator;
import com.core.elevator_api.constants.ElevatorTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ElevatorRepository extends JpaRepository<Elevator, String> {

  Optional<Elevator> findByType(ElevatorTypes type);

  List<Elevator> findByIdOrType(String id, ElevatorTypes type);
}
