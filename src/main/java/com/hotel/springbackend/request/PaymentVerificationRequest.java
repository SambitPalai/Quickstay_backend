package com.hotel.springbackend.request;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class PaymentVerificationRequest {
    // Razorpay payment details
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;

    // Booking details to save after verification
    private Long roomId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkInDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOutDate;
    private int numberOfAdults;
    private int numberOfChildren;
}
