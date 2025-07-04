package com.example.classroom_reservation_system.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // ===== 회원 관련 =====
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND", "존재하지 않는 회원입니다."),
    DUPLICATE_ID(HttpStatus.BAD_REQUEST, "DUPLICATE_ID", "이미 사용 중인 아이디입니다."),
    INVALID_ROLE_FOR_SIGNUP(HttpStatus.BAD_REQUEST, "INVALID_ROLE_FOR_SIGNUP", "해당 역할로는 회원가입이 불가능합니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "LOGIN_FAILED", "아이디 또는 비밀번호가 일치하지 않습니다."),
    PASSWORD_CONFIRM_NOT_MATCH(HttpStatus.BAD_REQUEST, "PASSWORD_CONFIRM_NOT_MATCH", "비밀번호가 일치하지 않습니다."),

    // ===== 토큰/인증 관련 =====
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "ACCESS_TOKEN_EXPIRED", "AccessToken이 만료되었습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_ACCESS_TOKEN", "유효하지 않는 AccessToken입니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "REFRESH_TOKEN_EXPIRED", "RefreshToken이 만료되었습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_REFRESH_TOKEN", "유효하지 않은 RefreshToken입니다."),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "TOKEN_NOT_FOUND", "토큰이 존재하지 않습니다."),
    TOKEN_MISMATCH(HttpStatus.FORBIDDEN, "TOKEN_MISMATCH", "서버에 저장된 토큰과 일치하지 않습니다."),
    AUTHENTICATION_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTHENTICATION_NOT_FOUND", "인증 정보가 없습니다."),

    // ===== 입력값 검증 관련 =====
    VALIDATION_FAIL(HttpStatus.BAD_REQUEST, "VALIDATION_FAIL", "입력값 검증 실패"),

    // ===== 시스템 에러 =====
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "서버 내부 오류");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
