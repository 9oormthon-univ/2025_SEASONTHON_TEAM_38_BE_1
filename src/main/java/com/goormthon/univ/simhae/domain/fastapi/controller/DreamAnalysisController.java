package com.goormthon.univ.simhae.domain.fastapi.controller;

import com.goormthon.univ.simhae.domain.fastapi.dto.overall.DreamAnalyzeRequest;
import com.goormthon.univ.simhae.domain.fastapi.dto.unconscious.UnconsciousAnalysisResponse;
import com.goormthon.univ.simhae.domain.fastapi.service.DreamAnalysisService;
import com.goormthon.univ.simhae.global.dto.ErrorResponse;
import com.goormthon.univ.simhae.global.dto.SuccessResponse;
import com.goormthon.univ.simhae.global.exception.message.SuccessMessage;
import com.goormthon.univ.simhae.global.security.JwtAuthFilter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            @AuthenticationPrincipal JwtAuthFilter.UserPrincipal principal,
            @RequestBody @Valid DreamAnalyzeRequest request
    ) {
        Long userId = principal.id();

        Map<String, Object> result = dreamAnalysisService.analyzeOverall(request, userId);
        return ResponseEntity.status(201)
                .body(SuccessResponse.of(SuccessMessage.CREATE_SUCCESS, result));
    }

    // 무의식 분석 - 최근 7개 꿈
// 무의식 분석 - 최근 7개 꿈
    @PostMapping("/unconscious")
    public ResponseEntity<SuccessResponse<UnconsciousAnalysisResponse>> analyzeUnconscious(
            @AuthenticationPrincipal JwtAuthFilter.UserPrincipal principal
    ) {

        Long userId = principal.id();

        UnconsciousAnalysisResponse result = dreamAnalysisService.analyzeUnconscious(userId);
        return ResponseEntity.status(201)
                .body(SuccessResponse.of(SuccessMessage.UNCONSCIOUS_SUCCESS, result));
    }

}
