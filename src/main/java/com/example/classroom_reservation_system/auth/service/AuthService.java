package com.example.classroom_reservation_system.auth.service;

import com.example.classroom_reservation_system.auth.dto.request.LoginRequest;
import com.example.classroom_reservation_system.auth.dto.request.TokenRequest;
import com.example.classroom_reservation_system.auth.dto.response.LoginResponse;
import com.example.classroom_reservation_system.auth.dto.response.TokenResponse;
import com.example.classroom_reservation_system.auth.token.RefreshToken;
import com.example.classroom_reservation_system.common.exception.CustomException;
import com.example.classroom_reservation_system.common.exception.ErrorCode;
import com.example.classroom_reservation_system.config.jwt.JwtProperties;
import com.example.classroom_reservation_system.config.jwt.JwtUtil;
import com.example.classroom_reservation_system.config.jwt.TokenType;
import com.example.classroom_reservation_system.member.entity.Member;
import com.example.classroom_reservation_system.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final MemberService memberService;
    private final MemberLoginService memberLoginService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;

    /**
     * 로그인
     */
    public LoginResponse login(LoginRequest request) {
        // member 찾기
        Member member = memberLoginService.login(request.getId(), request.getPassword());

        // JWT 생성
        String accessToken = jwtUtil.generateAccessToken(member.getMemberUuid(), member.getId(), member.getName(), member.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(member.getMemberUuid());

        // RefreshToken 저장
        refreshTokenService.saveRefreshToken(
                member.getMemberUuid(),
                refreshToken,
                jwtProperties.getRefreshTokenExpirationTime()
        );

        return new LoginResponse(accessToken, refreshToken);
    }

    /**
     * 토큰 재발급
     */
    public TokenResponse refresh(TokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // 토큰 null 또는 공백 검증
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new CustomException(ErrorCode.TOKEN_NOT_FOUND);
        }

        // 토큰 검증
        jwtUtil.validateTokenOrThrow(refreshToken, TokenType.REFRESH);

        RefreshToken storedToken = refreshTokenService.findByToken(refreshToken);

        // 새 AccessToken 발급
        Member member = memberService.findByMemberUuid(storedToken.getMemberUuid());
        String newAccessToken = jwtUtil.generateAccessToken(member.getMemberUuid(), member.getId(), member.getName() ,member.getRole());

        // 새 RefreshToken 발급하고 DB의 기존 토큰 업데이트
        String newRefreshToken = jwtUtil.generateRefreshToken(member.getMemberUuid());
        storedToken.updateToken(
                newRefreshToken,
                jwtUtil.getExpiryDateFromToken(newRefreshToken).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()
        );

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    /**
     * 로그아웃: RefreshToken 삭제
     */
    public void logout(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new CustomException(ErrorCode.AUTHENTICATION_NOT_FOUND);
        }

        String memberUuid = authentication.getPrincipal().toString();
        refreshTokenService.deleteByMemberUuid(memberUuid);
    }
}
