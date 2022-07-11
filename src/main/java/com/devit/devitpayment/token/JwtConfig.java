package com.devit.devitpayment.token;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    @Value("${jwt.secret}")
    private String secret;

    @Bean
    public AuthToken jwtProvider() {
        return new AuthToken(secret);
    }
}
