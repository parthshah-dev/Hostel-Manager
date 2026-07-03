package com.example.hostelmanagement.repository;

import com.example.hostelmanagement.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Tenant} entities.
 */
@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {

    /**
     * Counts the number of active tenants.
     *
     * @return The count of active tenants.
     */
    long countByActiveTrue();
}
