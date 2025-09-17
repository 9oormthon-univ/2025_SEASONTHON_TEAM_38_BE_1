package com.goormthon.univ.simhae.domain.dream.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record DreamDetailResponse(
        Long dreamId,
        LocalDate dreamDate,
        LocalDateTime createdAt,
        String title,
        String emoji,
        String content,
        String categoryName,
        String categoryDescription,
        List<Map<String, String>> interpretation,
        String suggestion
) {
}
