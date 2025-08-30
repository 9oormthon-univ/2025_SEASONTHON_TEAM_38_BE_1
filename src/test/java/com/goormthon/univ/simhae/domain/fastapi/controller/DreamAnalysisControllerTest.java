package com.goormthon.univ.simhae.domain.fastapi.controller;

import com.goormthon.univ.simhae.domain.dream.entity.Dream;
import com.goormthon.univ.simhae.domain.fastapi.dto.overall.RephraseResponse;
import com.goormthon.univ.simhae.domain.fastapi.dto.overall.UnconsciousResponse;
import com.goormthon.univ.simhae.domain.fastapi.dto.overall.RecommendationResponse;
import com.goormthon.univ.simhae.domain.fastapi.dto.unconscious.UnconsciousAnalysisResponse;
import com.goormthon.univ.simhae.domain.fastapi.service.DreamAnalysisService;
import com.goormthon.univ.simhae.global.exception.GlobalExceptionHandler;
import com.goormthon.univ.simhae.global.exception.message.ErrorMessage;
import com.goormthon.univ.simhae.global.exception.model.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DreamAnalysisControllerTest {

    @Mock
    private DreamAnalysisService dreamAnalysisService;

    @InjectMocks
    private DreamAnalysisController dreamAnalysisController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // GlobalExceptionHandler 등록
        mockMvc = MockMvcBuilders.standaloneSetup(dreamAnalysisController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }


    @Test
    void 꿈_해몽_생성_성공() throws Exception {
        Map<String, Object> dummy = new HashMap<>();
        dummy.put("rephrase", new RephraseResponse("💭", "하늘을 나는 꿈", "하늘을 자유롭게 날았다"));
        dummy.put("unconscious", new UnconsciousResponse("기쁨", "자유와 성취감을 느끼고 있음"));
        dummy.put("recommendation", new RecommendationResponse("최근 스트레스 요인을 줄이고, 긍정적인 활동 계획을 세워보세요."));

        Mockito.when(dreamAnalysisService.analyzeOverall(any()))
                .thenReturn(dummy);

        mockMvc.perform(post("/ai/dreams/overall")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"하늘을 나는 꿈\", \"dreamDate\":\"2025-08-29\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("꿈 해몽 생성이 완료되었습니다"))
                .andExpect(jsonPath("$.data.rephrase.emoji").value("💭"))
                .andExpect(jsonPath("$.data.rephrase.title").value("하늘을 나는 꿈"))
                .andExpect(jsonPath("$.data.rephrase.content").value("하늘을 자유롭게 날았다"));
    }

    @Test
    void 무의식_분석_생성_성공() throws Exception {
        UnconsciousAnalysisResponse dummy = new UnconsciousAnalysisResponse(
                "최근 반복되는 불안과 긴장 패턴이 꿈에 반영되어 있습니다."
        );

        Mockito.when(dreamAnalysisService.analyzeUnconscious(anyLong()))
                .thenReturn(dummy);

        mockMvc.perform(post("/ai/dreams/unconscious")
                        .param("userId", "1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("무의식 분석이 완료되었습니다"))
                .andExpect(jsonPath("$.data.unconsciousMeaning")
                        .value("최근 반복되는 불안과 긴장 패턴이 꿈에 반영되어 있습니다."));
    }

    @Test
    void 무의식_분석_예외_및_성공_테스트() throws Exception {
        // 최근 꿈 7개 정상 데이터
        List<Object> recent7Dreams = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            recent7Dreams.add(new Object());
        }

        Mockito.when(dreamAnalysisService.analyzeUnconscious(anyLong()))
                .thenAnswer(invocation -> {
                    if (recent7Dreams.size() < 7) {
                        throw new BadRequestException(ErrorMessage.UNCONSCIOUS_MIN_DREAMS);
                    }
                    return new UnconsciousAnalysisResponse("최근 반복되는 불안과 긴장 패턴이 꿈에 반영되어 있습니다.");
                });

        // 정상 7개일 때
        mockMvc.perform(post("/ai/dreams/unconscious")
                        .param("userId", "1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("무의식 분석이 완료되었습니다"))
                .andExpect(jsonPath("$.data.unconsciousMeaning")
                        .value("최근 반복되는 불안과 긴장 패턴이 꿈에 반영되어 있습니다."));

        // 6개만 남겼을 때 (테스트 시 수동으로 recent7Dreams.remove(0) 등)
        recent7Dreams.remove(0); // 이제 6개
        mockMvc.perform(post("/ai/dreams/unconscious")
                        .param("userId", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("무의식 분석을 위해 최소 7개의 꿈 해석이 필요합니다"));
    }

}
