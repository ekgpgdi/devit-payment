package com.devit.devitpayment.point.controller;

import com.devit.devitpayment.aop.LoggingClientInfo;
import com.devit.devitpayment.common.ResponseDetails;
import com.devit.devitpayment.point.dto.PointDto;
import com.devit.devitpayment.point.service.PointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequiredArgsConstructor
@LoggingClientInfo
@Slf4j
public class PointController {
    private final PointService pointService;

    @PutMapping("/points")
    public ResponseEntity<?> updatePoint(HttpServletRequest request, @RequestBody PointDto pointDto) {
        log.info("==포인트 충전 요청 시작==");
        ResponseDetails responseDetails = pointService.updatePoint(request, pointDto);
        return new ResponseEntity<>(responseDetails, HttpStatus.valueOf(responseDetails.getHttpStatus()));
    }

    @GetMapping("/points")
    public ResponseEntity<?> showPoint(HttpServletRequest request) {
        log.info("==포인트 조회 요청 시작==");
        ResponseDetails responseDetails = pointService.showPoint(request);
        return new ResponseEntity<>(responseDetails, HttpStatus.valueOf(responseDetails.getHttpStatus()));
    }

    @GetMapping("/points/record")
    public ResponseEntity<?> showRecordPoint(HttpServletRequest request,
                                             @PageableDefault(page = 1, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                                             @RequestParam(name = "fromRegDt", required = false) String fromRegDt,
                                             @RequestParam(name = "toRegDt", required = false) String toRegDt) {
        log.info("==포인트 기록 조회 요청 시작==");
        ResponseDetails responseDetails = pointService.showRecordPoint(request, pageable, fromRegDt, toRegDt);
        return new ResponseEntity<>(responseDetails, HttpStatus.valueOf(responseDetails.getHttpStatus()));
    }
}
