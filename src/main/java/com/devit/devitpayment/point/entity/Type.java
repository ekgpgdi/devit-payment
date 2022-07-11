package com.devit.devitpayment.point.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum Type {
    CHARGE("CHARGE", "포인트 충전"),
    DEDUCTION("DEDUCTION", "포인트 사용"),
    ERROR("ERROR", "포인트 충전/사용 에러 발생");

    @Schema(example = "포인트 변동 -> 충전/감소")
    private final String code;
    @Schema(example = "포인트 변동 설명")
    private final String displayName;

    public static Type of(String code) {
        return Arrays.stream(Type.values())
                .filter(r -> r.getCode().equals(code))
                .findAny()
                .orElse(ERROR);
    }
}
