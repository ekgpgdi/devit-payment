package com.devit.devitpayment.point.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@Schema(description = "point 충전/사용 DTO")
public class PointDto {
    @Schema(description = "포인트 충전/사용", example = "CHARGE")
    private final String type;
    @Schema(description = "충전/사용 포인트양", example = "10000")
    private final long amount;
}
