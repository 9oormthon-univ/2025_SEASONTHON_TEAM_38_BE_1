package com.goormthon.univ.simhae.domain.fastapi.dto.unconscious;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnconsciousAnalysisResponse {

    private String title;                   // 분석 결과 제목/심리 상태
    private String analysis;                // 무의식 분석 내용
    private String suggestion;              // 제안 방법
    private List<String> recentDreams;      // 최근 7개의 꿈 (이모지+제목 형태로)

}