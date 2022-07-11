package com.devit.devitpayment.point.entity;

import com.devit.devitpayment.point.dto.PointDto;
import com.devit.devitpayment.rabbitMQ.dto.UserDto;
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
public class Point extends Timestamped {
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

    public Point(UserDto userDto) {
        this.userUid = userDto.getUuid();
        this.point = 0;
    }

    public void update(PointDto pointDto) {
        if (Type.CHARGE == Type.of(pointDto.getType())) {
            point += pointDto.getAmount();
        } else if (Type.DEDUCTION == Type.of(pointDto.getType())) {
            point -= pointDto.getAmount();
        }
    }
}
