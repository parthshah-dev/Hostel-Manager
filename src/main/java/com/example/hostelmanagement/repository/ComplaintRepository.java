package com.example.hostelmanagement.repository;

import com.example.hostelmanagement.entity.Complaint;
import com.example.hostelmanagement.entity.ComplaintCategory;
import com.example.hostelmanagement.entity.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing {@link Complaint} entities.
 */
@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    /**
     * Finds complaints filtered by their status.
     *
     * @param status The complaint status.
     * @return List of Complaint entities.
     */
    List<Complaint> findByStatus(ComplaintStatus status);

    /**
     * Finds complaints filtered by their category.
     *
     * @param category The complaint category.
     * @return List of Complaint entities.
     */
    List<Complaint> findByComplaintCategory(ComplaintCategory category);

    /**
     * Finds all complaints submitted by a specific tenant.
     *
     * @param tenantId Tenant ID.
     * @return List of Complaint entities.
     */
    List<Complaint> findByTenantId(Long tenantId);

    /**
     * Counts complaints with a specific status.
     *
     * @param status The complaint status.
     * @return Count of matching complaints.
     */
    long countByStatus(ComplaintStatus status);
}
