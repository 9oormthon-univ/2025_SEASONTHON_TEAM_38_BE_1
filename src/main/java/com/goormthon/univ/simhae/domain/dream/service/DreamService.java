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
                        d.getContent(), // content 추가
                        d.getCategory().getName(),          // categoryName
                        d.getCategory().getDescription(),   // categoryDescription
                        d.getInterpretation(),
                        d.getSuggestion()
                ));
    }

    @Transactional
    public boolean deleteDream(Long userId, Long dreamId) {
        return dreamRepository.deleteByIdAndUser_Id(dreamId, userId) > 0;
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
            case DAILY_REFLECTION -> "일상 반영 꿈";
            case WISH_FULFILLMENT -> "소망 충족 꿈";
            case NIGHTMARE        -> "악몽 / 불안 꿈";
            case RECURRING       -> "반복 꿈";
            case SYMBOLIC         -> "상징적 꿈";
            case PRECOGNITIVE     -> "예지 / 직관 꿈";
            case CREATIVE         -> "창의적 / 문제 해결 꿈";
            case SOMATIC         -> "신체 반영 꿈";
            case LUCID            -> "자각몽";
            case SURREAL          -> "초현실 / 비논리적 꿈";
        };
    }



}