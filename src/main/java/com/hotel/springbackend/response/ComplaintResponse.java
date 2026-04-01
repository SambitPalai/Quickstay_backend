package com.hotel.springbackend.response;

import com.hotel.springbackend.model.Complaint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComplaintResponse {

    private Long id;
    private String message;
    private String roomNo;
    private LocalDateTime createdAt;
    private Complaint.Status status;

    // User info (safe subset only — no password exposed)
    private Long userId;
    private String userName;
    private String userEmail;

    // Optional booking reference
    private Long bookingId;
    private String bookingConfirmationCode;
}
