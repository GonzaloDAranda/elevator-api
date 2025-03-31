package com.core.elevator_api.api.repository;

import com.core.elevator_api.api.model.RestrictedAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestrictedAccessRepository extends JpaRepository<RestrictedAccess, String> {
}
