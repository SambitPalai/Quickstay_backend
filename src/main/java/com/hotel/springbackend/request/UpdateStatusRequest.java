package com.hotel.springbackend.request;

import com.hotel.springbackend.model.Complaint;
import lombok.Data;

@Data
public class UpdateStatusRequest {
    // OPEN | IN_PROGRESS | RESOLVED
    private Complaint.Status status;
}
