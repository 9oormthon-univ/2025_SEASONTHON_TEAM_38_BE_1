package com.goormthon.univ.simhae.domain.dream.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DreamDetailResponse(
        Long dreamId,
        LocalDate dreamDate,
        LocalDateTime createdAt,
        String title,
        String emoji,
        String category,
        String summary,
        String interpretation,
        String suggestion
) {
}
