package com.hotel.springbackend.repository;

import com.hotel.springbackend.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    @Query("SELECT c FROM Complaint c JOIN FETCH c.user WHERE c.user.userId = :userId")
    List<Complaint> findByUserUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM Complaint c JOIN FETCH c.user WHERE c.status = :status")
    List<Complaint> findByStatus(@Param("status") Complaint.Status status);

    @Query("SELECT c FROM Complaint c JOIN FETCH c.user WHERE c.roomNo = :roomNo")
    List<Complaint> findByRoomNo(@Param("roomNo") String roomNo);

    // Override findAll to always fetch user eagerly
    @Query("SELECT c FROM Complaint c JOIN FETCH c.user")
    List<Complaint> findAll();
    
    // Override findById to also fetch lazily joined booking
    @Query("SELECT c FROM Complaint c JOIN FETCH c.user LEFT JOIN FETCH c.booking WHERE c.id = :id")
    Optional<Complaint> findById(@Param("id") Long id);
}
