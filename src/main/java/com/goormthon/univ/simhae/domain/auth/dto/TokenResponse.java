package com.goormthon.univ.simhae.domain.auth.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {

}
