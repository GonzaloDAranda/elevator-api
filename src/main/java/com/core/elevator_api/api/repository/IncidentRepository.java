package com.core.elevator_api.api.repository;

import com.core.elevator_api.api.model.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidentRepository extends JpaRepository<Incident, String> {
}
