package com.hotel.springbackend.repository;

import com.hotel.springbackend.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    // User sees only their own complaints
    List<Complaint> findByUserUserId(Long userId);

    // Admin sees complaints filtered by status
    List<Complaint> findByStatus(Complaint.Status status);

    // Admin sees complaints for a specific room
    List<Complaint> findByRoomNo(String roomNo);
}
