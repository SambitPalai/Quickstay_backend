package com.hotel.springbackend.exception;

public class InvalidBookingRequestException extends RuntimeException {
	public InvalidBookingRequestException(String message) {
		super(message);
	}
}
