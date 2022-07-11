package com.devit.devitpayment.point.repository;

import com.devit.devitpayment.point.entity.PointRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointRecordRepository extends JpaRepository<PointRecord, Long>  {
}
