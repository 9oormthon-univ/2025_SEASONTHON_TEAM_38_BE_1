package com.goormthon.univ.simhae.domain.dream.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DreamResponse(
        Long dreamId,
        LocalDate dreamDate,
        String title,
        String emoji,
        String summary,
        String content,
        String category,
        LocalDateTime createdAt
) {
}
