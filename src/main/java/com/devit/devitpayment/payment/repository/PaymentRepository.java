package com.devit.devitpayment.payment.repository;

import com.devit.devitpayment.payment.entity.PaymentRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<PaymentRecord, Long> {
    @Query(value = "SELECT * FROM payment_record p WHERE (p.buyer_uid = :userUid OR p.provider_uid =:userUid) AND p.created_at BETWEEN :fromDateTime AND :toDateTime",
            countQuery = "SELECT count(*) FROM payment_record",
            nativeQuery = true)
    Page<PaymentRecord> findAllByBuyerUidOrProviderUidAndCreatedAtBetween(Pageable pageable, @Param("userUid") UUID userUid, @Param("fromDateTime") LocalDateTime fromDateTime, @Param("toDateTime") LocalDateTime toDateTime);

    @Query(value = "SELECT * FROM payment_record p WHERE p.buyer_uid = :userUid OR p.provider_uid = :userUid",
            countQuery = "SELECT count(*) FROM payment_record",
            nativeQuery = true)
    Page<PaymentRecord> findAllByBuyerUidOrProviderUid(Pageable pageable, @Param("userUid") UUID userUid);
}
