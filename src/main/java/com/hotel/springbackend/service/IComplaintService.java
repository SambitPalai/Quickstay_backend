package com.hotel.springbackend.service;

import com.hotel.springbackend.model.Complaint;
import com.hotel.springbackend.request.ComplaintRequest;
import com.hotel.springbackend.response.ComplaintResponse;

import java.util.List;

public interface IComplaintService {

    // User raises a new complaint
    ComplaintResponse raiseComplaint(ComplaintRequest request, String userEmail);

    // User fetches only their own complaints
    List<ComplaintResponse> getMyComplaints(String userEmail);

    // Admin / Owner fetches ALL complaints
    List<ComplaintResponse> getAllComplaints();

    // Admin / Owner updates complaint status
    ComplaintResponse updateStatus(Long complaintId, Complaint.Status newStatus);

    // Admin filters by status
    List<ComplaintResponse> getComplaintsByStatus(Complaint.Status status);
}
