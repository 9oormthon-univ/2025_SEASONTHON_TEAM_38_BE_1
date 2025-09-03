package com.goormthon.univ.simhae.domain.fastapi.dto.unconscious;

import java.util.List;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 무의식 분석 요청 DTO
 * - 최근 7개의 꿈 해석 결과를 요청 본문으로 전달
 */
@Getter
@AllArgsConstructor
@Builder
public class UnconsciousAnalyzeRequest {

    @Size(min = 7)
    private List<String> recentDreamAnalyses; // 항상 7개의 꿈 해석 결과

}
