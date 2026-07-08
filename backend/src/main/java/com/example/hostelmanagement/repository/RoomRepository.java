package com.example.hostelmanagement.repository;

import com.example.hostelmanagement.entity.Room;
import com.example.hostelmanagement.entity.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link Room} entities.
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    /**
     * Checks if a room exists with the given room number.
     *
     * @param roomNumber The room number to check.
     * @return true if it exists, false otherwise.
     */
    boolean existsByRoomNumber(String roomNumber);

    /**
     * Finds a room by its room number.
     *
     * @param roomNumber The room number.
     * @return An Optional containing the Room, or empty if not found.
     */
    Optional<Room> findByRoomNumber(String roomNumber);

    /**
     * Counts the number of rooms with the specified room status.
     *
     * @param roomStatus The room status.
     * @return The count of rooms.
     */
    long countByRoomStatus(RoomStatus roomStatus);

    /**
     * Finds all rooms with the specified room status.
     *
     * @param roomStatus The room status.
     * @return A list of rooms matching the status.
     */
    List<Room> findByRoomStatus(RoomStatus roomStatus);
}
