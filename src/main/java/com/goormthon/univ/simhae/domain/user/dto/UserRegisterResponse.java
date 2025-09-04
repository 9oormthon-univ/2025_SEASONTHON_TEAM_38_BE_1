package com.goormthon.univ.simhae.domain.user.dto;

public record UserRegisterResponse(
        Long userId,
        String externalId
) {
}
