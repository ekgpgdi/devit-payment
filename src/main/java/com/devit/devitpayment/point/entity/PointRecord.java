package com.devit.devitpayment.point.entity;

import com.devit.devitpayment.point.dto.PointDto;
import com.devit.devitpayment.util.Timestamped;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointRecord extends Timestamped {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(example = "자동 증가되는 db 내 id")
    private long idx;

    @Column(nullable = false, columnDefinition = "BINARY(16)")
    @Schema(example = "유저 식별 uid")
    private UUID userUid;

    @JsonIgnore
    @Column(nullable = false)
    @Schema(example = "point 테이블과 연결되는 fk")
    private long pointIdx;

    @Column(nullable = false)
    @Schema(example = "기존 보유 포인트")
    private long existingPoint;

    @Column(nullable = false)
    @Schema(example = "충전/감소량")
    private long amount;

    @Column(nullable = false)
    @Schema(example = "충전/감소")
    private Type type;

    @Column(nullable = false)
    @Schema(example = "남은 포인트")
    private long remainingPoint;


    public PointRecord(UUID userUid, long idx, PointDto pointDto, Long existingPoint, Long remainingPoint) {
        this.userUid = userUid;
        this.pointIdx = idx;
        this.existingPoint = existingPoint;
        this.amount = pointDto.getAmount();
        this.type = Type.of(pointDto.getType());
        this.remainingPoint = remainingPoint;
    }
}
