package com.devit.devitpayment.payment.controller;

import com.devit.devitpayment.common.ResponseDetails;
import com.devit.devitpayment.exception.NoResourceException;
import com.devit.devitpayment.payment.dto.PaymentDto;
import com.devit.devitpayment.payment.entity.PaymentRecord;
import com.devit.devitpayment.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    @PostMapping("/payments")
    public ResponseEntity<?> updatePoint(@RequestBody PaymentDto paymentDto) throws NoResourceException, IOException {
        ResponseDetails responseDetails;
        PaymentRecord result = paymentService.createPayment(paymentDto);
        responseDetails = ResponseDetails.success(result, "/api/payment/payments");
        return new ResponseEntity<>(responseDetails, HttpStatus.valueOf(responseDetails.getHttpStatus()));
    }
}
