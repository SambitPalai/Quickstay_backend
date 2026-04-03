package com.hotel.springbackend.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.hotel.springbackend.exception.InvalidBookingRequestException;
import com.hotel.springbackend.exception.ResourceNotFoundException;
import com.hotel.springbackend.model.BookedRoom;
import com.hotel.springbackend.model.Room;
import com.hotel.springbackend.repository.BookingRepository;
import com.hotel.springbackend.repository.RoomRepository;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService implements IBookingService {

    private final BookingRepository bookingRepository;
    private final IRoomService roomService;
    private final RoomRepository roomRepository; 

    @Override
    public List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
        return bookingRepository.findByRoomId(roomId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookedRoom> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public BookedRoom findByBookingConfirmationCode(String confirmationCode) {
        return bookingRepository.findByBookingConfirmationCode(confirmationCode);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookedRoom> getBookingsByUserEmail(String email) {
        return bookingRepository.findByGuestEmail(email);
    }

    @Override
    @Transactional
    public String saveBooking(Long roomId, BookedRoom bookingRequest) {
    	
    	if (bookingRequest.getCheckInDate() == null || bookingRequest.getCheckOutDate() == null) {
            throw new InvalidBookingRequestException("Check-in and Check-out dates are required.");
        }
        if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())) {
            throw new InvalidBookingRequestException("Check-in date must come before Check-out date.");
        }
        bookingRequest.setTotalNumOfGuests(
                bookingRequest.getNumberOfAdults() +
                bookingRequest.getNumberOfChildren()
        );
        
        Room room = roomService.getRoomById(roomId)
        .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        bookingRequest.setRoomNo(room.getRoomNo());
        
        List<BookedRoom> existingBookings = room.getBookings();
        boolean roomIsAvailable = roomIsAvailable(bookingRequest, existingBookings);
        if (roomIsAvailable) {
            room.addBooking(bookingRequest);
            bookingRepository.save(bookingRequest);
            roomRepository.save(room);
        } else {
            throw new InvalidBookingRequestException("Sorry, This room is not available for selected dates.");
        }
        return bookingRequest.getBookingConfirmationCode();
    }

    @Override
    public void cancelBooking(Long bookingId) {
    	 BookedRoom booking = bookingRepository.findById(bookingId)
    	            .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    	 Room room = booking.getRoom();
    	 bookingRepository.deleteById(bookingId);
    	 List<BookedRoom> remainingBookings = bookingRepository.findByRoomId(room.getId());
    	    if (remainingBookings.isEmpty()) {
    	        room.setBooked(false);
    	        roomRepository.save(room);
    	    }
    }

    private boolean roomIsAvailable(BookedRoom request, List<BookedRoom> bookings) {
        for (BookedRoom existing : bookings) {
            boolean overlap =
                    request.getCheckInDate().isBefore(existing.getCheckOutDate()) &&
                    existing.getCheckInDate().isBefore(request.getCheckOutDate());
            if (overlap) return false;
        }
        return true;
    }
}
