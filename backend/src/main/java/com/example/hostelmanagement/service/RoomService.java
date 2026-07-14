package com.example.hostelmanagement.service;

import com.example.hostelmanagement.dto.ApiResponse;
import com.example.hostelmanagement.dto.BedsAvailableResponse;
import com.example.hostelmanagement.dto.RoomRequest;
import com.example.hostelmanagement.dto.RoomResponse;
import com.example.hostelmanagement.entity.RoomStatus;
import java.util.List;


public interface RoomService {


    ApiResponse addRoom(RoomRequest request);


    List<RoomResponse> getAllRooms();


    RoomResponse getRoomById(Long id);


    ApiResponse updateRoom(Long id, RoomRequest request);


    ApiResponse deleteRoom(Long id);


    RoomResponse searchRoom(String roomNumber);


    List<RoomResponse> filterRooms(RoomStatus status);

    List<BedsAvailableResponse> getBedsAvailable();
}
