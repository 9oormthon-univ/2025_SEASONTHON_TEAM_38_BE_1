package com.goormthon.univ.simhae.domain.fastapi.dto.overall;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UnconsciousResponse {

    private String categoryName;      // 무의식 카테고리명
    private String interpretation;    // 꿈 해석 내용

}
