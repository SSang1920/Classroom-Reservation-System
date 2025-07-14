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
                                "/", "/login", "signup", "/find-password", "reset-password",    // View
                                "/js/**", "/css/**", "/img/**", // 정적 리소스

                                // api
                                "/api/auth/login",              // 로그인
                                "/api/auth/refresh",            // 토큰 재발급
                                "/api/auth/password/**",        // 비밀번호 찾기, 재설정 관련 모든 api
                                "/api/members/signup",          // 회원가입
                                "/api/members/check-id",        // ID 중복 검사
                                "/api/members/check-email"       // 이메일 중복 검사
                        ).permitAll()
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")  // 관리자 권한
                        .anyRequest().authenticated()
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
