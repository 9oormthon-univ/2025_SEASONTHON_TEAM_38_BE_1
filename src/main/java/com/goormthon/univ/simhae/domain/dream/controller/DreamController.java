package com.goormthon.univ.simhae.domain.dream.controller;

import com.goormthon.univ.simhae.domain.dream.dto.DreamResponse;
import com.goormthon.univ.simhae.domain.dream.service.DreamService;
import com.goormthon.univ.simhae.domain.user.service.UserService;
import com.goormthon.univ.simhae.global.dto.ErrorResponse;
import com.goormthon.univ.simhae.global.dto.SuccessResponse;
import com.goormthon.univ.simhae.global.exception.message.ErrorMessage;
import com.goormthon.univ.simhae.global.exception.message.SuccessMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/dreams")
@RequiredArgsConstructor
public class DreamController {

    private static final String HEADER_ANON = "X-Anonymous-Id";
    private final DreamService dreamService;
    private final UserService userService;

    /**
     * 월별 조회 + 검색 통합
     * GET /dreams?dreamDate=yyyy-MM&keyword=...  (모두 옵션)
     * - dreamDate 미지정 시 현재 월로 기본값 처리
     * - keyword 있으면 2~20자 검증 + title/content에서 검색
     */
    @GetMapping
    public ResponseEntity<?> getDreamsByMonth(
            @RequestHeader(name = HEADER_ANON, required = false) String externalId,
            @RequestParam(value = "dreamDate", required = false) String month, // yyyy-MM
            @RequestParam(value = "keyword",   required = false) String keyword
    ) {
        if (externalId == null || externalId.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ErrorResponse.of(ErrorMessage.REQUIRED_FIELD_MISSING)); // 헤더 누락
        }
        Long userId = userService.getUserIdOrNullByExternalId(externalId);
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
                        .body(ErrorResponse.of(400, "dreamDate는 yyyy-MM 형식이어야 합니다. 예) 2025-08"));
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
            @RequestHeader(name = HEADER_ANON, required = false) String externalId,
            @RequestParam("dreamDate") String date
    ) {
        if (externalId == null || externalId.isBlank() || date == null || date.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ErrorResponse.of(ErrorMessage.REQUIRED_FIELD_MISSING));
        }
        Long userId = userService.getUserIdOrNullByExternalId(externalId);
        if (userId == null) {
            return ResponseEntity.status(404)
                    .body(ErrorResponse.of(ErrorMessage.RESOURCE_NOT_FOUND));
        }

        final LocalDate day;
        try { day = LocalDate.parse(date); }
        catch (DateTimeParseException e) {
            return ResponseEntity.badRequest()
                    .body(ErrorResponse.of(400, "dreamDate는 yyyy-MM-dd 형식이어야 합니다. 예) 2025-08-22"));
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
            @RequestHeader(name = HEADER_ANON, required = false) String externalId,
            @PathVariable Long dreamId
    ) {
        if (externalId == null || externalId.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ErrorResponse.of(ErrorMessage.REQUIRED_FIELD_MISSING));
        }
        Long userId = userService.getUserIdOrNullByExternalId(externalId);
        if (userId == null) {
            return ResponseEntity.status(404)
                    .body(ErrorResponse.of(ErrorMessage.RESOURCE_NOT_FOUND));
        }

        return dreamService.getDreamDetail(userId, dreamId)
                .<ResponseEntity<?>>map(detail ->
                        ResponseEntity.ok(SuccessResponse.of(SuccessMessage.LOAD_SUCCESS, detail)))
                .orElseGet(() ->
                        ResponseEntity.status(404).body(ErrorResponse.of(ErrorMessage.RESOURCE_NOT_FOUND)));
    }
}

