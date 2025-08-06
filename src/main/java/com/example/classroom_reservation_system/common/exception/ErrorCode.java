package com.example.classroom_reservation_system.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // ===== 회원 관련 =====
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND", "존재하지 않는 회원입니다."),
    CANNOT_DELETE_ADMIN(HttpStatus.BAD_REQUEST, "CANNOT_DELETE_ADMIN", "관리자 계정은 삭제할 수 없습니다."),
    DUPLICATE_ID(HttpStatus.BAD_REQUEST, "DUPLICATE_ID", "이미 사용 중인 아이디입니다."),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "DUPLICATE_EMAIL", "이미 사용 중인 이메일입니다."),
    INVALID_ROLE_FOR_SIGNUP(HttpStatus.BAD_REQUEST, "INVALID_ROLE_FOR_SIGNUP", "해당 역할로는 회원가입이 불가능합니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "LOGIN_FAILED", "아이디 또는 비밀번호가 일치하지 않습니다."),
    PASSWORD_CONFIRM_NOT_MATCH(HttpStatus.BAD_REQUEST, "PASSWORD_CONFIRM_NOT_MATCH", "비밀번호가 일치하지 않습니다."),
    INVALID_ID_FORMAT(HttpStatus.BAD_REQUEST, "INVALID_ID_FORMAT", "아이디는 6~15자 이내의 영어와 숫자만 입력할 수 있습니다."),
    CANNOT_USE_OLD_PASSWORD(HttpStatus.BAD_REQUEST, "CANNOT_USE_OLD_PASSWORD", "이전과 동일한 비밀번호는 사용할 수 없습니다."),

    // ===== 토큰/인증 관련 =====
    UNAUTHORIZED_MEMBER(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED_MEMBER", "인증되지 않은 사용자입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "접근 권한이 없습니다."),
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
    CURRENT_PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "CURRENT_PASSWORD_NOT_MATCH", "현재 비밀번호와 일치하지 않습니다."),

    // ===== 건물 관련 =====
    DUPLICATE_BUILDING_NAME(HttpStatus.BAD_REQUEST, "DUPLICATE_BUILDING_NAME", "이미 존재하는 건물입니다."),

    // ===== 강의실 관련 =====
    BUILDING_NOT_FOUND(HttpStatus.NOT_FOUND, "BUILDING_NOT_FOUND", "존재하지 않는 건물입니다."),
    CLASSROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "CLASSROOM_NOT_FOUND", "존재하지 않는 강의실입니다."),
    DUPLICATE_CLASSROOM_NAME(HttpStatus.BAD_REQUEST, "DUPLICATE_CLASSROOM_NAME", "이미 존재하는 강의실입니다."),
    CLASSROOM_ALREADY_RESERVED(HttpStatus.BAD_REQUEST, "CLASSROOM_ALREADY_RESERVED", "이미 예약된 강의실입니다."),
    CLASSROOM_HAS_ACTIVE_RESERVATIONS(HttpStatus.BAD_REQUEST, "CLASSROOM_HAS_ACTIVE_RESERVATIONS", "이미 진행 중인 예약이 있는 강의실입니다."),

    // ===== 예약 관련 =====
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "RESERVATION_NOT_FOUND", "존재하지 않는 예약입니다."),
    RESERVATION_ALREADY_CANCELED(HttpStatus.BAD_REQUEST, "RESERVATION_ALREADY_CANCELED", "이미 취소된 예약은 완료 처리할 수 없습니다."),
    RESERVATION_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "RESERVATION_ALREADY_COMPLETED","이미 완료된 예약입니다."),
    RESERVATION_NOT_CANCELLABLE(HttpStatus.BAD_REQUEST, "RESERVATION_NOT_CANCELLABLE", "이미 완료되었거나 취소된 예약은 취소할 수 없습니다."),
    RESERVATION_ACCESS_DENIED(HttpStatus.FORBIDDEN, "RESERVATION_ACCESS_DENIED", "해당 예약에 접근할 권한이 없습니다."),
    INVALID_RESERVATION_TIME(HttpStatus.BAD_REQUEST, "INVALID_RESERVATION_TIME", "유효하지 않은 예약 시간입니다."),
    ALREADY_PENDING_REQUEST(HttpStatus.BAD_REQUEST, "ALREADY_PENDING_REQUEST", "이미 처리 대기 중인 변경 요청이 존재합니다."),

    // ===== 알림 관련 =====
    NOTIFICATION_NOT_FOUND(HttpStatus.BAD_REQUEST, "NOTIFICATION_NOT_FOUND", "알림을 찾을 수 없습니다."),
    NOTIFICATION_ACCESS_DENIED(HttpStatus.FORBIDDEN, "NOTIFICATION_ACCESS_DENIED", "자신의 알림만 읽을 수 있습니다.."),
    HISTORY_NOT_FOUND(HttpStatus.BAD_REQUEST, "HISTORY_NOT_fOUND", " 예약에 대한 기록을 찾을 수 없습니다."),

    // ===== 예약 변경 관련 =====
    REQUEST_NOT_FOUND(HttpStatus.BAD_REQUEST, "REQUEST_NOT_FOUND", "요청을 찾을 수 없습니다."),
    ALREADY_PROCESSED_REQUEST(HttpStatus.BAD_REQUEST, "ALREADY_PROCESSED_REQUEST", "이미 처리된 요청입니다."),

    // ===== 공지사항 관련 =====
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTICE_NOT_FOUND", "존재하지 않는 공지사항입니다."),
    NO_UPDATE_CONTENT(HttpStatus.BAD_REQUEST, "NO_UPDATE_CONTENT", "수정할 내용이 없습니다."),

    // ===== 입력값 검증 관련 =====
    VALIDATION_FAIL(HttpStatus.BAD_REQUEST, "VALIDATION_FAIL", "입력값 검증 실패"),

    // ===== 시스템 에러 =====
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "서버 내부 오류"),
    MAIL_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "MAIL_SEND_FAIL", "메일 전송에 실패했습니다."),
    MAIL_SEND_FAIL_BUT_TOKEN_SAVED(HttpStatus.INTERNAL_SERVER_ERROR, "MAIL_SEND_FAIL_BUT_TOKEN_SAVED", "요청은 처리되었으나, 메일 발송에 실패햇습니다. 잠시 후 다시 시도해 주세요");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
