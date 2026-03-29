package com.hotel.springbackend.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.hotel.springbackend.exception.ResourceNotFoundException;
import com.hotel.springbackend.model.Room;

public interface IRoomService {

	List<String> getAllRoomTypes();

	byte[] getRoomPhotoByRoomId(Long roomId) throws ResourceNotFoundException;

	List<Room> getAllRooms();

	void deleteRoom(Long roomId);

	Optional<Room> getRoomById(Long roomId);
	
	Room addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice, String roomNo ) throws IOException;
	Room updateRoom(Long roomId, String roomType, BigDecimal roomPrice, String roomNo, byte[] photoBytes);

	List<Room> getAllRoomsWithBookings();

}
