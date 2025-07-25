package com.example.classroom_reservation_system.config.jwt;

import com.example.classroom_reservation_system.member.entity.Member;
import com.example.classroom_reservation_system.common.exception.CustomException;
import com.example.classroom_reservation_system.common.exception.ErrorCode;
import com.example.classroom_reservation_system.config.security.CustomUserDetails;
import com.example.classroom_reservation_system.member.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final MemberService memberService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain
    ) throws ServletException, IOException {

        // Authorization 헤더에서 토큰 가져오기
        String token = null; // JWT 토큰 변수 초기화
        String authHeader = request.getHeader("Authorization");

        // 헤더에서 토큰을 먼저 확인
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // "Bearer " 다음 부분이 실제 토큰이므로 substring(7)로 잘라냄
            token = authHeader.substring(7);
        }

        // 헤더에 토큰이 없고, 요청이 SSE 구독(/subscribe)일 경우 쿼리 파라미터에서 토큰을 추출
        else if ("/api/notifications/subscribe".equals(request.getRequestURI())
                && request.getParameter("token") != null){
            token = request.getParameter("token");
            logger.info("SSE connection token found in query parameter.");
        }

        // 토큰이 여전히 없으면 인증 로직 없이 다음 필터로 진행
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // AccessToken 유효성 검사
            jwtUtil.validateTokenOrThrow(token);

            // 토큰에서 정보 추출
            String memberUuid = jwtUtil.getMemberUuidFromToken(token);
            Member member = memberService.findByMemberUuid(memberUuid);
            CustomUserDetails userDetails = new CustomUserDetails(member);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, //사용자 정보
                            null,
                            userDetails.getAuthorities() // 권한 가져오기
                    );

            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // SecurityContext에 auth 객체 저장
            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (CustomException e) {
            logger.warn("JWT 인증 실패: {}", e.getMessage());

            // SSE 요청이면 연결만 끊고, 응답 본문은 쓰지 않음 (SSE는 response body 쓰면 안 됨)
            if (request.getRequestURI().contains("/api/notifications/subscribe")) {
                return;
            }

            // 그 외 요청은 에러 응답 반환
            setErrorResponse(response, e.getErrorCode());
            return;
        }

        filterChain.doFilter(request, response);
    }
    private void setErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getStatus().value());
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", errorCode.getStatus().value());
        errorDetails.put("errorCode", errorCode.getErrorCode());
        errorDetails.put("message", errorCode.getMessage());

        response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
    }
}
