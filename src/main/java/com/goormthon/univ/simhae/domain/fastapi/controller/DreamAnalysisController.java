package com.goormthon.univ.simhae.domain.fastapi.controller;

import com.goormthon.univ.simhae.domain.fastapi.dto.overall.DreamAnalyzeRequest;
import com.goormthon.univ.simhae.domain.fastapi.dto.unconscious.UnconsciousAnalysisResponse;
import com.goormthon.univ.simhae.domain.fastapi.service.DreamAnalysisService;
import com.goormthon.univ.simhae.global.dto.SuccessResponse;
import com.goormthon.univ.simhae.global.exception.message.SuccessMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ai/dreams")
@RequiredArgsConstructor
public class DreamAnalysisController {

    private final DreamAnalysisService dreamAnalysisService;

    // AI 꿈 해몽 - 재진술, 무의식, 제안
    @PostMapping("/overall")
    public ResponseEntity<SuccessResponse<Map<String, Object>>> analyzeOverall(
            @RequestBody @Valid DreamAnalyzeRequest request) {

        Map<String, Object> result = dreamAnalysisService.analyzeOverall(request);
        return ResponseEntity.status(201)
                .body(SuccessResponse.of(SuccessMessage.CREATE_SUCCESS, result));
    }

    // 무의식 분석 - 최근 7개 꿈
    @PostMapping("/unconscious")
    public ResponseEntity<SuccessResponse<UnconsciousAnalysisResponse>> analyzeUnconscious(
            @RequestParam Long userId) {

        UnconsciousAnalysisResponse result = dreamAnalysisService.analyzeUnconscious(userId);
        return ResponseEntity.status(201)
                .body(SuccessResponse.of(SuccessMessage.UNCONSCIOUS_SUCCESS, result));
    }

}
