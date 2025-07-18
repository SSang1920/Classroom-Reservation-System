package com.example.classroom_reservation_system.config.security;

import com.example.classroom_reservation_system.config.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())   // CSRF 비활성화
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))   // 세션 방식 사용 X(토큰 기반 인증 사용)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                // --- 정적 리소스 ---
                                "/js/**", "/css/**", "/img/**",

                                // --- View 페이지 (인증 없이 접근 가능) ---
                                "/", "/login", "/signup", "/find-password", "/reset-password",
                                "/admin/main", "/admin/members",
                                "reserve", "history",

                                // --- API (인증 없이 접근 가능) ---
                                "/api/auth/login",              // 로그인
                                "/api/auth/refresh",            // 토큰 재발급
                                "/api/auth/password/**",        // 비밀번호 찾기, 재설정 관련 모든 api
                                "/api/members/signup",          // 회원가입
                                "/api/members/check-id",        // ID 중복 검사
                                "/api/members/check-email",     // 이메일 중복 검사
                                "/api/classrooms"               // 강의실 목록 조회
                        ).permitAll()
                        // SSE 구독 경로는 인증된 사용자만 접근 가능하도록 추가
                        .requestMatchers("/api/notifications/**").authenticated()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")  // 'ADMIN' 역할을 가진 사용자만 접근 가능
                        .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);  // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 등록

        return http.build();
    }

    // 비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
