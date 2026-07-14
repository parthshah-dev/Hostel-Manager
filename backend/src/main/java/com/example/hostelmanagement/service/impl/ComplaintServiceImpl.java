package com.example.hostelmanagement.service.impl;

import com.example.hostelmanagement.dto.*;
import com.example.hostelmanagement.entity.Complaint;
import com.example.hostelmanagement.entity.ComplaintCategory;
import com.example.hostelmanagement.entity.ComplaintStatus;
import com.example.hostelmanagement.entity.Tenant;
import com.example.hostelmanagement.entity.User;
import com.example.hostelmanagement.exception.ComplaintNotFoundException;
import com.example.hostelmanagement.exception.InvalidComplaintStatusException;
import com.example.hostelmanagement.exception.TenantNotFoundException;
import com.example.hostelmanagement.repository.ComplaintRepository;
import com.example.hostelmanagement.repository.TenantRepository;
import com.example.hostelmanagement.security.AuthenticationHelper;
import com.example.hostelmanagement.service.ComplaintService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final TenantRepository tenantRepository;
    private final AuthenticationHelper authenticationHelper;

    public ComplaintServiceImpl(
            ComplaintRepository complaintRepository,
            TenantRepository tenantRepository,
            AuthenticationHelper authenticationHelper
    ) {
        this.complaintRepository = complaintRepository;
        this.tenantRepository = tenantRepository;
        this.authenticationHelper = authenticationHelper;
    }

    @Override
    @Transactional
    public ApiResponse addComplaint(ComplaintRequest request) {
        User currentAdmin = authenticationHelper.getCurrentUser();
        log.info("Processing complaint submission by admin {} for Tenant ID: {}", currentAdmin.getEmail(), request.tenantId());

        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new TenantNotFoundException("Tenant not found with ID: " + request.tenantId()));

        if (!tenant.getRoom().getAdmin().getId().equals(currentAdmin.getId())) {
            log.warn("Access denied: Tenant ID {} does not belong to admin {}", request.tenantId(), currentAdmin.getEmail());
            throw new TenantNotFoundException("Tenant not found with ID: " + request.tenantId());
        }

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
        User currentAdmin = authenticationHelper.getCurrentUser();
        log.info("Retrieving all complaints for admin {}", currentAdmin.getEmail());
        return complaintRepository.findByTenantRoomAdmin(currentAdmin).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ComplaintResponse getComplaintById(Long id) {
        User currentAdmin = authenticationHelper.getCurrentUser();
        log.info("Retrieving complaint by ID: {} for admin {}", id, currentAdmin.getEmail());
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ComplaintNotFoundException("Complaint not found with ID: " + id));

        if (!complaint.getTenant().getRoom().getAdmin().getId().equals(currentAdmin.getId())) {
            log.warn("Access denied: Complaint ID {} does not belong to admin {}", id, currentAdmin.getEmail());
            throw new ComplaintNotFoundException("Complaint not found with ID: " + id);
        }

        return mapToResponse(complaint);
    }

    @Override
    @Transactional
    public ApiResponse updateComplaint(Long id, ComplaintUpdateRequest request) {
        User currentAdmin = authenticationHelper.getCurrentUser();
        log.info("Processing request by admin {} to update complaint with ID: {}", currentAdmin.getEmail(), id);

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ComplaintNotFoundException("Complaint not found with ID: " + id));

        if (!complaint.getTenant().getRoom().getAdmin().getId().equals(currentAdmin.getId())) {
            log.warn("Access denied: Complaint ID {} does not belong to admin {}", id, currentAdmin.getEmail());
            throw new ComplaintNotFoundException("Complaint not found with ID: " + id);
        }

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
        User currentAdmin = authenticationHelper.getCurrentUser();
        log.info("Processing request by admin {} to delete complaint with ID: {}", currentAdmin.getEmail(), id);

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new ComplaintNotFoundException("Complaint not found with ID: " + id));

        if (!complaint.getTenant().getRoom().getAdmin().getId().equals(currentAdmin.getId())) {
            log.warn("Access denied: Complaint ID {} does not belong to admin {}", id, currentAdmin.getEmail());
            throw new ComplaintNotFoundException("Complaint not found with ID: " + id);
        }

        complaintRepository.delete(complaint);
        log.info("Successfully deleted complaint with ID: {}", id);
        return new ApiResponse("Complaint deleted successfully.");
    }

    @Override
    public List<ComplaintResponse> getComplaintsByStatus(ComplaintStatus status) {
        User currentAdmin = authenticationHelper.getCurrentUser();
        log.info("Retrieving complaints for status: {} by admin {}", status, currentAdmin.getEmail());
        return complaintRepository.findByStatusAndTenantRoomAdmin(status, currentAdmin).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ComplaintResponse> getComplaintsByCategory(ComplaintCategory category) {
        User currentAdmin = authenticationHelper.getCurrentUser();
        log.info("Retrieving complaints for category: {} by admin {}", category, currentAdmin.getEmail());
        return complaintRepository.findByComplaintCategoryAndTenantRoomAdmin(category, currentAdmin).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ComplaintResponse> getComplaintsByTenant(Long tenantId) {
        User currentAdmin = authenticationHelper.getCurrentUser();
        log.info("Retrieving complaints for tenant ID: {} by admin {}", tenantId, currentAdmin.getEmail());

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new TenantNotFoundException("Tenant not found with ID: " + tenantId));

        if (!tenant.getRoom().getAdmin().getId().equals(currentAdmin.getId())) {
            log.warn("Access denied: Tenant ID {} does not belong to admin {}", tenantId, currentAdmin.getEmail());
            throw new TenantNotFoundException("Tenant not found with ID: " + tenantId);
        }

        return complaintRepository.findByTenantId(tenantId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ComplaintStatisticsResponse getComplaintStatistics() {
        User currentAdmin = authenticationHelper.getCurrentUser();
        log.info("Retrieving complaint workflow statistics for admin {}", currentAdmin.getEmail());
        long total = complaintRepository.countByTenantRoomAdmin(currentAdmin);
        long open = complaintRepository.countByStatusAndTenantRoomAdmin(ComplaintStatus.OPEN, currentAdmin);
        long inProgress = complaintRepository.countByStatusAndTenantRoomAdmin(ComplaintStatus.IN_PROGRESS, currentAdmin);
        long resolved = complaintRepository.countByStatusAndTenantRoomAdmin(ComplaintStatus.RESOLVED, currentAdmin);
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
