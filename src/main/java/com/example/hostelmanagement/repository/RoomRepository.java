package com.example.hostelmanagement.repository;

import com.example.hostelmanagement.entity.Room;
import com.example.hostelmanagement.entity.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Room} entities.
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    /**
     * Counts the number of rooms with the specified status.
     *
     * @param status The room status.
     * @return The count of rooms.
     */
    long countByStatus(RoomStatus status);
}
