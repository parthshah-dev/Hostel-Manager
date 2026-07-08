package com.example.hostelmanagement.service.impl;

import com.example.hostelmanagement.dto.ApiResponse;
import com.example.hostelmanagement.dto.RoomRequest;
import com.example.hostelmanagement.dto.RoomResponse;
import com.example.hostelmanagement.entity.Room;
import com.example.hostelmanagement.entity.RoomStatus;
import com.example.hostelmanagement.exception.RoomAlreadyExistsException;
import com.example.hostelmanagement.exception.RoomNotFoundException;
import com.example.hostelmanagement.repository.RoomRepository;
import com.example.hostelmanagement.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation offering transaction boundaries and business validation rules for Room operations.
 */
@Service
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    @Transactional
    public ApiResponse addRoom(RoomRequest request) {
        log.info("Processing request to add room number: {}", request.roomNumber());

        if (roomRepository.existsByRoomNumber(request.roomNumber())) {
            log.warn("Room creation block: Room number {} already exists", request.roomNumber());
            throw new RoomAlreadyExistsException("Room already exists with room number: " + request.roomNumber());
        }

        Room room = Room.builder()
                .roomNumber(request.roomNumber())
                .roomType(request.roomType())
                .capacity(request.capacity())
                .monthlyRent(request.monthlyRent())
                .roomStatus(request.roomStatus())
                .build();

        roomRepository.save(room);
        log.info("Successfully created room: {}", request.roomNumber());

        return new ApiResponse("Room created successfully.");
    }

    @Override
    public List<RoomResponse> getAllRooms() {
        log.info("Retrieving all rooms");
        return roomRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RoomResponse getRoomById(Long id) {
        log.info("Retrieving room by ID: {}", id);
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException("Room not found with ID: " + id));
        return mapToResponse(room);
    }

    @Override
    @Transactional
    public ApiResponse updateRoom(Long id, RoomRequest request) {
        log.info("Processing request to update room with ID: {}", id);

        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException("Room not found with ID: " + id));

        // Uniqueness check for room number if modified
        if (!room.getRoomNumber().equals(request.roomNumber()) && roomRepository.existsByRoomNumber(request.roomNumber())) {
            log.warn("Room update block: Room number {} already exists on another room record", request.roomNumber());
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
        log.info("Processing request to delete room with ID: {}", id);

        if (!roomRepository.existsById(id)) {
            log.warn("Room deletion block: Room with ID {} not found", id);
            throw new RoomNotFoundException("Room not found with ID: " + id);
        }

        roomRepository.deleteById(id);
        log.info("Successfully deleted room with ID: {}", id);

        return new ApiResponse("Room deleted successfully.");
    }

    @Override
    public RoomResponse searchRoom(String roomNumber) {
        log.info("Searching for room number: {}", roomNumber);
        Room room = roomRepository.findByRoomNumber(roomNumber)
                .orElseThrow(() -> new RoomNotFoundException("Room not found with room number: " + roomNumber));
        return mapToResponse(room);
    }

    @Override
    public List<RoomResponse> filterRooms(RoomStatus status) {
        log.info("Filtering rooms by status: {}", status);
        return roomRepository.findByRoomStatus(status).stream()
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
}
