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
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class) // 생성/수정 시간을 자동으로 반영하도록 설정
@Builder
public class Point {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(example = "자동 증가되는 db 내 id")
    private long idx;

    @Column(nullable = false, unique = true, columnDefinition = "BINARY(16)")
    @Schema(example = "유저 식별 uid")
    private UUID userUid;

    @Column(nullable = false)
    @Schema(example = "유저 보유 포인트")
    private long point;

    @CreatedDate // 생성일자임을 나타냅니다.
    private LocalDateTime createdAt;

    @LastModifiedDate // 마지막 수정일자임을 나타냅니다.
    private LocalDateTime modifiedAt;

    public void update(PointDto pointDto) {
        if (Type.CHARGE == Type.of(pointDto.getType())) {
            point += pointDto.getAmount();
        } else if (Type.DEDUCTION == Type.of(pointDto.getType())) {
            point -= pointDto.getAmount();
        }
    }
}
