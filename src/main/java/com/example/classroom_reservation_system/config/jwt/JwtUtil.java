package com.example.classroom_reservation_system.config.jwt;

import com.example.classroom_reservation_system.member.entity.Role;
import com.example.classroom_reservation_system.common.exception.CustomException;
import com.example.classroom_reservation_system.common.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties jwtProperties;

    // 비밀키 properties 가져오기
    private Key getSigningKey() {
        byte[] keyBytes = jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8);
        log.info("JWT KEY LENGTH: {}", keyBytes.length);
        return Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
    }

    // AccessToken 생성
    public String generateAccessToken(String memberUuid, String userId, String name,  Role role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getAccessTokenExpirationTime());  // 만료일

        return Jwts.builder()
                .setSubject(memberUuid)
                .claim("userId", userId)    // 학번, 교수번호, 관리자번호(ID)
                .claim("name", name) //사용자 실명
                .claim("role", "ROLE_" + role.name()) // 사용자 권한
                .setIssuedAt(now)              // 발급일
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // RefreshToken 생성
    public String generateRefreshToken(String memberUuid) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getRefreshTokenExpirationTime());

        return Jwts.builder()
                .setSubject(memberUuid)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 예외 처리
    private Claims parseClaims(String token, TokenType type) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            if (type == TokenType.REFRESH) {
                throw new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED);
            }
            throw new CustomException(ErrorCode.ACCESS_TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            if (type == TokenType.REFRESH) {
                throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
            }
            throw new CustomException((ErrorCode.INVALID_ACCESS_TOKEN));
        }
    }

    // 토큰에서 memberUuid 가져오기
    public String getMemberUuidFromToken(String token) {
        return parseClaims(token, TokenType.ACCESS).getSubject();
    }

    // 토큰에서 사용자 Id 가져오기
    public String getUserIdFromToken(String token) {
        return parseClaims(token, TokenType.ACCESS).get("userId", String.class);
    }

    // 토큰에서 사용자 role 가져오기
    public String getRoleFromToken(String token) {
        return parseClaims(token, TokenType.ACCESS).get("role", String.class);
    }

    // 토큰 유효성 검사 (Access)
    public void validateTokenOrThrow(String token) {
        parseClaims(token, TokenType.ACCESS);
    }

    // 토큰 유효성 검사(Access/Refresh 구분)
    public void validateTokenOrThrow(String token, TokenType type) {
        parseClaims(token, type);
    }
}
