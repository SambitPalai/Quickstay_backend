package com.hotel.springbackend.service;

import com.hotel.springbackend.exception.InvalidBookingRequestException;
import com.hotel.springbackend.model.BookedRoom;
import com.hotel.springbackend.request.PaymentOrderRequest;
import com.hotel.springbackend.request.PaymentVerificationRequest;
import com.hotel.springbackend.response.PaymentOrderResponse;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class PaymentService implements IPaymentService {

    private final RazorpayClient razorpayClient;
    private final IBookingService bookingService;

    @Value("${razorpay.key-id}")
    private String keyId;

    @Value("${razorpay.key-secret}")
    private String keySecret;

    @Override
    public PaymentOrderResponse createOrder(PaymentOrderRequest request) throws Exception {
        // Razorpay requires amount in paise (1 rupee = 100 paise)
        int amountInPaise = (int) (request.getAmount() * 100);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "booking_rcpt_" + System.currentTimeMillis());

        Order order = razorpayClient.orders.create(orderRequest);

        return new PaymentOrderResponse(
                order.get("id"),
                request.getAmount(),
                "INR",
                keyId
        );
    }

    @Override
    public String verifyAndBook(PaymentVerificationRequest request, String userEmail, String userName) throws Exception {
        // Step 1: Verify Razorpay signature (HMAC SHA256)
        String payload = request.getRazorpayOrderId() + "|" + request.getRazorpayPaymentId();

        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(
                keySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"
        );
        mac.init(secretKey);
        byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexSignature = new StringBuilder();
        for (byte b : hash) {
            hexSignature.append(String.format("%02x", b));
        }

        if (!hexSignature.toString().equals(request.getRazorpaySignature())) {
            throw new InvalidBookingRequestException("Payment verification failed: invalid signature.");
        }

        // Step 2: Signature is valid — now save the booking
        BookedRoom booking = new BookedRoom();
        booking.setGuestFullName(userName);
        booking.setGuestEmail(userEmail);
        booking.setCheckInDate(request.getCheckInDate());
        booking.setCheckOutDate(request.getCheckOutDate());
        booking.setNumberOfAdults(request.getNumberOfAdults());
        booking.setNumberOfChildren(request.getNumberOfChildren());

        return bookingService.saveBooking(request.getRoomId(), booking);
    }
}
