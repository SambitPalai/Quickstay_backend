package com.hotel.springbackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.springbackend.model.BookedRoom;

public interface BookingRepository extends JpaRepository<BookedRoom, Long>{

	List<BookedRoom> findByRoomId(Long roomId);

	BookedRoom findByBookingConfirmationCode(String confirmationCode);
	
	List<BookedRoom> findByGuestEmail(String guestEmail);

}
