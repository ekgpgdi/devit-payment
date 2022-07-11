package com.devit.devitpayment.common;

import com.devit.devitpayment.rabbitMQ.dto.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Getter
@RequiredArgsConstructor
public class ResponseDetails {
    private final static int SUCCESS = 200;
    private final static int CREATED = 201;
    private final static int BAD_REQUEST = 400;
    private final static int NOT_FOUND = 404;
    private final static int FAILED = 500;

    private final static String SUCCESS_MESSAGE = "SUCCESS";
    private final static String NOT_FOUND_MESSAGE = "NOT FOUND";
    private final static String FAILED_MESSAGE = "Server error detected.";

    @Schema
    private Date timestamp;
    private Object data;
    @Schema
    private int httpStatus;
    @Schema
    private String path;

    public ResponseDetails(Object data, int httpStatus, String path){
        this.timestamp = new Date();
        this.data = data;
        this.httpStatus = httpStatus;
        this.path = path;
    }

    public static ResponseDetails success(Object data, String path) {
        return new ResponseDetails(data, SUCCESS, path);
    }

    public static ResponseDetails created(Object data, String path) {
        return new ResponseDetails(data, CREATED, path);
    }

    public static ResponseDetails fail(Object data ,String path) {
        return new ResponseDetails(data, FAILED, path);
    }

    public static ResponseDetails badRequest(Object data ,String path) {
        return new ResponseDetails(data, BAD_REQUEST, path);
    }

    public static ResponseDetails notFound(Object data, String path) {
        return new ResponseDetails(data, NOT_FOUND, path);
    }
}
