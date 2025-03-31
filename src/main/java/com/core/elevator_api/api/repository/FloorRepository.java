package com.core.elevator_api.api.repository;

import com.core.elevator_api.api.model.Floor;
import com.core.elevator_api.constants.AccessLevels;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface FloorRepository extends JpaRepository<Floor, String> {

  Optional<Floor> findByNumber(Integer number);

  List<Floor> findByAccessLevel(AccessLevels accessLevel);
}
