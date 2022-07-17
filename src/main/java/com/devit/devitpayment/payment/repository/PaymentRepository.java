package com.devit.devitpayment.payment.repository;

import com.devit.devitpayment.payment.entity.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentRecord, Long> {
}
