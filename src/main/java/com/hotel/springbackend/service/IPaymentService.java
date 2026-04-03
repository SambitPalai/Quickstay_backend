package com.hotel.springbackend.service;

import com.hotel.springbackend.request.PaymentOrderRequest;
import com.hotel.springbackend.request.PaymentVerificationRequest;
import com.hotel.springbackend.response.PaymentOrderResponse;

public interface IPaymentService {
    PaymentOrderResponse createOrder(PaymentOrderRequest request) throws Exception;
    String verifyAndBook(PaymentVerificationRequest request, String userEmail, String userName) throws Exception;
}
