package com.hotel.springbackend.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.hotel.springbackend.exception.InvalidBookingRequestException;
import com.hotel.springbackend.exception.ResourceNotFoundException;
import com.hotel.springbackend.model.BookedRoom;
import com.hotel.springbackend.model.Room;
import com.hotel.springbackend.model.User;
import com.hotel.springbackend.response.BookingResponse;
import com.hotel.springbackend.response.RoomResponse;
import com.hotel.springbackend.service.IBookingService;
import com.hotel.springbackend.service.IRoomService;
import com.hotel.springbackend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final IBookingService bookingService;
    private final IRoomService roomService;
    private final UserRepository userRepository;

    // --- Admin: get all bookings ------------------------
    @GetMapping("/all-bookings")
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        List<BookedRoom> bookings = bookingService.getAllBookings();
        List<BookingResponse> bookingResponses = new ArrayList<>();
        for (BookedRoom booking : bookings) {
            bookingResponses.add(getBookingResponse(booking));
        }
        return ResponseEntity.ok(bookingResponses);
    }

    // -- User: get OWN bookings — email taken from JWT token directly -------
    // No email in URL = no @ symbol problem
    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingResponse>> getMyBookings(
            @AuthenticationPrincipal UserDetails currentUser) {

        String email = currentUser.getUsername();   // comes from JWT token
        List<BookedRoom> bookings = bookingService.getBookingsByUserEmail(email);
        List<BookingResponse> responses = new ArrayList<>();
        for (BookedRoom booking : bookings) {
            responses.add(getBookingResponse(booking));
        }
        return ResponseEntity.ok(responses);
    }
    
    // --- Admin: get bookings for ANY specific user by email ----------------
    // Uses request param to avoid @ symbol issue in path variable
    @GetMapping("/user/bookings")
    public ResponseEntity<?> getBookingsByUserEmail(
            @RequestParam String email,
            @AuthenticationPrincipal UserDetails currentUser) {

        boolean isAdminOrOwner = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || 
                				a.getAuthority().equals("ROLE_OWNER")
                			);
        if (!isAdminOrOwner) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied.");
        }

        List<BookedRoom> bookings = bookingService.getBookingsByUserEmail(email);
        List<BookingResponse> responses = new ArrayList<>();
        for (BookedRoom booking : bookings) {
            responses.add(getBookingResponse(booking));
        }
        return ResponseEntity.ok(responses);
    }

    // -------- Get booking by confirmation code ---------------------
    @GetMapping("/confirmation/{confirmationCode}")
    public ResponseEntity<?> getBookingByConfirmationCode(
            @PathVariable String confirmationCode) {
        try {
            BookedRoom booking = bookingService.findByBookingConfirmationCode(confirmationCode);
            return ResponseEntity.ok(getBookingResponse(booking));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // -------------- Save a new booking -------------------
 // ── Save a new booking ────────────────────────────────────────────────
    @PostMapping("/room/{roomId}/booking")
    public ResponseEntity<?> saveBooking(
            @PathVariable Long roomId,
            @RequestBody BookedRoom bookingRequest,
            @AuthenticationPrincipal UserDetails currentUser) {  // ← add this
        try {
            // Always use logged-in user's details 
            // Ignore whatever guestEmail/guestFullName was sent in the body
            User loggedInUser = userRepository.findByEmail(currentUser.getUsername())
                    .orElseThrow();
            bookingRequest.setGuestEmail(loggedInUser.getEmail());
            bookingRequest.setGuestFullName(loggedInUser.getName());
            
            String confirmationCode = bookingService.saveBooking(roomId, bookingRequest);
            return ResponseEntity.ok(
                    "Room booked successfully! Your confirmation code is: " + confirmationCode);
        } catch (InvalidBookingRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ------------ Cancel a booking --------------------
    @DeleteMapping("/booking/{bookingId}/delete")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.noContent().build();
    }

    //  -------------- Helper ------------------- 
    private BookingResponse getBookingResponse(BookedRoom booking) {
        Room theRoom = roomService.getRoomById(booking.getRoom().getId()).get();
        RoomResponse room = new RoomResponse(
                theRoom.getId(), 
                theRoom.getRoomNo(),
                theRoom.getRoomType(), 
                theRoom.getRoomPrice(),
                theRoom.isBooked(),
                theRoom.getPhoto(),
                null );
        return new BookingResponse(
                booking.getBookingId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getGuestFullName(),
                booking.getGuestEmail(),
                booking.getNumberOfAdults(),
                booking.getNumberOfChildren(),
                booking.getTotalNumOfGuests(),
                booking.getBookingConfirmationCode(),
                booking.getRoomNo(), 
                room );
    }
}
