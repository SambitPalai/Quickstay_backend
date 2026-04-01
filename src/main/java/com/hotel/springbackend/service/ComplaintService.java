package com.hotel.springbackend.service;

import com.hotel.springbackend.exception.ResourceNotFoundException;
import com.hotel.springbackend.model.BookedRoom;
import com.hotel.springbackend.model.Complaint;
import com.hotel.springbackend.model.User;
import com.hotel.springbackend.repository.BookingRepository;
import com.hotel.springbackend.repository.ComplaintRepository;
import com.hotel.springbackend.repository.UserRepository;
import com.hotel.springbackend.request.ComplaintRequest;
import com.hotel.springbackend.response.ComplaintResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplaintService implements IComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    // ── Raise a new complaint ─────────────────────────────────────────────────
    @Override
    @Transactional
    public ComplaintResponse raiseComplaint(ComplaintRequest request, String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        Complaint complaint = new Complaint();
        complaint.setMessage(request.getMessage());
        complaint.setRoomNo(request.getRoomNo());
        complaint.setCreatedAt(LocalDateTime.now());
        complaint.setStatus(Complaint.Status.OPEN);
        complaint.setUser(user);

        // Attach booking if provided
        if (request.getBookingId() != null) {
            BookedRoom booking = bookingRepository.findById(request.getBookingId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Booking not found: " + request.getBookingId()));
            complaint.setBooking(booking);
        }

        Complaint saved = complaintRepository.save(complaint);
        return toResponse(saved);
    }

    // ── User fetches their own complaints ─────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<ComplaintResponse> getMyComplaints(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        return complaintRepository.findByUserUserId(user.getUserId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ── Admin fetches ALL complaints ──────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<ComplaintResponse> getAllComplaints() {
        return complaintRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ── Admin updates complaint status ────────────────────────────────────────
    @Override
    @Transactional
    public ComplaintResponse updateStatus(Long complaintId, Complaint.Status newStatus) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Complaint not found: " + complaintId));

        complaint.setStatus(newStatus);
        return toResponse(complaintRepository.save(complaint));
    }

    // ── Admin filters complaints by status ───────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<ComplaintResponse> getComplaintsByStatus(Complaint.Status status) {
        return complaintRepository.findByStatus(status)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ── Entity → DTO mapping (keeps raw entities out of the API layer) ────────
    private ComplaintResponse toResponse(Complaint complaint) {
        ComplaintResponse response = new ComplaintResponse();

        response.setId(complaint.getId());
        response.setMessage(complaint.getMessage());
        response.setRoomNo(complaint.getRoomNo());
        response.setCreatedAt(complaint.getCreatedAt());
        response.setStatus(complaint.getStatus());

        // Safe user info
        User user = complaint.getUser();
        response.setUserId(user.getUserId());
        response.setUserName(user.getName());
        response.setUserEmail(user.getEmail());

        // Optional booking reference
        if (complaint.getBooking() != null) {
            response.setBookingId(complaint.getBooking().getBookingId());
            response.setBookingConfirmationCode(
                    complaint.getBooking().getBookingConfirmationCode());
        }

        return response;
    }
}
