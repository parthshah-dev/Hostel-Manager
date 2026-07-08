package com.example.hostelmanagement.controller;

import com.example.hostelmanagement.dto.*;
import com.example.hostelmanagement.entity.ComplaintCategory;
import com.example.hostelmanagement.entity.ComplaintStatus;
import com.example.hostelmanagement.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for executing CRUD, status updates, and filter operations on Complaints.
 * Restricted to ADMIN role (enforced via SecurityConfig).
 */
@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    /**
     * Endpoint to submit a new complaint.
     */
    @PostMapping
    public ResponseEntity<ApiResponse> addComplaint(@Valid @RequestBody ComplaintRequest request) {
        ApiResponse response = complaintService.addComplaint(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Endpoint to list all complaints.
     */
    @GetMapping
    public ResponseEntity<List<ComplaintResponse>> getAllComplaints() {
        List<ComplaintResponse> complaints = complaintService.getAllComplaints();
        return ResponseEntity.ok(complaints);
    }

    /**
     * Endpoint to retrieve a complaint by database ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ComplaintResponse> getComplaintById(@PathVariable("id") Long id) {
        ComplaintResponse complaint = complaintService.getComplaintById(id);
        return ResponseEntity.ok(complaint);
    }

    /**
     * Endpoint to update the status and resolution remarks of a complaint.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateComplaint(
            @PathVariable("id") Long id,
            @Valid @RequestBody ComplaintUpdateRequest request
    ) {
        ApiResponse response = complaintService.updateComplaint(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to delete a complaint.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteComplaint(@PathVariable("id") Long id) {
        ApiResponse response = complaintService.deleteComplaint(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to list complaints filtered by status.
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ComplaintResponse>> getComplaintsByStatus(@PathVariable("status") ComplaintStatus status) {
        List<ComplaintResponse> complaints = complaintService.getComplaintsByStatus(status);
        return ResponseEntity.ok(complaints);
    }

    /**
     * Endpoint to list complaints filtered by category.
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ComplaintResponse>> getComplaintsByCategory(@PathVariable("category") ComplaintCategory category) {
        List<ComplaintResponse> complaints = complaintService.getComplaintsByCategory(category);
        return ResponseEntity.ok(complaints);
    }

    /**
     * Endpoint to list complaints submitted by a specific tenant.
     */
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<ComplaintResponse>> getComplaintsByTenant(@PathVariable("tenantId") Long tenantId) {
        List<ComplaintResponse> complaints = complaintService.getComplaintsByTenant(tenantId);
        return ResponseEntity.ok(complaints);
    }

    /**
     * Endpoint to retrieve complaint workflow statistics.
     */
    @GetMapping("/statistics")
    public ResponseEntity<ComplaintStatisticsResponse> getComplaintStatistics() {
        ComplaintStatisticsResponse statistics = complaintService.getComplaintStatistics();
        return ResponseEntity.ok(statistics);
    }
}
