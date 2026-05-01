package com.hotel.springbackend.controller;

import com.hotel.springbackend.request.PaymentOrderRequest;
import com.hotel.springbackend.request.PaymentVerificationRequest;
import com.hotel.springbackend.response.PaymentOrderResponse;
import com.hotel.springbackend.service.IPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import com.hotel.springbackend.model.User;
import com.hotel.springbackend.repository.UserRepository;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final UserRepository userRepository;
    private final IPaymentService paymentService;

    // Step 1: Frontend calls this to get a Razorpay order_id before showing the popup
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody PaymentOrderRequest request) {
        try {
            PaymentOrderResponse response = paymentService.createOrder(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to create payment order: " + e.getMessage());
        }
    }

    // Step 2: Frontend calls this after Razorpay popup succeeds,
    //         sending payment proof + booking details
    @PostMapping("/verify-and-book")
    public ResponseEntity<?> verifyAndBook(@RequestBody PaymentVerificationRequest request, @AuthenticationPrincipal UserDetails currentUser) {
        try {
            User user = userRepository.findByEmail(currentUser.getUsername()).orElseThrow();
            String confirmationCode = paymentService.verifyAndBook(request,
                user.getEmail(),     // ← pass from JWT
                user.getName());
            return ResponseEntity.ok(
                    "Payment verified! Room booked successfully. Confirmation code: " + confirmationCode
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }
}
