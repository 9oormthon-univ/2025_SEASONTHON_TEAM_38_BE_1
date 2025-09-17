package com.goormthon.univ.simhae.domain.auth.controller;

import com.goormthon.univ.simhae.domain.auth.dto.AppleLoginRequest;
import com.goormthon.univ.simhae.domain.auth.dto.RefreshRequest;
import com.goormthon.univ.simhae.domain.auth.dto.TokenResponse;
import com.goormthon.univ.simhae.domain.auth.service.AuthService;
import com.goormthon.univ.simhae.global.dto.SuccessResponse;
import com.goormthon.univ.simhae.global.exception.message.SuccessMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService auth;

    @PostMapping("/apple")
    public ResponseEntity<?> loginApple(@RequestBody @Valid AppleLoginRequest req) {
        var t = auth.loginWithApple(req.identityToken(), req.nonce());
        return ResponseEntity.ok(
                SuccessResponse.of(SuccessMessage.AUTH_SUCCESS,
                        new TokenResponse(t.accessToken(), t.refreshToken()))
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody @Valid RefreshRequest req) {
        var t = auth.refresh(req.refreshToken());
        return ResponseEntity.ok(
                SuccessResponse.of(SuccessMessage.TOKEN_REFRESH_SUCCESS,
                        new TokenResponse(t.accessToken(), t.refreshToken()))
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody @Valid RefreshRequest req) {
        auth.logout(req.refreshToken());
        return ResponseEntity.ok(SuccessResponse.of(SuccessMessage.LOGOUT_SUCCESS));
    }
}

