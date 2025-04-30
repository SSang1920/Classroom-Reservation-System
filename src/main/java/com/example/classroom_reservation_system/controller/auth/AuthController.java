package com.example.classroom_reservation_system.controller.auth;

import com.example.classroom_reservation_system.config.JwtProperties;
import com.example.classroom_reservation_system.dto.requestDto.LoginRequest;
import com.example.classroom_reservation_system.dto.requestDto.TokenRequest;
import com.example.classroom_reservation_system.dto.responseDto.LoginResponse;
import com.example.classroom_reservation_system.dto.responseDto.TokenResponse;
import com.example.classroom_reservation_system.entity.Member;
import com.example.classroom_reservation_system.entity.RefreshToken;
import com.example.classroom_reservation_system.entity.Role;
import com.example.classroom_reservation_system.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.MemberLoginService;
import service.RefreshTokenService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final MemberLoginService memberLoginService;
    private final RefreshTokenService refreshTokenService;

    /**
     * 로그인: AccessToken, RefreshToken 발급
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        Member member = memberLoginService.login(request.getId(), request.getPassword());

        String memberUuid = member.getMemberUuid();
        String userId = member.getId();
        String accessToken = jwtUtil.generateAccessToken(memberUuid, userId, member.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(memberUuid);

        // DB에 refreshToken 저장
        refreshTokenService.saveRefreshToken(memberUuid, refreshToken, jwtProperties.getRefreshTokenExpirationTime());

        return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken));
    }

    /**
     * RefreshToken으로 AccessToken 재발급
     */
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody TokenRequest request) {

        String refreshToken = request.getRefreshToken();

        // JWT 검증
        if (!jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.badRequest().body(null);
        }

        // DB에 저장된 토큰인지 검증
        RefreshToken storedToken = refreshTokenService.findByToken(refreshToken);
        String memberUuid = storedToken.getMemberUuid();

        // 새로운 AccessToken 발급
        String userId = jwtUtil.getUserIdFromToken(refreshToken);
        String role = jwtUtil.getRoleFromToken(refreshToken);
        String newAccessToken = jwtUtil.generateAccessToken(
                memberUuid,
                userId != null ? userId : "unknown",
                role != null ? Enum.valueOf(Role.class, role) : Role.STUDENT
        );

        return ResponseEntity.ok(new TokenResponse(newAccessToken));
    }

    /**
     * 로그아웃: RefreshToken 삭제
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Authentication authentication) {

        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.badRequest().build();
        }

        String memberUuid = authentication.getPrincipal().toString();
        refreshTokenService.deleteByMemberUuid(memberUuid);

        return ResponseEntity.ok().build();
    }
}
