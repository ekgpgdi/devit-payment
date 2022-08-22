package com.devit.devitpayment.point.entity;

import com.devit.devitpayment.point.dto.PointDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "point_record")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class) // 생성/수정 시간을 자동으로 반영하도록 설정
@Builder
public class PointRecord {
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

    @CreatedDate // 생성일자임을 나타냅니다.
    private LocalDateTime createdAt;

    @LastModifiedDate // 마지막 수정일자임을 나타냅니다.
    private LocalDateTime modifiedAt;
}
