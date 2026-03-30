package com.hotel.springbackend.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hotel.springbackend.exception.ResourceNotFoundException;
import com.hotel.springbackend.model.Room;
import com.hotel.springbackend.repository.RoomRepository;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements IRoomService{

	private final RoomRepository roomRepository;
	
	@Override
	public Room addNewRoom(MultipartFile file, String roomType, BigDecimal roomPrice, String roomNo ) throws IOException {
		Room room = new Room();
		room.setRoomNo(roomNo);
		room.setRoomType(roomType);
		room.setRoomPrice(roomPrice);
		if(!file.isEmpty()) {
			room.setPhoto(file.getBytes());
		}
		return roomRepository.save(room);
	}

	@Override
	public List<String> getAllRoomTypes() {
		return roomRepository.findDistinctRoomTypes();
	}

	@Override
	public byte[] getRoomPhotoByRoomId(Long roomId) throws ResourceNotFoundException {
		Optional<Room> theRoom = roomRepository.findById(roomId);
		if(theRoom.isEmpty()) {
			throw new ResourceNotFoundException("Sorry, Room not found!");
		}
		Room room = theRoom.get();
		return room.getPhoto();
	}

	@Override
	public List<Room> getAllRooms() {
		return roomRepository.findAll();
	}

	@Override
	public void deleteRoom(Long roomId) {
		Optional<Room> theRoom = roomRepository.findById(roomId);
		if(theRoom.isPresent()) {
			roomRepository.deleteById(roomId);
		}
	}
	
	@Override
	@Transactional
	public Room updateRoom(Long roomId, String roomType, BigDecimal roomPrice, String roomNo, byte[] photoBytes) {
		Room room = roomRepository.findById(roomId)
				.orElseThrow(() -> new ResourceNotFoundException("Room not found"));
		if (roomNo != null) room.setRoomNo(roomNo); 
		if (roomType != null) room.setRoomType(roomType); 
		if (roomPrice != null) room.setRoomPrice(roomPrice);
		if (photoBytes != null && photoBytes.length > 0) {
			room.setPhoto(photoBytes);
		}
		return roomRepository.save(room);
	}

	@Override
	@Transactional
	public Optional<Room> getRoomById(Long roomId) {
		return roomRepository.findByIdWithBookings(roomId);
	}

	@Override
	public List<Room> getAllRoomsWithBookings() {
		return roomRepository.findAllWithBookings();
	}


}
