package com.example.classroom_reservation_system.common.exception;

import com.example.classroom_reservation_system.common.dto.ApiErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @Valid 유효성 실패
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handelValidationsErrors(MethodArgumentNotValidException ex) {
        Map<String, String> hashMap = new HashMap<>();

        for (FieldError e : ex.getBindingResult().getFieldErrors()) {
            hashMap.put(e.getField(), e.getDefaultMessage());
        }

        return ResponseEntity
                .status(ErrorCode.VALIDATION_FAIL.getStatus())
                .body(ApiErrorResponse.of(ErrorCode.VALIDATION_FAIL, hashMap));
    }

    /**
     * 커스텀 예외 처리 (에러코드 기반)
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiErrorResponse> handleCustom(CustomException ex) {

        ErrorCode code = ex.getErrorCode();

        return ResponseEntity
                .status(code.getStatus())
                .body(ApiErrorResponse.of(code));
    }

    /**
     * Spring Security @PreAuthorize 권한 실패
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex){
        ErrorCode code = ErrorCode.ACCESS_DENIED;

        return ResponseEntity
                .status(code.getStatus())
                .body(ApiErrorResponse.of(code));
    }


    /**
     * 기타 모든 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handelGenericException(Exception ex) {
        return ResponseEntity
                .status(ErrorCode.SERVER_ERROR.getStatus())
                .body(ApiErrorResponse.of(ErrorCode.SERVER_ERROR));
    }
}
