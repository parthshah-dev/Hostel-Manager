package com.example.hostelmanagement.service;

import com.example.hostelmanagement.dto.ApiResponse;
import com.example.hostelmanagement.dto.RoomRequest;
import com.example.hostelmanagement.dto.RoomResponse;
import com.example.hostelmanagement.entity.RoomStatus;
import java.util.List;

/**
 * Service interface defining business processes for Room management.
 */
public interface RoomService {

    /**
     * Creates a new Room if the room number is unique.
     *
     * @param request The room details.
     * @return ApiResponse confirming successful creation.
     */
    ApiResponse addRoom(RoomRequest request);

    /**
     * Retrieves all rooms in the system.
     *
     * @return List of RoomResponse.
     */
    List<RoomResponse> getAllRooms();

    /**
     * Retrieves a room by its ID.
     *
     * @param id The ID of the room.
     * @return RoomResponse.
     */
    RoomResponse getRoomById(Long id);

    /**
     * Updates details of an existing room.
     *
     * @param id      The room ID.
     * @param request The updated room details.
     * @return ApiResponse.
     */
    ApiResponse updateRoom(Long id, RoomRequest request);

    /**
     * Deletes a room if it exists.
     *
     * @param id The room ID.
     * @return ApiResponse.
     */
    ApiResponse deleteRoom(Long id);

    /**
     * Searches for a room by its unique room number.
     *
     * @param roomNumber The room number.
     * @return RoomResponse.
     */
    RoomResponse searchRoom(String roomNumber);

    /**
     * Filters rooms by availability status.
     *
     * @param status The RoomStatus.
     * @return List of RoomResponse matching status.
     */
    List<RoomResponse> filterRooms(RoomStatus status);
}
