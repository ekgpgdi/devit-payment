package com.devit.devitpayment.point.repository;

import com.devit.devitpayment.point.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PointRepository extends JpaRepository<Point, Long> {
    Optional<Point> findByUserUid(UUID userUid);
}
