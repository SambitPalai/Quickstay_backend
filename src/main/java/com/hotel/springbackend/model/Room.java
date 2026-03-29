package com.hotel.springbackend.model;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class Room {

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;
	@Column(name = "room_no", unique = true)
	private String roomNo;
	private String roomType;
	private BigDecimal roomPrice;
	private boolean isBooked =false;
	@Lob
	private byte[] photo;
	
	@OneToMany(mappedBy="room",fetch = FetchType.LAZY, cascade= CascadeType.ALL)
	private List<BookedRoom> bookings;
	private static final SecureRandom random = new SecureRandom();
	
// Below provided was to protect from Null Pointer Exception ----
	public Room() {
		this.bookings= new ArrayList<>();
	}
	
	public void addBooking(BookedRoom booking) {
		if(bookings==null) {
			bookings= new ArrayList<>();
		}
		bookings.add(booking);
		booking.setRoom(this);
		isBooked = !bookings.isEmpty();
// Generates 10 digit bookingCode ----	
		String bookingCode= String.format("%010d", random.nextInt(1_000_000_000));
		booking.setBookingConfirmationCode(bookingCode);
	}

	
}
