package com.devit.devitpayment.point.service;

import com.devit.devitpayment.common.ResponseDetails;
import com.devit.devitpayment.point.dto.PointDto;
import com.devit.devitpayment.point.entity.Point;
import com.devit.devitpayment.point.entity.PointRecord;
import com.devit.devitpayment.point.entity.Type;
import com.devit.devitpayment.point.repository.PointRecordRepository;
import com.devit.devitpayment.point.repository.PointRepository;
import com.devit.devitpayment.rabbitMQ.dto.UserDto;
import com.devit.devitpayment.token.TokenParse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PointService {
    private final PointRepository pointRepository;
    private final PointRecordRepository pointRecordRepository;
    private final TokenParse tokenParse;

    /**
     * 유저가 회원가입될 때 포인트 정보를 만들어줌 (point = 0)
     */
    public void initUser(UserDto userDto) {
        log.info("유저 회원가입에 의한 Point 생성");
        Point point = new Point(userDto);
        pointRepository.save(point);
    }

    /**
     * 요청된 포인트 사용을 행할 수 있는 상태인지 확인
     */
    public boolean pointValidation(Long existingPoint, PointDto pointDto) {
        if (existingPoint >= pointDto.getAmount()) {
            log.info("보유 포인트가 요청된 포인트보다 많아 사용이 가능합니다.");
            return true;
        } else {
            log.info("보유 포인트가 적어 요청된 포인트를 사용할 수 없습니다.");
            return false;
        }
    }

    /**
     * 유저 uid 와 매칭되는 point 정보 찾기
     */
    public Object getUserPoint(UUID userUid, String path) {
        log.info("UserUid 와 매칭되는 포인트 조회 [userUid : {}]", userUid);
        Optional<Point> pointOptional = pointRepository.findByUserUid(userUid);
        if (pointOptional.isEmpty()) {
            log.info("유저 uid 와 매칭되는 point 정보를 찾을 수 없습니다. [userUid : {}]", userUid);
            return new ResponseDetails("유저 uid 와 매칭되는 point 정보를 찾을 수 없습니다.", 404, path);
        }
        log.info("유저 uid 와 매칭되는 point 정보를 찾았습니다.");
        return pointOptional.get();
    }

    /**
     * 포인트 변경 -> 사용/충전
     */
    public ResponseDetails updatePoint(HttpServletRequest request, PointDto pointDto) {
        UUID userUid = tokenParse.tokenParse(request);
        log.info("포인트를 변경합니다. [토큰에서 파싱한 userUid : {}]", userUid);
        String path = "/api/payment/points";
        Object data = getUserPoint(userUid, path);
        if (!data.getClass().equals(Point.class)) {
            log.info("유저 uid 와 매칭되는 point 정보를 찾을 수 없으므로 404 에러를 응답합니다. [userUid : {}]", userUid);
            return (ResponseDetails) data;
        }
        Point point = (Point) data;
        Long existingPoint = point.getPoint();
        // 포인트 사용이 가능한 상태인지 확인
        if (Type.of(pointDto.getType()) == Type.DEDUCTION && !pointValidation(existingPoint, pointDto)) {
            log.info("보유 포인트가 부족하여 400 에러를 응답합니다.");
            return new ResponseDetails("보유 포인트가 부족합니다.", 400, path);
        }
        point.update(pointDto);
        Long remainingPoint = point.getPoint();

        PointRecord pointRecord = new PointRecord(userUid, point.getIdx(), pointDto, existingPoint, remainingPoint);
        pointRecordRepository.save(pointRecord);
        log.info("포인트 변동을 기록하였습니다. [pointIdx : {}]", pointRecord.getIdx());
        return new ResponseDetails(pointRecord, 200, path);
    }

    /**
     * 유저의 보유 포인트 조회
     */
    public ResponseDetails showPoint(HttpServletRequest request) {
        UUID userUid = tokenParse.tokenParse(request);
        log.info("유저 포인트를 조회합니다. [토큰에서 파싱한 userUid : {}]", userUid);
        String path = "/api/payment/points";
        Object data = getUserPoint(userUid, path);
        if (!data.getClass().equals(Point.class)) {
            log.info("유저 uid 와 매칭되는 point 정보를 찾을 수 없으므로 404 에러를 응답합니다. [userUid : {}]", userUid);
            return (ResponseDetails) data;
        }
        Map<String, Long> pointData = new HashMap<>();
        pointData.put("point", ((Point) data).getPoint());
        log.info("포인트 조회를 완료하였습니다. [토큰에서 파싱한 userUid : {}, point : {}]", userUid, ((Point) data).getPoint());
        return new ResponseDetails(pointData, 200, path);
    }

    /**
     * 유저 포인트 기록 페이징 조회
     */
    public ResponseDetails showRecordPoint(HttpServletRequest request, Pageable pageable, String fromRegDt, String toRegDt) {
        UUID userUid = tokenParse.tokenParse(request);
        log.info("유저 포인트 기록을 조회합니다. [토큰에서 파싱한 userUid : {}]", userUid);
        Page<PointRecord> pointRecords;
        if (fromRegDt != null) {
            LocalDate fromDate = LocalDate.parse(fromRegDt, DateTimeFormatter.ISO_DATE);
            LocalDateTime fromDateTime = fromDate.atStartOfDay();

            LocalDate toDate = LocalDate.parse(toRegDt, DateTimeFormatter.ISO_DATE);
            LocalDateTime toDateTime = toDate.atTime(LocalTime.MAX);

            log.info("기간으로 검색 [fromDate : {}, toRegDt : {}]", fromRegDt, toRegDt);
            pointRecords = pointRecordRepository.findAllByUserUidAndCreatedAtBetween(pageable, userUid, fromDateTime, toDateTime);
        } else {
            pointRecords = pointRecordRepository.findAllByUserUid(pageable, userUid);
        }
        String path = "/api/payment/points/record";
        return new ResponseDetails(pointRecords, 200, path);
    }
}
