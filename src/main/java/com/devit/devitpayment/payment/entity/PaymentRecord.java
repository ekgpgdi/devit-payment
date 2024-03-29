package com.devit.devitpayment.payment.entity;

import com.devit.devitpayment.point.entity.PointRecord;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.json.JSONObject;
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
@Builder(builderMethodName = "paymentRecordBuilder") // 빌더 패턴 적용
@ToString
public class PaymentRecord {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(example = "자동 증가되는 db 내 id")
    private long idx;

    @Column(nullable = false, unique = true, columnDefinition = "BINARY(16)")
    @Schema(example = "결제 식별 uid")
    private UUID paymentUid;

    @Schema(example = "구매자의 point_record 테이블과 매핑되는 idx")
    @OneToOne(fetch = FetchType.LAZY) // 지연 로딩으로
    private PointRecord buyerPointRecord;

    @Column(nullable = false, columnDefinition = "BINARY(16)")
    @Schema(example = "구매자 회원 uid")
    private UUID buyerUid;

    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    @Schema(example = "구매자 회원 이름")
    private String buyerName;

    @Schema(example = "제공자의 point_record 테이블과 매핑되는 idx")
    @OneToOne(fetch = FetchType.LAZY) // 지연 로딩으로
    private PointRecord providerPointRecord;

    @Column(nullable = false, columnDefinition = "BINARY(16)")
    @Schema(example = "제공자 회원 uid")
    private UUID providerUid;

    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    @Schema(example = "제공자 회원 이름")
    private String providerName;

    @Column(nullable = false, columnDefinition = "BINARY(16)")
    @Schema(example = "공고 게시글 uid")
    private UUID boardUid;

    @Column(nullable = false, columnDefinition = "VARCHAR(100)")
    @Schema(example = "공고 게시글 제목")
    private String boardTitle;

    @Column(nullable = false)
    @Schema(example = "결제/적립 금액")
    private long amount;

    @CreatedDate // 생성일자임을 나타냅니다.
    private LocalDateTime createdAt;

    @LastModifiedDate // 마지막 수정일자임을 나타냅니다.
    private LocalDateTime modifiedAt;

    public static PaymentRecordBuilder builder(UUID paymentUid) {
        if(paymentUid == null) {
            throw new IllegalArgumentException("필수 파라미터 누락");
        }
        return paymentRecordBuilder().paymentUid(paymentUid);
    }
}
