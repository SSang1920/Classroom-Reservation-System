package com.example.classroom_reservation_system.security.jwt;

import com.example.classroom_reservation_system.entity.Member;
import com.example.classroom_reservation_system.exception.CustomException;
import com.example.classroom_reservation_system.exception.ErrorCode;
import com.example.classroom_reservation_system.security.CustomUserDetails;
import com.example.classroom_reservation_system.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // 토큰 없으면 다음 필터로 넘김
            return;
        }

        String token = authHeader.substring(7); // "Bearer" 이후 부분 추출

        try {
            // AccessToken 유효성 검사
            jwtUtil.validateTokenOrThrow(token);

            // 토큰에서 정보 추출
            String memberUuid = jwtUtil.getMemberUuidFromToken(token);
            Member member = memberService.findByMemberUuid(memberUuid);
            CustomUserDetails userDetails = new CustomUserDetails(member);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, //사용자 정보
                            null,
                            userDetails.getAuthorities() // 권한 가져오기
                    );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // SecurityContext 등록
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (CustomException e) {
            logger.warn("JWT 인증 실패: {}", e.getMessage());
            setErrorResponse(response, e.getErrorCode());
            return;
        }

        filterChain.doFilter(request, response);
    }
    private void setErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException{
        response.setStatus(errorCode.getStatus().value());
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", errorCode.getStatus().value());
        errorDetails.put("errorCode", errorCode.getErrorCode());
        errorDetails.put("message", errorCode.getMessage());

        response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
    }
}
