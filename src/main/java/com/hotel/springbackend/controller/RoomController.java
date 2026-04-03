package com.hotel.springbackend.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hotel.springbackend.exception.PhotoRetrievalException;
import com.hotel.springbackend.exception.ResourceNotFoundException;
import com.hotel.springbackend.model.BookedRoom;
import com.hotel.springbackend.model.Room;
import com.hotel.springbackend.response.BookingResponse;
import com.hotel.springbackend.response.RoomResponse;
import com.hotel.springbackend.service.BookingService;
import com.hotel.springbackend.service.IRoomService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
@CrossOrigin(origins = {
    "http://localhost:5173",
    "https://quickstay-web.vercel.app"
})
public class RoomController  {
	
	private final IRoomService roomService;
	
	@PostMapping("/add/new-room")
	public ResponseEntity<RoomResponse> addNewRoom(
			@RequestParam("photo") MultipartFile photo, 
			@RequestParam("roomType") String roomType, 
			@RequestParam("roomPrice") BigDecimal roomPrice,
			@RequestParam("roomNo") String roomNo) 
			throws IOException {
		Room savedRoom = roomService.addNewRoom(photo, roomType, roomPrice, roomNo);
		RoomResponse response = getRoomResponse(savedRoom);
		return ResponseEntity.ok(response);
	
	}
  
	@GetMapping("/room/types")
	public List<String> getRoomtypes() {
		return roomService.getAllRoomTypes();
	}
	
	@GetMapping("/all-rooms")
	public ResponseEntity<List<RoomResponse>> getAllRooms() throws PhotoRetrievalException {
	    List<Room> rooms = roomService.getAllRoomsWithBookings();
	    List<RoomResponse> roomResponses = new ArrayList<>();
	    for (Room room : rooms) {
	        RoomResponse roomResponse = getRoomResponse(room);
	        roomResponses.add(roomResponse);
	    }
	    return ResponseEntity.ok(roomResponses);
	}

	@DeleteMapping("/delete/room/{roomId}")
	public ResponseEntity<Void> deleteRoom(@PathVariable Long roomId ){
		roomService.deleteRoom(roomId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@PutMapping("/update/{roomId}")
	public ResponseEntity<RoomResponse> updateRoom(
			@PathVariable Long roomId,
			@RequestParam(required = false) String roomNo,
			@RequestParam(required = false) String roomType, 
			@RequestParam(required = false) BigDecimal roomPrice, 
			@RequestParam(required = false) MultipartFile photo) throws ResourceNotFoundException, IOException {
		
		byte[] photoBytes = photo != null && !photo.isEmpty()?
				photo.getBytes() : roomService.getRoomPhotoByRoomId(roomId);
	
		Room theRoom = roomService.updateRoom(roomId, roomType, roomPrice, roomNo, photoBytes);
	    RoomResponse roomResponse = getRoomResponse(theRoom);
	    return ResponseEntity.ok(roomResponse);
	}
	
	@GetMapping("/room/{roomId}")
	public ResponseEntity<RoomResponse> getRoomById(@PathVariable Long roomId){
	    Room room = roomService.getRoomById(roomId)
	            .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
	    RoomResponse roomResponse = getRoomResponse(room);
	    return ResponseEntity.ok(roomResponse);
	}
	
	private RoomResponse getRoomResponse(Room room) {
		List<BookedRoom> bookings =  room.getBookings();
		
		if (bookings == null) {
			bookings = new ArrayList<>();
		}
		
		
		List<BookingResponse> bookingInfo = bookings.stream()
				.map(booking -> new BookingResponse(
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
						null 
					)).toList();
		
		return new RoomResponse(room.getId(), 
								room.getRoomNo(), 
								room.getRoomType(), 
								room.getRoomPrice(), 
								room.isBooked(), 
								room.getPhoto(), 
								bookingInfo);
	}

	
}
