package com.example.hostelmanagement.controller;

import com.example.hostelmanagement.dto.ApiResponse;
import com.example.hostelmanagement.dto.BedsAvailableResponse;
import com.example.hostelmanagement.dto.RoomRequest;
import com.example.hostelmanagement.dto.RoomResponse;
import com.example.hostelmanagement.entity.RoomStatus;
import com.example.hostelmanagement.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for executing CRUD, filter, and search operations on Rooms.
 * Only ADMIN users have authorization to invoke these mappings.
 */
@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    /**
     * Endpoint to create a new room.
     */
    @PostMapping
    public ResponseEntity<ApiResponse> addRoom(@Valid @RequestBody RoomRequest request) {
        ApiResponse response = roomService.addRoom(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Endpoint to retrieve all rooms.
     */
    @GetMapping
    public ResponseEntity<List<RoomResponse>> getAllRooms() {
        List<RoomResponse> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }

    /**
     * Endpoint to retrieve a specific room by its database ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> getRoomById(@PathVariable("id") Long id) {
        RoomResponse room = roomService.getRoomById(id);
        return ResponseEntity.ok(room);
    }

    /**
     * Endpoint to update an existing room.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateRoom(
            @PathVariable("id") Long id,
            @Valid @RequestBody RoomRequest request
    ) {
        ApiResponse response = roomService.updateRoom(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to delete a room.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteRoom(@PathVariable("id") Long id) {
        ApiResponse response = roomService.deleteRoom(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to search a room by its unique room number.
     */
    @GetMapping("/search")
    public ResponseEntity<RoomResponse> searchRoom(@RequestParam("roomNumber") String roomNumber) {
        RoomResponse room = roomService.searchRoom(roomNumber);
        return ResponseEntity.ok(room);
    }

    /**
     * Endpoint to retrieve rooms filtered by status.
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<RoomResponse>> filterRooms(@PathVariable("status") RoomStatus status) {
        List<RoomResponse> rooms = roomService.filterRooms(status);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/beds-available")
    public ResponseEntity<List<BedsAvailableResponse>> getBedsAvailable() {
        List<BedsAvailableResponse> bedsAvailable = roomService.getBedsAvailable();
        return ResponseEntity.ok(bedsAvailable);
    }
}
