package com.goormthon.univ.simhae.domain.fastapi.service;

import com.goormthon.univ.simhae.domain.dream.entity.Dream;
import com.goormthon.univ.simhae.domain.dream.repository.DreamRepository;
import com.goormthon.univ.simhae.domain.fastapi.dto.overall.DreamAnalyzeRequest;
import com.goormthon.univ.simhae.domain.fastapi.dto.unconscious.UnconsciousAnalysisResponse;
import com.goormthon.univ.simhae.domain.fastapi.dto.unconscious.UnconsciousAnalyzeRequest;
import com.goormthon.univ.simhae.global.exception.message.ErrorMessage;
import com.goormthon.univ.simhae.global.exception.model.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DreamAnalysisService {

    private final DreamRepository dreamRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${deploy.fastapi_url}")
    private String FAST_API_URL;

    /**
     * 전체 분석 호출
     */
    public Map<String, Object> analyzeOverall(DreamAnalyzeRequest request) {
        String url = "http://" + FAST_API_URL + "/ai/dreams/overall";
        // FastAPI POST 호출
        return restTemplate.postForObject(url, request, Map.class);
    }

    /**
     * 무의식 분석 호출 - 최근 7개 꿈
     */
    public UnconsciousAnalysisResponse analyzeUnconscious(Long userId) {
        List<Dream> recentDreams = dreamRepository.findTop7ByUserIdOrderByCreatedAtDesc(userId);

        // 최소 7개 검사
        if (recentDreams.size() < 7) {
            throw new BadRequestException(ErrorMessage.UNCONSCIOUS_MIN_DREAMS);
        }

        UnconsciousAnalyzeRequest request = new UnconsciousAnalyzeRequest(
                recentDreams.stream()
                        .map(Dream::getInterpretation)
                        .collect(Collectors.toList())
        );

        String url = "http://" + FAST_API_URL + "/ai/dreams/unconscious";
        return restTemplate.postForObject(url, request, UnconsciousAnalysisResponse.class);
    }

}
