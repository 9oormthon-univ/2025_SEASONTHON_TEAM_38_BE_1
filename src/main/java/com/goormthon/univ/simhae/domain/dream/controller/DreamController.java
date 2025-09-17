package com.goormthon.univ.simhae.domain.dream.controller;

import com.goormthon.univ.simhae.domain.dream.dto.DreamResponse;
import com.goormthon.univ.simhae.domain.dream.service.DreamService;
import com.goormthon.univ.simhae.global.dto.ErrorResponse;
import com.goormthon.univ.simhae.global.dto.SuccessResponse;
import com.goormthon.univ.simhae.global.exception.message.ErrorMessage;
import com.goormthon.univ.simhae.global.exception.message.SuccessMessage;
import com.goormthon.univ.simhae.global.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/dreams")
@RequiredArgsConstructor
public class DreamController {

    private final DreamService dreamService;
    /**
     * 월별 조회 + 검색 통합
     * GET /dreams?dreamDate=yyyy-MM&keyword=...  (모두 옵션)
     * - dreamDate 미지정 시 현재 월로 기본값 처리
     * - keyword 있으면 2~20자 검증 + title/content에서 검색
     */
    @GetMapping
    public ResponseEntity<?> getDreamsByMonth(
            @AuthenticationPrincipal JwtAuthFilter.UserPrincipal principal,
            @RequestParam(value = "dreamDate", required = false) String month, // yyyy-MM
            @RequestParam(value = "keyword",   required = false) String keyword
    ) {
        if (principal == null) {
            return ResponseEntity.status(401)
                    .body(ErrorResponse.of(ErrorMessage.UNAUTHORIZED));
        }
        Long userId = principal.id();
        if (userId == null) {
            return ResponseEntity.status(404)
                    .body(ErrorResponse.of(ErrorMessage.USER_NOT_FOUND)); // 미등록 사용자
        }
        // keyword 길이 검증
        if (keyword != null && !keyword.isBlank()) {
            String k = keyword.trim();
            if (k.length() < 2)  return ResponseEntity.badRequest()
                    .body(ErrorResponse.of(ErrorMessage.INPUT_VALUE_TOO_SHORT));
            if (k.length() > 20) return ResponseEntity.badRequest()
                    .body(ErrorResponse.of(ErrorMessage.INPUT_VALUE_TOO_LONG));
            keyword = k;
        }

        // month 파싱(옵션 → 기본값 현재월)
        final YearMonth ym;
        if (month == null || month.isBlank()) {
            ym = YearMonth.now();
        } else {
            try {
                ym = YearMonth.parse(month); // yyyy-MM
            } catch (DateTimeParseException e) {
                return ResponseEntity.badRequest()
                        .body(ErrorResponse.of(ErrorMessage.INVALID_MONTH_FORMAT));
            }
        }

        List<DreamResponse> data = dreamService.getDreamsByMonth(userId, ym, keyword);
        return ResponseEntity.ok(SuccessResponse.of(SuccessMessage.LOAD_SUCCESS, data));
    }

    /**
     * 일별 조회 (dreamDate만)
     * GET /dreams/day?dreamDate=yyyy-MM-dd
     */
    @GetMapping("/day")
    public ResponseEntity<?> getDreamsByDay(
            @AuthenticationPrincipal JwtAuthFilter.UserPrincipal principal,
            @RequestParam("dreamDate") String date
    ) {
        if (principal == null) {
            return ResponseEntity.status(401)
                    .body(ErrorResponse.of(ErrorMessage.UNAUTHORIZED));
        }
        if (date == null || date.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ErrorResponse.of(ErrorMessage.REQUIRED_FIELD_MISSING));
        }
        Long userId = principal.id();

        final LocalDate day;
        try { day = LocalDate.parse(date); }
        catch (DateTimeParseException e) {
            return ResponseEntity.badRequest()
                    .body(ErrorResponse.of(ErrorMessage.INVALID_DAY_FORMAT));
        }

        List<DreamResponse> data = dreamService.getDreamsByDay(userId, day);
        return ResponseEntity.ok(SuccessResponse.of(SuccessMessage.LOAD_SUCCESS, data));
    }

    /**
     * 상세 조회
     * GET /dreams/{dreamId}
     */
    @GetMapping("/{dreamId}")
    public ResponseEntity<?> getDreamDetail(
            @AuthenticationPrincipal JwtAuthFilter.UserPrincipal principal,
            @PathVariable Long dreamId
    ) {
        if (principal == null) {
            return ResponseEntity.status(401)
                    .body(ErrorResponse.of(ErrorMessage.UNAUTHORIZED));
        }
        Long userId = principal.id();

        return dreamService.getDreamDetail(userId, dreamId)
                .<ResponseEntity<?>>map(detail ->
                        ResponseEntity.ok(SuccessResponse.of(SuccessMessage.LOAD_SUCCESS, detail)))
                .orElseGet(() ->
                        ResponseEntity.status(404).body(ErrorResponse.of(ErrorMessage.RESOURCE_NOT_FOUND)));
    }

    @DeleteMapping("/{dreamId}")
    public ResponseEntity<?> deleteDream(
            @AuthenticationPrincipal JwtAuthFilter.UserPrincipal principal,
            @PathVariable Long dreamId
    ){
        if (principal == null) {
            return ResponseEntity.status(401)
                    .body(ErrorResponse.of(ErrorMessage.UNAUTHORIZED));
        }
        Long userId = principal.id();


        // 소유자 일치하는 꿈 삭제
        boolean deleted = dreamService.deleteDream(userId, dreamId);
        if (!deleted) {
            return ResponseEntity.status(404)
                    .body(ErrorResponse.of(ErrorMessage.RESOURCE_NOT_FOUND));
        }

        // 204 No Content (바디 없음)
        return ResponseEntity.ok(SuccessResponse.of(SuccessMessage.DELETED_SUCCESS));
    }
}

