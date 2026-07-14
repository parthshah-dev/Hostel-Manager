package com.example.hostelmanagement.repository;

import com.example.hostelmanagement.entity.Room;
import com.example.hostelmanagement.entity.RoomStatus;
import com.example.hostelmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    boolean existsByRoomNumberAndAdmin(String roomNumber, User admin);

    Optional<Room> findByRoomNumberAndAdmin(String roomNumber, User admin);

    long countByRoomStatusAndAdmin(RoomStatus roomStatus, User admin);

    List<Room> findByRoomStatusAndAdmin(RoomStatus roomStatus, User admin);

    List<Room> findAllByAdmin(User admin);

    long countByAdmin(User admin);
}
