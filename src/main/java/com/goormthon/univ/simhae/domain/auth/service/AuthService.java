package com.goormthon.univ.simhae.domain.auth.service;


import com.goormthon.univ.simhae.domain.auth.entity.RefreshToken;
import com.goormthon.univ.simhae.domain.auth.repository.RefreshTokenRepository;
import com.goormthon.univ.simhae.domain.user.entity.User;
import com.goormthon.univ.simhae.domain.user.repository.UserRepository;
import com.goormthon.univ.simhae.global.config.AppleProps;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final AppleTokenVerifier apple;
    private final UserRepository userRepo;
    private final RefreshTokenRepository rtRepo;
    private final JwtTokenService jwt;

    /** 애플 로그인: id_token(+nonce) 검증 → 유저 upsert → access/refresh 발급 → refresh 해시 저장 */
    @Transactional
    public Tokens loginWithApple(String idToken, String noncePlainOrSha256) {
        var c = apple.verify(idToken, noncePlainOrSha256); // 서명/iss/aud/exp + nonce 검증

        // upsert user
        User user = userRepo.findByAppleSub(c.sub())
                .orElseGet(() -> userRepo.save(User.builder()
                        .appleSub(c.sub())
                        .email(c.email()) // null 가능
                        .build()));

        // issue tokens
        String access  = jwt.issueAccessToken(user.getId());
        String refresh = jwt.issueRefreshToken(user.getId());

        // refresh 토큰 파싱해서 jti/exp 추출
        var parsed = jwt.parse(refresh).getPayload();
        String jti = parsed.getId();
        var exp = parsed.getExpiration().toInstant();

        // store refresh (hash + 만료 + not revoked)
        String hash = sha256Hex(refresh);

        rtRepo.save(RefreshToken.builder()
                .user(user)
                .tokenHash(hash)
                .jti(jti)
                .expiresAt(exp)
                .revoked(false)
                .build());

        return new Tokens(access, refresh);
    }

    /** 토큰 재발급: 재사용 감지 + 회전(rotate) + 기존 revoke */
    @Transactional
    public Tokens refresh(String refreshToken) {
        var claims = jwt.parse(refreshToken).getPayload();
        if (!"refresh".equals(claims.get("typ"))) {
            throw new IllegalArgumentException("not a refresh token");
        }
        Long userId = Long.valueOf(claims.getSubject());
        String hash = sha256Hex(refreshToken);

        // 1) DB에 존재하지 않으면 재사용 공격 의심 → 해당 유저의 모든 활성 RT revoke
        var existingOpt = rtRepo.findByTokenHash(hash);
        if (existingOpt.isEmpty()) {
            rtRepo.revokeAllActiveByUser(userId);
            throw new IllegalArgumentException("refresh reuse suspected (not found)");
        }
        var existing = existingOpt.get();

        // 2) 이미 revoke된 토큰이면 재사용 공격 → 전체 revoke
        if (existing.isRevoked()) {
            rtRepo.revokeAllActiveByUser(userId);
            throw new IllegalArgumentException("refresh reused (revoked)");
        }

        // 3) 만료 처리
        if (existing.getExpiresAt().isBefore(Instant.now())) {
            existing.setRevoked(true);
            rtRepo.save(existing);
            throw new IllegalArgumentException("refresh expired");
        }

        // 4) 정상 회전: 기존 revoke → 새 access/refresh 발급 → 새 refresh 저장
        existing.setRevoked(true);
        rtRepo.save(existing);

        String newAccess  = jwt.issueAccessToken(userId);
        String newRefresh = jwt.issueRefreshToken(userId);

        // 새 refresh의 jti/exp 저장
        var newClaims = jwt.parse(newRefresh).getPayload();
        String newJti = newClaims.getId();
        var newExp = newClaims.getExpiration().toInstant();

        String newHash = sha256Hex(newRefresh);

        // 회전 링크(선택)
        existing.setReplacedBy(newHash);
        rtRepo.save(existing);

        rtRepo.save(RefreshToken.builder()
                .user(existing.getUser())
                .tokenHash(newHash)
                .jti(newJti)
                .expiresAt(newExp)
                .revoked(false)
                .build());

        return new Tokens(newAccess, newRefresh);
    }

    /** 로그아웃: 전달된 refresh 하나만 revoke (서버 보관 중인 것만) */
    @Transactional
    public void logout(String refreshToken) {
        rtRepo.findByTokenHashAndRevokedFalse(sha256Hex(refreshToken))
                .ifPresent(rt -> {
                    rt.setRevoked(true);
                    rtRepo.save(rt);
                });
    }

    // --- util ---
    private static String sha256Hex(String s) {
        try {
            var md = MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : d) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 응답 DTO
    public record Tokens(String accessToken, String refreshToken) {}
}