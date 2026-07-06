package com.example.hostelmanagement.service;

import com.example.hostelmanagement.dto.*;
import com.example.hostelmanagement.entity.ComplaintCategory;
import com.example.hostelmanagement.entity.ComplaintStatus;
import java.util.List;

/**
 * Service interface defining business processes for Complaint management.
 */
public interface ComplaintService {

    /**
     * Submits a new Complaint for the tenant.
     * Status defaults to OPEN.
     *
     * @param request DTO carrying tenant ID, category, and description.
     * @return ApiResponse.
     */
    ApiResponse addComplaint(ComplaintRequest request);

    /**
     * Retrieves all complaints.
     *
     * @return List of ComplaintResponse.
     */
    List<ComplaintResponse> getAllComplaints();

    /**
     * Retrieves a complaint by database ID.
     *
     * @param id The database ID of the complaint.
     * @return ComplaintResponse.
     */
    ComplaintResponse getComplaintById(Long id);

    /**
     * Updates an existing complaint status and remarks.
     * Validates status transitions: OPEN -> IN_PROGRESS -> RESOLVED.
     * Checks that remarks are mandatory when status resolves.
     *
     * @param id      The database ID.
     * @param request DTO containing updated status and remarks.
     * @return ApiResponse.
     */
    ApiResponse updateComplaint(Long id, ComplaintUpdateRequest request);

    /**
     * Deletes a complaint.
     *
     * @param id The database ID.
     * @return ApiResponse.
     */
    ApiResponse deleteComplaint(Long id);

    /**
     * Finds complaints filtered by status.
     *
     * @param status The status enum value.
     * @return List of ComplaintResponse.
     */
    List<ComplaintResponse> getComplaintsByStatus(ComplaintStatus status);

    /**
     * Finds complaints filtered by category.
     *
     * @param category The category enum value.
     * @return List of ComplaintResponse.
     */
    List<ComplaintResponse> getComplaintsByCategory(ComplaintCategory category);

    /**
     * Finds complaints submitted by a specific tenant.
     *
     * @param tenantId The tenant ID.
     * @return List of ComplaintResponse.
     */
    List<ComplaintResponse> getComplaintsByTenant(Long tenantId);

    /**
     * Retrieves status statistics counts for complaints.
     *
     * @return ComplaintStatisticsResponse DTO.
     */
    ComplaintStatisticsResponse getComplaintStatistics();
}
