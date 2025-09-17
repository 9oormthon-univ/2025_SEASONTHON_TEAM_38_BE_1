package com.goormthon.univ.simhae.domain.auth.dto;

public record AppleLoginRequest(
        String identityToken,
        String nonce
) {
}
