package com.devit.devitpayment.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@RequiredArgsConstructor
@Schema(description = "결제 dto")
public class PaymentDto {
    @Schema(description = "제공자 정보", example = "\"buyer\" : {\n" +
            "        \"uid\" : \"ea579e47-fcff-40df-8cf7-1bc3136a584d\",\n" +
            "        \"name\" : \"이다혜\"\n" +
            "    }")
    private final Map<String, String> buyer;
    @Schema(description = "충전/사용 포인트양", example = "\"provider\" : {\n" +
            "        \"uid\" : \"ae82ea19-41fb-4aa2-9272-eb49d2d28e8d\",\n" +
            "        \"name\": \"혜다이\"\n" +
            "    }")
    private final Map<String, String> provider;
    @Schema(description = "충전/사용 포인트양", example = "\"board\": {\n" +
            "        \"uid\": \"61653832-6561-3139-2d34-3166622d3461\",\n" +
            "        \"title\": \"결제 시스템 구현을 도와주실 3년차 이상의 백엔드 개발자(Spring)를 구합니다.\"\n" +
            "    }")
    private final Map<String, String> board;
}
