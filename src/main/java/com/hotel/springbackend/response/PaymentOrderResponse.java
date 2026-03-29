package com.hotel.springbackend.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentOrderResponse {
    private String razorpayOrderId;
    private double amount;
    private String currency;
    private String keyId;
}