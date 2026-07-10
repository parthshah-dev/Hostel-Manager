package com.example.hostelmanagement.repository;

import com.example.hostelmanagement.entity.Tenant;
import com.example.hostelmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link Tenant} entities.
 */
@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {

    /**
     * Checks if a tenant exists with the given email.
     *
     * @param email The email to check.
     * @return true if it exists, false otherwise.
     */
    boolean existsByEmail(String email);

    /**
     * Checks if a tenant exists with the given Aadhaar number.
     *
     * @param aadhaarNumber The Aadhaar number.
     * @return true if it exists, false otherwise.
     */
    boolean existsByAadhaarNumber(String aadhaarNumber);

    /**
     * Finds a tenant by their email.
     *
     * @param email The email.
     * @return An Optional containing the Tenant, or empty if not found.
     */
    Optional<Tenant> findByEmail(String email);

    /**
     * Finds all tenants assigned to a specific room.
     *
     * @param roomId The room ID.
     * @return A list of tenants.
     */
    List<Tenant> findByRoomId(Long roomId);

    /**
     * Counts active tenants residing in a specific room.
     *
     * @param roomId The room ID.
     * @return Count of active tenants in the room.
     */
    long countByRoomIdAndActiveTrue(Long roomId);

    /**
     * Counts total active tenants across the system.
     *
     * @return Total count of active tenants.
     */
    long countByActiveTrue();

    List<Tenant> findByActiveTrue();

    List<Tenant> findByRoomAdmin(User admin);

    List<Tenant> findByActiveTrueAndRoomAdmin(User admin);

    long countByActiveTrueAndRoomAdmin(User admin);
}
