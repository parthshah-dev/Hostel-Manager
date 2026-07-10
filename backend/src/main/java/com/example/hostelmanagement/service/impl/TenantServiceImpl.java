package com.example.hostelmanagement.service.impl;

import com.example.hostelmanagement.dto.ApiResponse;
import com.example.hostelmanagement.dto.TenantRequest;
import com.example.hostelmanagement.dto.TenantResponse;
import com.example.hostelmanagement.entity.Room;
import com.example.hostelmanagement.entity.RoomStatus;
import com.example.hostelmanagement.entity.Tenant;
import com.example.hostelmanagement.entity.User;
import com.example.hostelmanagement.exception.RoomCapacityExceededException;
import com.example.hostelmanagement.exception.RoomNotFoundException;
import com.example.hostelmanagement.exception.TenantAlreadyExistsException;
import com.example.hostelmanagement.exception.TenantNotFoundException;
import com.example.hostelmanagement.repository.RoomRepository;
import com.example.hostelmanagement.repository.TenantRepository;
import com.example.hostelmanagement.security.AuthenticationHelper;
import com.example.hostelmanagement.service.TenantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation containing core business flows for managing Tenants
 * and executing automatic Room occupancy recalculations.
 */
@Service
@Slf4j
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final RoomRepository roomRepository;
    private final AuthenticationHelper authenticationHelper;

    public TenantServiceImpl(
            TenantRepository tenantRepository,
            RoomRepository roomRepository,
            AuthenticationHelper authenticationHelper
    ) {
        this.tenantRepository = tenantRepository;
        this.roomRepository = roomRepository;
        this.authenticationHelper = authenticationHelper;
    }

    @Override
    @Transactional
    public ApiResponse addTenant(TenantRequest request) {
        User currentAdmin = authenticationHelper.getCurrentUser();
        log.info("Processing request by admin {} to add tenant: {}", currentAdmin.getEmail(), request.fullName());

        if (tenantRepository.existsByEmail(request.email())) {
            log.warn("Tenant creation block: Email {} already registered", request.email());
            throw new TenantAlreadyExistsException("Tenant already exists with email: " + request.email());
        }

        if (tenantRepository.existsByAadhaarNumber(request.aadhaarNumber())) {
            log.warn("Tenant creation block: Aadhaar {} already registered", request.aadhaarNumber());
            throw new TenantAlreadyExistsException("Tenant already exists with Aadhaar number: " + request.aadhaarNumber());
        }

        Room room = roomRepository.findById(request.roomId())
                .orElseThrow(() -> new RoomNotFoundException("Room not found with ID: " + request.roomId()));

        if (!room.getAdmin().getId().equals(currentAdmin.getId())) {
            log.warn("Access denied: Room ID {} does not belong to admin {}", room.getId(), currentAdmin.getEmail());
            throw new RoomNotFoundException("Room not found with ID: " + request.roomId());
        }

        long activeTenantsCount = tenantRepository.countByRoomIdAndActiveTrue(room.getId());
        if (activeTenantsCount >= room.getCapacity()) {
            log.warn("Tenant creation block: Room {} capacity exceeded", room.getRoomNumber());
            throw new RoomCapacityExceededException("Room capacity exceeded for room number: " + room.getRoomNumber());
        }

        Tenant tenant = Tenant.builder()
                .fullName(request.fullName())
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .aadhaarNumber(request.aadhaarNumber())
                .checkInDate(request.checkInDate())
                .securityDeposit(request.securityDeposit())
                .room(room)
                .active(true)
                .build();

        tenantRepository.save(tenant);
        log.info("Saved tenant profile. Recalculating Room status for: {}", room.getRoomNumber());

        updateRoomStatus(room);

        return new ApiResponse("Tenant added successfully.");
    }

    @Override
    public List<TenantResponse> getAllTenants() {
        User currentAdmin = authenticationHelper.getCurrentUser();
        log.info("Retrieving all tenants for admin {}", currentAdmin.getEmail());
        return tenantRepository.findByRoomAdmin(currentAdmin).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TenantResponse getTenantById(Long id) {
        User currentAdmin = authenticationHelper.getCurrentUser();
        log.info("Retrieving tenant by ID: {} for admin {}", id, currentAdmin.getEmail());
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new TenantNotFoundException("Tenant not found with ID: " + id));

        if (!tenant.getRoom().getAdmin().getId().equals(currentAdmin.getId())) {
            log.warn("Access denied: Tenant ID {} does not belong to admin {}", id, currentAdmin.getEmail());
            throw new TenantNotFoundException("Tenant not found with ID: " + id);
        }

        return mapToResponse(tenant);
    }

    @Override
    @Transactional
    public ApiResponse updateTenant(Long id, TenantRequest request) {
        User currentAdmin = authenticationHelper.getCurrentUser();
        log.info("Processing request by admin {} to update tenant with ID: {}", currentAdmin.getEmail(), id);

        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new TenantNotFoundException("Tenant not found with ID: " + id));

        if (!tenant.getRoom().getAdmin().getId().equals(currentAdmin.getId())) {
            log.warn("Access denied: Tenant ID {} does not belong to admin {}", id, currentAdmin.getEmail());
            throw new TenantNotFoundException("Tenant not found with ID: " + id);
        }

        if (!tenant.getEmail().equals(request.email()) && tenantRepository.existsByEmail(request.email())) {
            log.warn("Tenant update block: Email {} already registered", request.email());
            throw new TenantAlreadyExistsException("Tenant already exists with email: " + request.email());
        }

        if (!tenant.getAadhaarNumber().equals(request.aadhaarNumber()) && tenantRepository.existsByAadhaarNumber(request.aadhaarNumber())) {
            log.warn("Tenant update block: Aadhaar {} already registered", request.aadhaarNumber());
            throw new TenantAlreadyExistsException("Tenant already exists with Aadhaar number: " + request.aadhaarNumber());
        }

        Room oldRoom = tenant.getRoom();
        Room newRoom = roomRepository.findById(request.roomId())
                .orElseThrow(() -> new RoomNotFoundException("Room not found with ID: " + request.roomId()));

        if (!newRoom.getAdmin().getId().equals(currentAdmin.getId())) {
            log.warn("Access denied: Room ID {} does not belong to admin {}", newRoom.getId(), currentAdmin.getEmail());
            throw new RoomNotFoundException("Room not found with ID: " + request.roomId());
        }

        tenant.setFullName(request.fullName());
        tenant.setEmail(request.email());
        tenant.setPhoneNumber(request.phoneNumber());
        tenant.setAadhaarNumber(request.aadhaarNumber());
        tenant.setCheckInDate(request.checkInDate());
        tenant.setSecurityDeposit(request.securityDeposit());

        if (!oldRoom.getId().equals(newRoom.getId())) {
            long activeTenantsInNewRoom = tenantRepository.countByRoomIdAndActiveTrue(newRoom.getId());
            if (activeTenantsInNewRoom >= newRoom.getCapacity()) {
                log.warn("Tenant update block: New Room {} capacity exceeded", newRoom.getRoomNumber());
                throw new RoomCapacityExceededException("Room capacity exceeded for room number: " + newRoom.getRoomNumber());
            }
            tenant.setRoom(newRoom);
            tenantRepository.save(tenant);

            log.info("Updating Room statuses: old room={}, new room={}", oldRoom.getRoomNumber(), newRoom.getRoomNumber());
            updateRoomStatus(oldRoom);
            updateRoomStatus(newRoom);
        } else {
            tenantRepository.save(tenant);
        }

        log.info("Successfully updated tenant with ID: {}", id);
        return new ApiResponse("Tenant updated successfully.");
    }

    @Override
    @Transactional
    public ApiResponse deleteTenant(Long id) {
        User currentAdmin = authenticationHelper.getCurrentUser();
        log.info("Processing request by admin {} to delete tenant with ID: {}", currentAdmin.getEmail(), id);

        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new TenantNotFoundException("Tenant not found with ID: " + id));

        if (!tenant.getRoom().getAdmin().getId().equals(currentAdmin.getId())) {
            log.warn("Access denied: Tenant ID {} does not belong to admin {}", id, currentAdmin.getEmail());
            throw new TenantNotFoundException("Tenant not found with ID: " + id);
        }

        Room room = tenant.getRoom();
        tenantRepository.delete(tenant);
        log.info("Deleted tenant. Recalculating Room status for: {}", room.getRoomNumber());

        updateRoomStatus(room);

        return new ApiResponse("Tenant deleted successfully.");
    }

    @Override
    public List<TenantResponse> getTenantsByRoom(Long roomId) {
        User currentAdmin = authenticationHelper.getCurrentUser();
        log.info("Retrieving tenants for Room ID: {} for admin {}", roomId, currentAdmin.getEmail());
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Room not found with ID: " + roomId));

        if (!room.getAdmin().getId().equals(currentAdmin.getId())) {
            log.warn("Access denied: Room ID {} does not belong to admin {}", roomId, currentAdmin.getEmail());
            throw new RoomNotFoundException("Room not found with ID: " + roomId);
        }

        return tenantRepository.findByRoomId(roomId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TenantResponse searchTenantByEmail(String email) {
        User currentAdmin = authenticationHelper.getCurrentUser();
        log.info("Searching tenant by email: {} for admin {}", email, currentAdmin.getEmail());
        Tenant tenant = tenantRepository.findByEmail(email)
                .orElseThrow(() -> new TenantNotFoundException("Tenant not found with email: " + email));

        if (!tenant.getRoom().getAdmin().getId().equals(currentAdmin.getId())) {
            log.warn("Access denied: Tenant email {} does not belong to admin {}", email, currentAdmin.getEmail());
            throw new TenantNotFoundException("Tenant not found with email: " + email);
        }

        return mapToResponse(tenant);
    }

    @Override
    public List<TenantResponse> getActiveTenants() {
        User currentAdmin = authenticationHelper.getCurrentUser();
        log.info("Retrieving all active tenants for admin {}", currentAdmin.getEmail());
        return tenantRepository.findByActiveTrueAndRoomAdmin(currentAdmin).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void updateRoomStatus(Room room) {
        long activeTenantsCount = tenantRepository.countByRoomIdAndActiveTrue(room.getId());
        if (activeTenantsCount > 0) {
            room.setRoomStatus(RoomStatus.OCCUPIED);
        } else {
            room.setRoomStatus(RoomStatus.AVAILABLE);
        }
        roomRepository.save(room);
        log.info("Updated Room {} status to {}", room.getRoomNumber(), room.getRoomStatus());
    }

    private TenantResponse mapToResponse(Tenant tenant) {
        return new TenantResponse(
                tenant.getId(),
                tenant.getFullName(),
                tenant.getEmail(),
                tenant.getPhoneNumber(),
                tenant.getAadhaarNumber(),
                tenant.getCheckInDate(),
                tenant.getSecurityDeposit(),
                tenant.getRoom().getId(),
                tenant.getRoom().getRoomNumber(),
                tenant.getActive(),
                tenant.getCreatedAt(),
                tenant.getUpdatedAt()
        );
    }
}
