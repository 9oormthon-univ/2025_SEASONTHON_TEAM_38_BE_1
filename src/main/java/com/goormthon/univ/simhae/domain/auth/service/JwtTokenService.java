package com.goormthon.univ.simhae.domain.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;          // ✅ 중요
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtTokenService {

    private final SecretKey key;        // SecretKey로 선언
    private final long accessTtlSeconds;
    private final long refreshTtlSeconds;

    public JwtTokenService(
            @Value("${auth.jwt.secret}") String secret,
            @Value("${auth.jwt.access-ttl-seconds:900}") long accessTtlSeconds,
            @Value("${auth.jwt.refresh-ttl-seconds:2592000}") long refreshTtlSeconds
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); //   SecretKey 반환
        this.accessTtlSeconds = accessTtlSeconds;
        this.refreshTtlSeconds = refreshTtlSeconds;
    }

    public String issueAccessToken(Long userId) {
        var now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTtlSeconds)))
                .claim("typ", "access")
                .id(UUID.randomUUID().toString())
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public String issueRefreshToken(Long userId) {
        var now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(refreshTtlSeconds)))
                .claim("typ", "refresh")
                .id(UUID.randomUUID().toString())
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parser()
                .verifyWith(key)                   // ✅ 0.12.x 문법
                .build()
                .parseSignedClaims(token);
    }

    public Long getUserId(String accessToken) {
        return Long.valueOf(parse(accessToken).getPayload().getSubject());
    }

    // 추가 메서드
    public String getJti(String jwt) {
        return parse(jwt).getPayload().getId();
    }

}