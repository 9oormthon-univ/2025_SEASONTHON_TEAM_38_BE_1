package com.goormthon.univ.simhae.domain.fastapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.goormthon.univ.simhae.domain.dream.entity.Dream;
import com.goormthon.univ.simhae.domain.dream.entity.value.Category;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    public Map<String, Object> analyzeOverall(DreamAnalyzeRequest request, Long userId) {
        // User 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        // FastAPI 호출
        String url = "http://" + FAST_API_URL + "/ai/dreams/overall";
        Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);

        Map<String, Object> restate = (Map<String, Object>) response.get("restate");
        Map<String, Object> unconscious = (Map<String, Object>) response.get("unconscious");
        Map<String, Object> suggestionMap = (Map<String, Object>) response.get("suggestion");

        // Category 문자열을 ENUM으로 변환
        String categoryStr = (String) restate.get("category");
        Category categoryEnum = Category.valueOf(categoryStr);

        // Dream 엔티티 생성 및 저장
        Dream dream = Dream.builder()
                .user(user)
                .title((String) restate.get("title"))
                .emoji((String) restate.get("emoji"))
                .content((String) restate.get("content"))
                .category(categoryEnum)
                .interpretation((String) unconscious.get("analysis"))
                .suggestion((String) suggestionMap.get("suggestion"))
                .dreamDate(request.getDreamDate())
                .build();

        dreamRepository.save(dream);

        Map<String, Object> clientResponse = new HashMap<>();

        Map<String, Object> restateMap = new HashMap<>();
        restateMap.put("emoji", dream.getEmoji());
        restateMap.put("title", dream.getTitle());
        restateMap.put("content", dream.getContent());
        restateMap.put("categoryName", categoryEnum.getName());
        restateMap.put("categoryDescription", categoryEnum.getDescription());

        String interpretation = dream.getInterpretation();
        List<Map<String, String>> analysisList = new ArrayList<>();

        Pattern pattern = Pattern.compile("\\[(.+?)\\]\\s*(.*?)(?=(\\[.+?\\]|$))", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(interpretation);

        while (matcher.find()) {
            Map<String, String> item = new HashMap<>();
            item.put("title", matcher.group(1).trim());
            item.put("content", matcher.group(2).trim());
            analysisList.add(item);
        }

        Map<String, Object> unconsciousMap = new HashMap<>();
        unconsciousMap.put("analysis", analysisList);

        Map<String, Object> suggestionMapOut = new HashMap<>();
        suggestionMapOut.put("suggestion", dream.getSuggestion());

        clientResponse.put("restate", restateMap);
        clientResponse.put("unconscious", unconsciousMap);
        clientResponse.put("suggestion", suggestionMapOut);

        return clientResponse;
    }

    /**
     * 무의식 분석 호출 - 최근 7개 꿈
     */
    public Map<String, Object> analyzeUnconscious(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND));

        List<Dream> recentDreams = dreamRepository.findTop7ByUserIdOrderByCreatedDateDesc(user.getId());
        if (recentDreams.size() < 7) {
            throw new BadRequestException(ErrorMessage.UNCONSCIOUS_MIN_DREAMS);
        }

        UnconsciousAnalyzeRequest request = new UnconsciousAnalyzeRequest(
                recentDreams.stream()
                        .map(Dream::getInterpretation)
                        .collect(Collectors.toList())
        );

        UnconsciousAnalysisResponse apiResponse =
                restTemplate.postForObject("http://" + FAST_API_URL + "/ai/dreams/unconscious",
                        request, UnconsciousAnalysisResponse.class);

        List<String> recentDreamsFormatted = recentDreams.stream()
                .map(dream -> dream.getEmoji() + " " + dream.getTitle())
                .collect(Collectors.toList());

        String analysisRaw = apiResponse.getAnalysis();
        List<Map<String, String>> analysisList = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\[(.+?)\\]\\s*(.*?)(?=(\\[.+?\\]|$))", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(analysisRaw);
        while (matcher.find()) {
            Map<String, String> item = new HashMap<>();
            item.put("title", matcher.group(1).trim());
            item.put("content", matcher.group(2).trim());
            analysisList.add(item);
        }

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("title", apiResponse.getTitle());
        dataMap.put("analysis", analysisList);
        dataMap.put("suggestion", apiResponse.getSuggestion());
        dataMap.put("recentDreams", recentDreamsFormatted);

        return dataMap;
    }

}
