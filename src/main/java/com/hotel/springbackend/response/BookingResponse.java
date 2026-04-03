package com.hotel.springbackend.response;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {

	private Long bookingId;
	@JsonFormat(pattern = "yyyy-MM-dd")  
	private LocalDate checkInDate;
	@JsonFormat(pattern = "yyyy-MM-dd")  
	private LocalDate checkOutDate;
	private String guestFullName;
	private String guestEmail;
	private int numOfAdults;
	private int numOfChildren;
	private int totalNumOfGuests;
	private String bookingConfirmationCode;
	private String roomNo; 
	private RoomResponse room;
	
	public BookingResponse(Long bookingId, LocalDate checkInDate, LocalDate checkOutDate,
			String bookingConfirmationCode) {
		this.bookingId = bookingId;
		this.checkInDate = checkInDate;
		this.checkOutDate = checkOutDate;
		this.bookingConfirmationCode = bookingConfirmationCode;
	}
	
	
}
