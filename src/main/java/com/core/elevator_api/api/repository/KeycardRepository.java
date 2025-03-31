package com.core.elevator_api.api.repository;

import com.core.elevator_api.api.model.Keycard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeycardRepository extends JpaRepository<Keycard, String> {

}
