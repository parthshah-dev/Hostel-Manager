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


@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }


    @PostMapping
    public ResponseEntity<ApiResponse> addComplaint(@Valid @RequestBody ComplaintRequest request) {
        ApiResponse response = complaintService.addComplaint(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @GetMapping
    public ResponseEntity<List<ComplaintResponse>> getAllComplaints() {
        List<ComplaintResponse> complaints = complaintService.getAllComplaints();
        return ResponseEntity.ok(complaints);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ComplaintResponse> getComplaintById(@PathVariable("id") Long id) {
        ComplaintResponse complaint = complaintService.getComplaintById(id);
        return ResponseEntity.ok(complaint);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateComplaint(
            @PathVariable("id") Long id,
            @Valid @RequestBody ComplaintUpdateRequest request
    ) {
        ApiResponse response = complaintService.updateComplaint(id, request);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteComplaint(@PathVariable("id") Long id) {
        ApiResponse response = complaintService.deleteComplaint(id);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/status/{status}")
    public ResponseEntity<List<ComplaintResponse>> getComplaintsByStatus(@PathVariable("status") ComplaintStatus status) {
        List<ComplaintResponse> complaints = complaintService.getComplaintsByStatus(status);
        return ResponseEntity.ok(complaints);
    }


    @GetMapping("/category/{category}")
    public ResponseEntity<List<ComplaintResponse>> getComplaintsByCategory(@PathVariable("category") ComplaintCategory category) {
        List<ComplaintResponse> complaints = complaintService.getComplaintsByCategory(category);
        return ResponseEntity.ok(complaints);
    }


    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<ComplaintResponse>> getComplaintsByTenant(@PathVariable("tenantId") Long tenantId) {
        List<ComplaintResponse> complaints = complaintService.getComplaintsByTenant(tenantId);
        return ResponseEntity.ok(complaints);
    }


    @GetMapping("/statistics")
    public ResponseEntity<ComplaintStatisticsResponse> getComplaintStatistics() {
        ComplaintStatisticsResponse statistics = complaintService.getComplaintStatistics();
        return ResponseEntity.ok(statistics);
    }
}
