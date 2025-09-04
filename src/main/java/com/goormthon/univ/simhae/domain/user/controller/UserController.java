package com.goormthon.univ.simhae.domain.user.controller;


import com.goormthon.univ.simhae.domain.user.dto.UserRegisterResponse;
import com.goormthon.univ.simhae.domain.user.service.UserService;
import com.goormthon.univ.simhae.global.dto.ErrorResponse;
import com.goormthon.univ.simhae.global.dto.SuccessResponse;
import com.goormthon.univ.simhae.global.exception.message.SuccessMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    /** 프론트가 앱 시작/설치 직후 1회 호출 */
    @PostMapping("/anonymous")
    public ResponseEntity<?> registerAnonymous(
            @RequestHeader(name = "X-Anonymous-Id", required = false) String externalId
    ) {
        if (externalId == null || externalId.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ErrorResponse.of(400, "X-Anonymous-Id 헤더가 필요합니다"));
        }
        UserRegisterResponse data = userService.registerByHeader(externalId);
        return ResponseEntity.ok(SuccessResponse.of(SuccessMessage.REGISTER_SUCCESS, data));
    }
}
