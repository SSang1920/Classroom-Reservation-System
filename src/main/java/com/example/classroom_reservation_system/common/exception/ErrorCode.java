package com.example.classroom_reservation_system.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // ===== 회원 관련 =====
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND", "존재하지 않는 회원입니다."),
    DUPLICATE_ID(HttpStatus.BAD_REQUEST, "DUPLICATE_ID", "이미 사용 중인 아이디입니다."),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "DUPLICATE_EMAIL", "이미 사용 중인 이메일입니다."),
    INVALID_ROLE_FOR_SIGNUP(HttpStatus.BAD_REQUEST, "INVALID_ROLE_FOR_SIGNUP", "해당 역할로는 회원가입이 불가능합니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "LOGIN_FAILED", "아이디 또는 비밀번호가 일치하지 않습니다."),
    PASSWORD_CONFIRM_NOT_MATCH(HttpStatus.BAD_REQUEST, "PASSWORD_CONFIRM_NOT_MATCH", "비밀번호가 일치하지 않습니다."),
    INVALID_ID_FORMAT(HttpStatus.BAD_REQUEST, "INVALID_ID_FORMAT", "아이디는 6~15자 이내의 영어와 숫자만 입력할 수 있습니다."),
    CANNOT_USE_OLD_PASSWORD(HttpStatus.BAD_REQUEST, "CANNOT_USE_OLD_PASSWORD", "이전과 동일한 비밀번호는 사용할 수 없습니다."),

    // ===== 토큰/인증 관련 =====
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "ACCESS_TOKEN_EXPIRED", "AccessToken이 만료되었습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_ACCESS_TOKEN", "유효하지 않는 AccessToken입니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "REFRESH_TOKEN_EXPIRED", "RefreshToken이 만료되었습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_REFRESH_TOKEN", "유효하지 않은 RefreshToken입니다."),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "TOKEN_NOT_FOUND", "토큰이 존재하지 않습니다."),
    TOKEN_MISMATCH(HttpStatus.FORBIDDEN, "TOKEN_MISMATCH", "서버에 저장된 토큰과 일치하지 않습니다."),
    AUTHENTICATION_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTHENTICATION_NOT_FOUND", "인증 정보가 없습니다."),

    // ===== 비밀번호 재설정 관련 =====
    INVALID_RESET_TOKEN(HttpStatus.BAD_REQUEST, "INVALID_RESET_TOKEN", "유효하지 않은 비밀번호 재설정 토큰입니다."),
    PASSWORD_RESET_TOKEN_ALREADY_USED(HttpStatus.BAD_REQUEST, "PASSWORD_RESET_TOKEN_ALREADY_USED", "이미 사용된 토큰입니다."),
    PASSWORD_RESET_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "PASSWORD_RESET_TOKEN_EXPIRED", "만료된 토큰입니다."),

    // ===== 강의실 관련 =====
    CLASSROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "CLASSROOM_NOT_FOUND", "존재하지 않는 강의실입니다."),
    CLASSROOM_ALREADY_RESERVED(HttpStatus.BAD_REQUEST, "CLASSROOM_ALREADY_RESERVED", "이미 예약된 강의실입니다."),
    CLASSROOM_HAS_ACTIVE_RESERVATIONS(HttpStatus.BAD_REQUEST, "CLASSROOM_HAS_ACTIVE_RESERVATIONS", "이미 진행 중인 예약이 있는 강의실입니다."),

    // ===== 예약 관련 =====
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "RESERVATION_NOT_FOUND", "존재하지 않는 예약입니다."),
    RESERVATION_ALREADY_CANCELED(HttpStatus.BAD_REQUEST, "RESERVATION_ALREADY_CANCELED", "이미 취소된 예약은 완료 처리할 수 없습니다."),
    RESERVATION_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "RESERVATION_ALREADY_COMPLETED","이미 완료된 예약입니다."),
    RESERVATION_NOT_CANCELLABLE(HttpStatus.BAD_REQUEST, "RESERVATION_NOT_CANCELLABLE", "이미 완료되었거나 취소된 예약은 취소할 수 없습니다."),

    // ===== 입력값 검증 관련 =====
    VALIDATION_FAIL(HttpStatus.BAD_REQUEST, "VALIDATION_FAIL", "입력값 검증 실패"),

    // ===== 시스템 에러 =====
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "서버 내부 오류"),
    MAIL_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "MAIL_SEND_FAIL", "메일 전송에 실패했습니다.");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
