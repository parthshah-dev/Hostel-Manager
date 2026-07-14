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


@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }


    @PostMapping
    public ResponseEntity<ApiResponse> addRoom(@Valid @RequestBody RoomRequest request) {
        ApiResponse response = roomService.addRoom(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @GetMapping
    public ResponseEntity<List<RoomResponse>> getAllRooms() {
        List<RoomResponse> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }


    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> getRoomById(@PathVariable("id") Long id) {
        RoomResponse room = roomService.getRoomById(id);
        return ResponseEntity.ok(room);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateRoom(
            @PathVariable("id") Long id,
            @Valid @RequestBody RoomRequest request
    ) {
        ApiResponse response = roomService.updateRoom(id, request);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteRoom(@PathVariable("id") Long id) {
        ApiResponse response = roomService.deleteRoom(id);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/search")
    public ResponseEntity<RoomResponse> searchRoom(@RequestParam("roomNumber") String roomNumber) {
        RoomResponse room = roomService.searchRoom(roomNumber);
        return ResponseEntity.ok(room);
    }


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
