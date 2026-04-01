package com.hotel.springbackend.request;

import lombok.Data;

@Data
public class ComplaintRequest {

    // The complaint text — required
    private String message;

    // Room number being complained about — required
    private String roomNo;

    // Optional: link to a specific booking
    private Long bookingId;
}
