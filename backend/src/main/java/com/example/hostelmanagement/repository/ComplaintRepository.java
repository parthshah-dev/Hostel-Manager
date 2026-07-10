package com.example.hostelmanagement.repository;

import com.example.hostelmanagement.entity.Complaint;
import com.example.hostelmanagement.entity.ComplaintCategory;
import com.example.hostelmanagement.entity.ComplaintStatus;
import com.example.hostelmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing {@link Complaint} entities.
 */
@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findByStatusAndTenantRoomAdmin(ComplaintStatus status, User admin);

    List<Complaint> findByComplaintCategoryAndTenantRoomAdmin(ComplaintCategory category, User admin);

    List<Complaint> findByTenantId(Long tenantId);

    List<Complaint> findByTenantRoomAdmin(User admin);

    long countByStatusAndTenantRoomAdmin(ComplaintStatus status, User admin);

    long countByTenantRoomAdmin(User admin);
}
