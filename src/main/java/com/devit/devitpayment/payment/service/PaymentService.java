package com.devit.devitpayment.payment.service;

import com.devit.devitpayment.common.ResponseDetails;
import com.devit.devitpayment.exception.ErrorCode;
import com.devit.devitpayment.exception.NoResourceException;
import com.devit.devitpayment.exception.PointValidFailedException;
import com.devit.devitpayment.payment.dto.PaymentDto;
import com.devit.devitpayment.payment.entity.PaymentRecord;
import com.devit.devitpayment.payment.repository.PaymentRepository;
import com.devit.devitpayment.point.dto.PointDto;
import com.devit.devitpayment.point.entity.Point;
import com.devit.devitpayment.point.entity.PointRecord;
import com.devit.devitpayment.point.entity.Type;
import com.devit.devitpayment.point.repository.PointRecordRepository;
import com.devit.devitpayment.point.repository.PointRepository;
import com.devit.devitpayment.token.TokenParse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PointRepository pointRepository;
    private final PointRecordRepository pointRecordRepository;
    private final TokenParse tokenParse;

    /**
     * board uid 를 이용하여 board price 조회 (API 통신)
     */
    public JSONObject getBoardPrice(UUID boardUid, String token) throws IOException {
        log.info("board uid 를 이용한 board price 조회 시작 [boardUid : {}]", boardUid);
        String url = "https://devit-backend.shop/api/boards/" + boardUid;
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .header("Authorization", token)
                .url(url)
                .get()
                .build();
        Response response = client.newCall(request).execute();
        log.info("board uid 를 이용한 board price 조회 완료");

        String responseBody = Objects.requireNonNull(response.body()).string();
        System.out.println(responseBody);
        return new JSONObject(responseBody);
    }

    /**
     * user uid 를 이용하여 Point entity 조회
     */
    public Optional<Point> getUserPoint(UUID userUid) {
        return pointRepository.findByUserUid(userUid);
    }

    /**
     * 포인트 변동 및 포인트 변동 기록
     */
    public PointRecord updatePoint(Point point, Long amount, String type) {
        log.info("Point Entity 의 User uid 조회 [userUid : {}]", point.getUserUid());
        UUID userUid = point.getUserUid();
        PointDto pointDto = new PointDto(type, amount);
        log.info("Point Entity 의 existingPoint 조회");
        Long existingPoint = point.getPoint();
        log.info("Point Entity 의 update 진행");
        point.update(new PointDto(type, amount));
        log.info("Point Entity 의 remainingPoint 조회");
        Long remainingPoint = point.getPoint();
        log.info("PointRecord entity 생성 및 저장");
        PointRecord pointRecord = new PointRecord(userUid, point.getIdx(), pointDto, existingPoint, remainingPoint);
        pointRecordRepository.save(pointRecord);
        return pointRecord;
    }

    /**
     * 결제 진행
     */
    public PaymentRecord createPayment(HttpServletRequest request, PaymentDto paymentDto) throws NoResourceException, IOException, PointValidFailedException {
        String token = request.getHeader("Authorization");
        log.info("header token 파싱 [token : {}]", token);
        // 1. 구매자와 일치하는 Point Entity 확인
        log.info("구매자와 일치하는 Point Entity 확인 [구매자 UserUid : {}]", paymentDto.getBuyer().get("uid"));
        UUID buyerUid = UUID.fromString(paymentDto.getBuyer().get("uid"));
        Point buyerPoint = getUserPoint(buyerUid).orElseThrow(() -> new NoResourceException(ErrorCode.RESOURCE_NOT_FOUND, "NOT_FOUND_BUYER_POINT"));

        // 2. 제공자와 일치하는 Point Entity 확인
        log.info("제공자와 일치하는 Point Entity 확인 [제공자 UserUid : {}]", paymentDto.getProvider().get("uid"));
        UUID providerUid = UUID.fromString(paymentDto.getProvider().get("uid"));
        Point providerPoint = getUserPoint(providerUid).orElseThrow(() -> new NoResourceException(ErrorCode.RESOURCE_NOT_FOUND, "NOT_FOUND_PROVIDER_POINT"));

        // 3. Board 도메인으로 Price 요청
        log.info("Board 도메인으로 Price 요청");
        UUID boardUid = UUID.fromString(paymentDto.getBoard().get("uid"));
        JSONObject boardObject = getBoardPrice(boardUid, token);
        String priceStr = boardObject.getString("price");
        long price = Long.parseLong(priceStr.replace(",", ""));
        log.info("price : {}", price);

        // 4. 구매자의 보유 포인트가 price 만큼 있는지 확인
        if (buyerPoint.getPoint() < price) {
            log.info("구매자의 포인트가 부족하여 결제가 실패되었습니다. [구매자의 남은 포인트 : {}, 결제에 필요한 포인트 : {}]", buyerPoint.getPoint(), price);
            throw new PointValidFailedException(ErrorCode.BAD_REQUEST, "LACK_OF_BUYER_POINTS");
        }

        // 5. 구매자 포인트 감소
        log.info("구매자 포인트 감소");
        PointRecord buyerPointRecord = updatePoint(buyerPoint, price, "DEDUCTION");

        // 6. 제공자 포인트 증가
        log.info("제공자 포인트 증가");
        PointRecord providerPointRecord = updatePoint(providerPoint, price, "CHARGE");

        // 7. 결제 기록 추가
        log.info("결제 기록 추가");
        PaymentRecord paymentRecord = new PaymentRecord(buyerPointRecord, paymentDto.getBuyer().get("name"), providerPointRecord, paymentDto.getProvider().get("name"), boardUid, boardObject);
        paymentRepository.save(paymentRecord);

        return paymentRecord;
    }

    public ResponseDetails showPaymentRecord(HttpServletRequest request, Pageable pageable, String fromRegDt, String toRegDt) {
        UUID userUid = tokenParse.tokenParse(request);
        log.info("결제 기록 조회 [userUid : {}]", userUid);
        Page<PaymentRecord> paymentRecords;
        if (fromRegDt != null) {
            LocalDate fromDate = LocalDate.parse(fromRegDt, DateTimeFormatter.ISO_DATE);
            LocalDateTime fromDateTime = fromDate.atStartOfDay();

            LocalDate toDate = LocalDate.parse(toRegDt, DateTimeFormatter.ISO_DATE);
            LocalDateTime toDateTime = toDate.atTime(LocalTime.MAX);

            log.info("기간으로 검색 [fromDate : {}, toRegDt : {}]", fromRegDt, toRegDt);
            paymentRecords = paymentRepository.findAllByBuyerUidOrProviderUidAndCreatedAtBetween(pageable, userUid, fromDateTime, toDateTime);
        } else {
            paymentRecords = paymentRepository.findAllByBuyerUidOrProviderUid(pageable, userUid);
        }
        String path = "/api/payment/payments";
        return new ResponseDetails(paymentRecords, 200, path);
    }
}
