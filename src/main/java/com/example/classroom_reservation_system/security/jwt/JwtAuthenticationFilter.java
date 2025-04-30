package com.example.classroom_reservation_system.security.jwt;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Authorization 헤더에서 토큰 가져오기
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer")) {
            String token = authHeader.substring(7); // "Bearer" 이후 부분 추출

            try {
                // 토큰 유효성 검사
                if (jwtUtil.validateToken(token)) {

                    // 토큰에서 사용자 정보 가져오기
                    String memberUuid = jwtUtil.getMemberUuidFromToken(token);
                    String role = jwtUtil.getRoleFromToken(token);

                    // 스프링 시큐리티 인증 객체 생성
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    memberUuid,
                                    null,
                                    List.of(() -> "ROLE_" + role)
                            );

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 시큐리티 컨텍스트에 인증 객체 저장
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } catch (JwtException | IllegalArgumentException e) {
                // 토큰 문제가 있을 경우 시큐리티 컨텍스트 초기화
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
