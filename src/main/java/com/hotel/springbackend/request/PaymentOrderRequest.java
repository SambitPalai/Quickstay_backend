package com.hotel.springbackend.request;

import java.time.LocalDate;
import lombok.Data;

@Data
public class PaymentOrderRequest {
    private Long roomId;
    private double amount;          // in rupees — backend will convert to paise
    private String guestFullName;
    private String guestEmail;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int numberOfAdults;
    private int numberOfChildren;
}