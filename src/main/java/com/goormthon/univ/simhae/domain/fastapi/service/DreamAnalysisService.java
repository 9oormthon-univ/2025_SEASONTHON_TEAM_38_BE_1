package com.goormthon.univ.simhae.domain.fastapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.goormthon.univ.simhae.domain.dream.entity.Dream;
import com.goormthon.univ.simhae.domain.dream.repository.DreamRepository;
import com.goormthon.univ.simhae.domain.fastapi.dto.overall.DreamAnalyzeRequest;
import com.goormthon.univ.simhae.domain.fastapi.dto.unconscious.UnconsciousAnalysisResponse;
import com.goormthon.univ.simhae.domain.fastapi.dto.unconscious.UnconsciousAnalyzeRequest;
import com.goormthon.univ.simhae.domain.user.entity.User;
import com.goormthon.univ.simhae.domain.user.repository.UserRepository;
import com.goormthon.univ.simhae.global.exception.message.ErrorMessage;
import com.goormthon.univ.simhae.global.exception.model.BadRequestException;
import com.goormthon.univ.simhae.global.exception.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DreamAnalysisService {

    private final DreamRepository dreamRepository;
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;

    @Value("${deploy.fastapi.url}")
    private String FAST_API_URL;

    public DreamAnalysisService(DreamRepository dreamRepository, UserRepository userRepository) {
        this.dreamRepository = dreamRepository;
        this.userRepository = userRepository;

        // ObjectMapper 커스터마이징
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // LocalDate 지원
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(mapper);

        this.restTemplate = new RestTemplate();
        // 기존 converter 제거 후 새 converter 추가
        this.restTemplate.getMessageConverters().removeIf(c -> c instanceof MappingJackson2HttpMessageConverter);
        this.restTemplate.getMessageConverters().add(converter);
    }

    /**
     * 전체 분석 호출 및 DB 저장
     */
    @Transactional
    public Map<String, Object> analyzeOverall(DreamAnalyzeRequest request, String externalId) {
        // externalId로 User 조회
        User user = userRepository.findByExternalId(externalId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        // FastAPI 호출
        String url = "http://" + FAST_API_URL + "/ai/dreams/overall";
        Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);

        // FastAPI 응답에서 바로 Map 가져오기
        Map<String, Object> restate = (Map<String, Object>) response.get("restate");
        Map<String, Object> unconscious = (Map<String, Object>) response.get("unconscious");
        Map<String, Object> suggestionMap = (Map<String, Object>) response.get("suggestion");

        // Dream 엔티티 생성 및 저장
        Dream dream = Dream.builder()
                .user(user)
                .title((String) restate.get("title"))
                .emoji((String) restate.get("emoji"))
                .content((String) restate.get("content"))
                .summary((String) restate.getOrDefault("summary", restate.get("content")))
                .interpretation((String) unconscious.get("analysis"))
                .suggestion((String) suggestionMap.get("suggestion"))
                .dreamDate(LocalDate.now())
                .category(null)
                .build();

        dreamRepository.save(dream);
        return response;
    }

    /**
     * 무의식 분석 호출 - 최근 7개 꿈
     */
    public UnconsciousAnalysisResponse analyzeUnconscious(Long userId) {
        List<Dream> recentDreams = dreamRepository.findTop7ByUserIdOrderByCreatedDateDesc(userId);

        // 최소 7개 검사
        if (recentDreams.size() < 7) {
            throw new BadRequestException(ErrorMessage.UNCONSCIOUS_MIN_DREAMS);
        }
        //
        UnconsciousAnalyzeRequest request = new UnconsciousAnalyzeRequest(
                recentDreams.stream()
                        .map(Dream::getInterpretation)
                        .collect(Collectors.toList())
        );

        String url = "http://" + FAST_API_URL + "/ai/dreams/unconscious";
        return restTemplate.postForObject(url, request, UnconsciousAnalysisResponse.class);
    }

}
