package com.goormthon.univ.simhae.global.exception;

import com.goormthon.univ.simhae.global.dto.ErrorResponse;
import com.goormthon.univ.simhae.global.exception.message.ErrorMessage;
import com.goormthon.univ.simhae.global.exception.model.BadRequestException;
import com.goormthon.univ.simhae.global.exception.model.SimhaeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(final BadRequestException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(e.getErrorMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();

        // 기본 메시지
        ErrorMessage errorMessage = ErrorMessage.REQUIRED_FIELD_MISSING;

        if (fieldError != null) {
            String code = fieldError.getCode();
            Object rejectedValue = fieldError.getRejectedValue();
            int length = rejectedValue != null ? rejectedValue.toString().length() : 0;

            switch (code) {
                case "NotNull", "NotBlank" -> errorMessage = ErrorMessage.REQUIRED_FIELD_MISSING;
                case "Size" -> {
                    int min = (int) fieldError.getArguments()[1];
                    int max = (int) fieldError.getArguments()[2];

                    if (length < min) {
                        errorMessage = ErrorMessage.INPUT_VALUE_TOO_SHORT; // 최소 길이 미만
                    } else if (length > max) {
                        errorMessage = ErrorMessage.INPUT_VALUE_TOO_LONG;  // 최대 길이 초과
                    }
                }
            }
        }

        return ResponseEntity.badRequest().body(ErrorResponse.of(errorMessage));
    }

    @ExceptionHandler(SimhaeException.class)
    public ResponseEntity<ErrorResponse> handleSimhaeException(final SimhaeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(e.getErrorMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(final Exception e) {
        // 로그 찍기 등 필요 시
        e.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(ErrorMessage.INTERNAL_SERVER_ERROR));
    }

}
