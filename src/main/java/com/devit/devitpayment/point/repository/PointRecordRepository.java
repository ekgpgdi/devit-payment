package com.devit.devitpayment.point.repository;

import com.devit.devitpayment.point.entity.PointRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface PointRecordRepository extends JpaRepository<PointRecord, Long> {

    Page<PointRecord> findAllByUserUidAndCreatedAtBetween(Pageable pageable, UUID userUid, LocalDateTime from, LocalDateTime to);

    Page<PointRecord> findAllByUserUid(Pageable pageable, UUID userUid);
}
