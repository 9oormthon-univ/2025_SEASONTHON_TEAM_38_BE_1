package com.goormthon.univ.simhae.domain.fastapi.dto.overall;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Builder
public class DreamAnalyzeRequest {

    @NotNull
    private String content;       // 꿈 원문
    private LocalDate dreamDate;  // 꿈 날짜

}
