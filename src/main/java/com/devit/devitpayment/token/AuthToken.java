package com.devit.devitpayment.token;

import com.devit.devitpayment.exception.ErrorCode;
import com.devit.devitpayment.exception.TokenValidFailedException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class AuthToken {
    public static final String UID = "uuid";
    private final Key key;

    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";

    public AuthToken(String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public UUID getUserUid(HttpServletRequest request) {
        String headerValue = request.getHeader(HEADER_AUTHORIZATION);
        String token = null;

        if (headerValue == null) {
            return null;
        }

        if (headerValue.startsWith(TOKEN_PREFIX)) {
            token = headerValue.substring(TOKEN_PREFIX.length());
        }
        return getTokenUserUid(token);
    }

    // 토큰 검증
    public boolean validate(String token) {
        return this.getTokenClaims(token) != null;
    }

    // 토큰 파싱 시 생길 수 있는 에러를 미리 정의
    public Claims getTokenClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SecurityException e) {
            log.info("Invalid JWT signature.");
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
        }
        return null;
    }

    public UUID getTokenUserUid(String token) throws TokenValidFailedException {

        if (validate(token)) {

            Claims claims = getTokenClaims(token);
            log.debug("claims subject := [{}]", claims.getSubject());
            String uid = (String) claims.get(AuthToken.UID);

            return UUID.fromString(uid);
        } else {
            throw new TokenValidFailedException(ErrorCode.UNAUTHORIZED, "authToken not validate");
        }
    }
}
