package com.core.elevator_api.api.repository;

import com.core.elevator_api.api.model.ClearanceLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClearanceLevelRepository extends JpaRepository<ClearanceLevel, String> {
}
