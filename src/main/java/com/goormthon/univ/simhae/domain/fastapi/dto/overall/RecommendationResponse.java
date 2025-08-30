package com.goormthon.univ.simhae.domain.fastapi.dto.overall;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class RecommendationResponse {

    private String suggestion; // AI 제안

}
