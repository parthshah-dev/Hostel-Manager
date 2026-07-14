package com.example.hostelmanagement.service.impl;

import com.example.hostelmanagement.dto.ApiResponse;
import com.example.hostelmanagement.dto.BedsAvailableResponse;
import com.example.hostelmanagement.dto.RoomRequest;
import com.example.hostelmanagement.dto.RoomResponse;
import com.example.hostelmanagement.entity.Room;
import com.example.hostelmanagement.entity.RoomStatus;
import com.example.hostelmanagement.entity.User;
import com.example.hostelmanagement.exception.RoomAlreadyExistsException;
import com.example.hostelmanagement.exception.RoomNotFoundException;
import com.example.hostelmanagement.repository.RoomRepository;
import com.example.hostelmanagement.repository.TenantRepository;
import com.example.hostelmanagement.security.AuthenticationHelper;
import com.example.hostelmanagement.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final TenantRepository tenantRepository;
    private final AuthenticationHelper authenticationHelper;

    public RoomServiceImpl(
            RoomRepository roomRepository,
            TenantRepository tenantRepository,
            AuthenticationHelper authenticationHelper
    ) {
        this.roomRepository = roomRepository;
        this.tenantRepository = tenantRepository;
        this.authenticationHelper = authenticationHelper;
    }

    @Override
    @Transactional
    public ApiResponse addRoom(RoomRequest request) {
        User currentAdmin = authenticationHelper.getCurrentUser();
        log.info("Processing request by admin {} to add room number: {}", currentAdmin.getEmail(), request.roomNumber());

        if (roomRepository.existsByRoomNumberAndAdmin(request.roomNumber(), currentAdmin)) {
            log.warn("Room creation block: Room number {} already exists for admin {}", request.roomNumber(), currentAdmin.getEmail());
            throw new RoomAlreadyExistsException("Room already exists with room number: " + request.roomNumber());
        }

        Room room = Room.builder()
                .roomNumber(request.roomNumber())
                .roomType(request.roomType())
                .capacity(request.capacity())
                .monthlyRent(request.monthlyRent())
                .roomStatus(request.roomStatus())
                .admin(currentAdmin)
                .build();

        roomRepository.save(room);
        log.info("Successfully created room: {}", request.roomNumber());

        return new ApiResponse("Room created successfully.");
    }

    @Override
    public List<RoomResponse> getAllRooms() {
        User currentAdmin = authenticationHelper.getCurrentUser();
        log.info("Retrieving all rooms for admin {}", currentAdmin.getEmail());
        return roomRepository.findAllByAdmin(currentAdmin).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RoomResponse getRoomById(Long id) {
        User currentAdmin = authenticationHelper.getCurrentUser();
        log.info("Retrieving room by ID: {} for admin {}", id, currentAdmin.getEmail());
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException("Room not found with ID: " + id));

        if (!room.getAdmin().getId().equals(currentAdmin.getId())) {
            log.warn("Access denied: Room ID {} does not belong to admin {}", id, currentAdmin.getEmail());
            throw new RoomNotFoundException("Room not found with ID: " + id);
        }

        return mapToResponse(room);
    }

    @Override
    @Transactional
    public ApiResponse updateRoom(Long id, RoomRequest request) {
        User currentAdmin = authenticationHelper.getCurrentUser();
        log.info("Processing request by admin {} to update room with ID: {}", currentAdmin.getEmail(), id);

        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException("Room not found with ID: " + id));

        if (!room.getAdmin().getId().equals(currentAdmin.getId())) {
            log.warn("Access denied: Room ID {} does not belong to admin {}", id, currentAdmin.getEmail());
            throw new RoomNotFoundException("Room not found with ID: " + id);
        }


        if (!room.getRoomNumber().equals(request.roomNumber()) && roomRepository.existsByRoomNumberAndAdmin(request.roomNumber(), currentAdmin)) {
            log.warn("Room update block: Room number {} already exists on another room record for admin {}", request.roomNumber(), currentAdmin.getEmail());
            throw new RoomAlreadyExistsException("Room already exists with room number: " + request.roomNumber());
        }

        room.setRoomNumber(request.roomNumber());
        room.setRoomType(request.roomType());
        room.setCapacity(request.capacity());
        room.setMonthlyRent(request.monthlyRent());
        room.setRoomStatus(request.roomStatus());

        roomRepository.save(room);
        log.info("Successfully updated room with ID: {}", id);

        return new ApiResponse("Room updated successfully.");
    }

    @Override
    @Transactional
    public ApiResponse deleteRoom(Long id) {
        User currentAdmin = authenticationHelper.getCurrentUser();
        log.info("Processing request by admin {} to delete room with ID: {}", currentAdmin.getEmail(), id);

        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException("Room not found with ID: " + id));

        if (!room.getAdmin().getId().equals(currentAdmin.getId())) {
            log.warn("Access denied: Room ID {} does not belong to admin {}", id, currentAdmin.getEmail());
            throw new RoomNotFoundException("Room not found with ID: " + id);
        }

        roomRepository.delete(room);
        log.info("Successfully deleted room with ID: {}", id);

        return new ApiResponse("Room deleted successfully.");
    }

    @Override
    public RoomResponse searchRoom(String roomNumber) {
        User currentAdmin = authenticationHelper.getCurrentUser();
        log.info("Searching for room number: {} for admin {}", roomNumber, currentAdmin.getEmail());
        Room room = roomRepository.findByRoomNumberAndAdmin(roomNumber, currentAdmin)
                .orElseThrow(() -> new RoomNotFoundException("Room not found with room number: " + roomNumber));
        return mapToResponse(room);
    }

    @Override
    public List<RoomResponse> filterRooms(RoomStatus status) {
        User currentAdmin = authenticationHelper.getCurrentUser();
        log.info("Filtering rooms by status: {} for admin {}", status, currentAdmin.getEmail());
        return roomRepository.findByRoomStatusAndAdmin(status, currentAdmin).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private RoomResponse mapToResponse(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getRoomNumber(),
                room.getRoomType(),
                room.getCapacity(),
                room.getMonthlyRent(),
                room.getRoomStatus(),
                room.getCreatedAt(),
                room.getUpdatedAt()
        );
    }

    @Override
    public List<BedsAvailableResponse> getBedsAvailable() {
        User currentAdmin = authenticationHelper.getCurrentUser();
        log.info("Retrieving rooms with available beds for admin {}", currentAdmin.getEmail());
        return roomRepository.findAllByAdmin(currentAdmin).stream()
                .map(room -> {
                    long activeTenants = tenantRepository.countByRoomIdAndActiveTrue(room.getId());
                    int bedsAvailable = room.getCapacity() - (int) activeTenants;
                    return new BedsAvailableResponse(room.getId(), room.getRoomNumber(), bedsAvailable, room.getMonthlyRent());
                })
                .filter(response -> response.bedsAvailable() > 0)
                .collect(Collectors.toList());
    }
}
