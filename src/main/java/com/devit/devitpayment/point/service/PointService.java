package com.devit.devitpayment.point.service;

import com.devit.devitpayment.common.ResponseDetails;
import com.devit.devitpayment.point.dto.PointDto;
import com.devit.devitpayment.point.entity.Point;
import com.devit.devitpayment.point.entity.PointRecord;
import com.devit.devitpayment.point.entity.Type;
import com.devit.devitpayment.point.repository.PointRecordRepository;
import com.devit.devitpayment.point.repository.PointRepository;
import com.devit.devitpayment.rabbitMQ.dto.UserDto;
import com.devit.devitpayment.token.AuthToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PointService {
    private final PointRepository pointRepository;
    private final PointRecordRepository pointRecordRepository;
    private final AuthToken authToken;

    /**
     * 요청 헤더 내 토큰 파싱하여 유저 uid 가져옴
     */
    public UUID tokenParse(HttpServletRequest request) {
        return authToken.getUserUid(request);
    }

    /**
     * 유저가 회원가입될 때 포인트 정보를 만들어줌 (point = 0)
     */
    public void initUser(UserDto userDto) {
        Point point = new Point(userDto);
        pointRepository.save(point);
    }

    /**
     * 요청된 포인트 사용을 행할 수 있는 상태인지 확인
     */
    public boolean pointValidation(Long existingPoint, PointDto pointDto) {
        if (existingPoint >= pointDto.getAmount()) {
            return true;
        } else {
            return false;
        }
    }

    public ResponseDetails updatePoint(HttpServletRequest request, PointDto pointDto) {
        UUID userUid = tokenParse(request);
        Optional<Point> pointOptional = pointRepository.findByUserUid(userUid);
        String path = "/api/payment/points";
        if (pointOptional.isEmpty()) {
            return new ResponseDetails("유저 uid 와 매칭되는 point 정보를 찾을 수 없습니다.", 404, path);
        }
        Point point = pointOptional.get();
        Long existingPoint = point.getPoint();
        // 포인트 사용이 가능한 상태인지 확인
        if (Type.of(pointDto.getType()) == Type.DEDUCTION && !pointValidation(existingPoint, pointDto)) {
            return new ResponseDetails("보유 포인트가 부족합니다.", 400, path);
        }
        point.update(pointDto);
        Long remainingPoint = point.getPoint();

        PointRecord pointRecord = new PointRecord(userUid, point.getIdx(), pointDto, existingPoint, remainingPoint);
        pointRecordRepository.save(pointRecord);
        return new ResponseDetails(pointRecord, 200, path);
    }
}
