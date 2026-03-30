package com.hotel.springbackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hotel.springbackend.model.Room;

public interface RoomRepository extends JpaRepository<Room, Long>  {

	@Query("SELECT DISTINCT r.roomType FROM Room r")
	List<String> findDistinctRoomTypes();

	@Query("SELECT DISTINCT r FROM Room r LEFT JOIN FETCH r.bookings")
	List<Room> findAllWithBookings();

	@Query("SELECT r FROM Room r LEFT JOIN FETCH r.bookings WHERE r.id = :roomId")
    Optional<Room> findByIdWithBookings(@Param("roomId") Long roomId);
	
}
