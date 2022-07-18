package com.devit.devitpayment.token;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenParse {
    private final AuthToken authToken;
    /**
     * 요청 헤더 내 토큰 파싱하여 유저 uid 가져옴
     */
    public UUID tokenParse(HttpServletRequest request) {
        return authToken.getUserUid(request);
    }


}
