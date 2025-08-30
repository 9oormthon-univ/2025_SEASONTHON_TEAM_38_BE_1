package com.goormthon.univ.simhae.domain.fastapi.dto.overall;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class RephraseResponse {

    private String emoji;    // 꿈을 상징하는 이모지
    private String title;    // 꿈 제목
    private String content;  // 꿈 원문

}
