package com.hotel.springbackend.service;

import java.util.List;

import com.hotel.springbackend.model.BookedRoom;

public interface IBookingService {

	List<BookedRoom> getAllBookings();
	
	List<BookedRoom> getAllBookingsByRoomId(Long roomId);
	
	List<BookedRoom> getBookingsByUserEmail(String email);

	BookedRoom findByBookingConfirmationCode(String confirmationCode);

	String saveBooking(Long roomId, BookedRoom bookingRequest);

	void cancelBooking(Long bookingId);
	
}
