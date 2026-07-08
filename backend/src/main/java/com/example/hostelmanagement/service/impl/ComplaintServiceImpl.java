package com.example.hostelmanagement.service.impl;

import com.example.hostelmanagement.dto.*;
import com.example.hostelmanagement.entity.Complaint;
import com.example.hostelmanagement.entity.ComplaintCategory;
import com.example.hostelmanagement.entity.ComplaintStatus;
import com.example.hostelmanagement.entity.Tenant;
import com.example.hostelmanagement.exception.ComplaintNotFoundException;
import com.example.hostelmanagement.exception.InvalidComplaintStatusException;
import com.example.hostelmanagement.exception.TenantNotFoundException;
import com.example.hostelmanagement.repository.ComplaintRepository;
import com.example.hostelmanagement.repository.TenantRepository;
import com.example.hostelmanagement.service.ComplaintService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation managing Complaint creation, transitions, and filters.
 */
@Service
@Slf4j
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final TenantRepository tenantRepository;

    public ComplaintServiceImpl(ComplaintRepository complaintRepository, TenantRepository tenantRepository) {
        this.complaintRepository = complaintRepository;
        this.tenantRepository = tenantRepository;
    }

    @Override
    @Transactional
    public ApiResponse addComplaint(ComplaintRequest request) {
        log.info("Processing complaint submission for Tenant ID: {}", request.tenantId());

        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new TenantNotFoundException("Tenant not found with ID: " + request.tenantId()));

        Complaint complaint = Complaint.builder()
                .tenant(tenant)
                .complaintCategory(request.complaintCategory())
                .description(request.description())
                .status(ComplaintStatus.OPEN)
                .resolutionRemarks(null)
                .build();

        complaintRepository.save(complaint);
        log.info("Successfully submitted complaint for tenant: {}", tenant.getFullName());

        return new ApiResponse("Complaint submitted successfully.");
    }

    @Override
    public List<ComplaintResponse> getAllComplaints() {
        log.info("Retrieving all complaints");
        return complaintRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ComplaintResponse getComplaintById(Long id) {
        log.info("Retrieving complaint by ID: {}", id);
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ComplaintNotFoundException("Complaint not found with ID: " + id));
        return mapToResponse(complaint);
    }

    @Override
    @Transactional
    public ApiResponse updateComplaint(Long id, ComplaintUpdateRequest request) {
        log.info("Processing request to update complaint with ID: {}", id);

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ComplaintNotFoundException("Complaint not found with ID: " + id));

        ComplaintStatus current = complaint.getStatus();
        ComplaintStatus target = request.status();

        if (current != target) {
            if (current == ComplaintStatus.OPEN && target != ComplaintStatus.IN_PROGRESS) {
                log.warn("Invalid complaint status transition attempted: {} -> {}", current, target);
                throw new InvalidComplaintStatusException("Complaint can only transition from OPEN to IN_PROGRESS");
            }
            if (current == ComplaintStatus.IN_PROGRESS && target != ComplaintStatus.RESOLVED) {
                log.warn("Invalid complaint status transition attempted: {} -> {}", current, target);
                throw new InvalidComplaintStatusException("Complaint can only transition from IN_PROGRESS to RESOLVED");
            }
            if (current == ComplaintStatus.RESOLVED) {
                log.warn("Invalid complaint status transition attempted from RESOLVED to {}", target);
                throw new InvalidComplaintStatusException("Cannot transition status of a RESOLVED complaint");
            }
        }

        if (target == ComplaintStatus.RESOLVED) {
            if (request.resolutionRemarks() == null || request.resolutionRemarks().trim().isEmpty()) {
                log.warn("Validation block: Resolution remarks required for resolving complaint ID {}", id);
                throw new InvalidComplaintStatusException("Resolution remarks are mandatory when status is RESOLVED");
            }
        }

        complaint.setStatus(target);
        complaint.setResolutionRemarks(request.resolutionRemarks());

        complaintRepository.save(complaint);
        log.info("Successfully updated complaint with ID: {} to status {}", id, target);

        return new ApiResponse("Complaint updated successfully.");
    }

    @Override
    @Transactional
    public ApiResponse deleteComplaint(Long id) {
        log.info("Processing request to delete complaint with ID: {}", id);
        if (!complaintRepository.existsById(id)) {
            throw new ComplaintNotFoundException("Complaint not found with ID: " + id);
        }
        complaintRepository.deleteById(id);
        log.info("Successfully deleted complaint with ID: {}", id);
        return new ApiResponse("Complaint deleted successfully.");
    }

    @Override
    public List<ComplaintResponse> getComplaintsByStatus(ComplaintStatus status) {
        log.info("Retrieving complaints for status: {}", status);
        return complaintRepository.findByStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ComplaintResponse> getComplaintsByCategory(ComplaintCategory category) {
        log.info("Retrieving complaints for category: {}", category);
        return complaintRepository.findByComplaintCategory(category).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ComplaintResponse> getComplaintsByTenant(Long tenantId) {
        log.info("Retrieving complaints for tenant ID: {}", tenantId);
        if (!tenantRepository.existsById(tenantId)) {
            throw new TenantNotFoundException("Tenant not found with ID: " + tenantId);
        }
        return complaintRepository.findByTenantId(tenantId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ComplaintStatisticsResponse getComplaintStatistics() {
        log.info("Retrieving complaint workflow statistics");
        long total = complaintRepository.count();
        long open = complaintRepository.countByStatus(ComplaintStatus.OPEN);
        long inProgress = complaintRepository.countByStatus(ComplaintStatus.IN_PROGRESS);
        long resolved = complaintRepository.countByStatus(ComplaintStatus.RESOLVED);
        return new ComplaintStatisticsResponse(total, open, inProgress, resolved);
    }

    private ComplaintResponse mapToResponse(Complaint complaint) {
        return new ComplaintResponse(
                complaint.getId(),
                complaint.getTenant().getId(),
                complaint.getTenant().getFullName(),
                complaint.getTenant().getRoom().getRoomNumber(),
                complaint.getComplaintCategory(),
                complaint.getDescription(),
                complaint.getStatus(),
                complaint.getResolutionRemarks(),
                complaint.getCreatedAt(),
                complaint.getUpdatedAt()
        );
    }
}
