package com.goormthon.univ.simhae.domain.dream.service;


import com.goormthon.univ.simhae.domain.dream.dto.DreamDetailResponse;
import com.goormthon.univ.simhae.domain.dream.dto.DreamResponse;
import com.goormthon.univ.simhae.domain.dream.entity.Dream;
import com.goormthon.univ.simhae.domain.dream.entity.value.Category;
import com.goormthon.univ.simhae.domain.dream.repository.DreamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DreamService {

    private final DreamRepository dreamRepository;

    public List<DreamResponse> getDreamsByMonth(Long userId, YearMonth ym, String keyword) {
        LocalDate start = ym.atDay(1);
        LocalDate end   = ym.plusMonths(1).atDay(1);

        return dreamRepository.findByUserAndMonthAndKeyword(userId, start, end, keyword)
                .stream()
                .map(this::toListDto)
                .toList();
    }

    public List<DreamResponse> getDreamsByDay(Long userId, LocalDate date) {
        return dreamRepository.findByUser_IdAndDreamDate(userId, date)
                .stream()
                .map(this::toListDto)
                .toList();
    }

    public Optional<DreamDetailResponse> getDreamDetail(Long userId, Long dreamId) {
        return dreamRepository.findByIdAndUser_Id(dreamId, userId)
                .map(d -> new DreamDetailResponse(
                        d.getId(),
                        d.getDreamDate(),
                        d.getCreatedDate(),
                        d.getTitle(),
                        d.getEmoji(),
                        toKorean(d.getCategory()),
                        d.getInterpretation(),
                        d.getSuggestion()
                ));
    }

    private DreamResponse toListDto(Dream d) {
        return new DreamResponse(
                d.getId(),
                d.getDreamDate(),
                d.getTitle(),
                d.getEmoji(),
                d.getContent(),
                toKorean(d.getCategory()),
                d.getCreatedDate()
        );
    }

    private String toKorean(Category c) {
        if (c == null) return null;
        return switch (c) {
            case ANXIETY -> "불안";
            case JOY     -> "기쁨";
            case SADNESS -> "슬픔";
            case ANGER   -> "분노";
            case OTHER   -> "기타";
        };
    }
}