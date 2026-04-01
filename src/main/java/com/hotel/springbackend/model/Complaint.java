package com.hotel.springbackend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "complaints")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The actual complaint message
    @Column(nullable = false, length = 1000)
    private String message;

    // Room number the complaint is about (denormalized for easy display)
    @Column(name = "room_no")
    private String roomNo;

    // Auto-set on creation
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Lifecycle tracking
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.OPEN;

    // --- Relationships ---

    // Who raised the complaint
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Which booking this is about (optional — guest might complain without a booking)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private BookedRoom booking;

    public enum Status {
        OPEN,
        IN_PROGRESS,
        RESOLVED
    }
}
