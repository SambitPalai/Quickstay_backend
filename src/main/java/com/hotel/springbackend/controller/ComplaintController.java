package com.hotel.springbackend.controller;

import com.hotel.springbackend.model.Complaint;
import com.hotel.springbackend.request.ComplaintRequest;
import com.hotel.springbackend.request.UpdateStatusRequest;
import com.hotel.springbackend.response.ComplaintResponse;
import com.hotel.springbackend.service.IComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/complaints")
@CrossOrigin(origins = {"http://localhost:5173" , "https://quickstay-web.vercel.app"})
public class ComplaintController {

    private final IComplaintService complaintService;

    // ── USER: raise a new complaint ───────────────────────────────────────────
    // POST /complaints/raise
    @PostMapping("/raise")
    public ResponseEntity<ComplaintResponse> raiseComplaint(
            @RequestBody ComplaintRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {

        ComplaintResponse response = complaintService.raiseComplaint(
                request, currentUser.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ── USER: view only their own complaints ──────────────────────────────────
    // GET /complaints/my-complaints
    @GetMapping("/my-complaints")
    public ResponseEntity<List<ComplaintResponse>> getMyComplaints(
            @AuthenticationPrincipal UserDetails currentUser) {

        return ResponseEntity.ok(
                complaintService.getMyComplaints(currentUser.getUsername()));
    }

    // ── ADMIN / OWNER: view ALL complaints ────────────────────────────────────
    // GET /complaints/all
    @GetMapping("/all")
    public ResponseEntity<List<ComplaintResponse>> getAllComplaints() {
        return ResponseEntity.ok(complaintService.getAllComplaints());
    }

    // ── ADMIN / OWNER: filter complaints by status ────────────────────────────
    // GET /complaints/filter?status=OPEN
    @GetMapping("/filter")
    public ResponseEntity<List<ComplaintResponse>> getByStatus(
            @RequestParam Complaint.Status status) {
        return ResponseEntity.ok(complaintService.getComplaintsByStatus(status));
    }

    // ── ADMIN / OWNER: update complaint status ────────────────────────────────
    // PUT /complaints/{id}/status
    @PutMapping("/{id}/status")
    public ResponseEntity<ComplaintResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusRequest request) {

        return ResponseEntity.ok(
                complaintService.updateStatus(id, request.getStatus()));
    }
}
