package com.devit.devitpayment.point.controller;

import com.devit.devitpayment.common.ResponseDetails;
import com.devit.devitpayment.point.dto.PointDto;
import com.devit.devitpayment.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class PointController {
    private final PointService pointService;

    @PutMapping("/points")
    public ResponseEntity<?> updatePoint(HttpServletRequest request, @RequestBody PointDto pointDto) {
        ResponseDetails responseDetails = pointService.updatePoint(request, pointDto);
        return new ResponseEntity<>(responseDetails, HttpStatus.valueOf(responseDetails.getHttpStatus()));
    }
}