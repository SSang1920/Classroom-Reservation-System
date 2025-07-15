package com.example.classroom_reservation_system.auth.service;

import com.example.classroom_reservation_system.member.entity.Member;
import com.example.classroom_reservation_system.common.exception.CustomException;
import com.example.classroom_reservation_system.common.exception.ErrorCode;
import com.example.classroom_reservation_system.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberLoginService {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 로그인 처리
     * @param id studentId, professorId, adminId 중 하나
     * @param password
     * @return 인증된 Member 객체
     */
    public Member login(String id, String password) {

        // ID로 Member 조회 (학생, 교수, 관리자)
        Optional<Member> memberOpt = memberService.findMemberById(id);

        boolean passwordMatches = memberOpt
                .map(member -> passwordEncoder.matches(password, member.getPassword()))
                .orElse(false);

        // 아이디가 없거나 비밀번호 틀릴시 에러 처리
        if (!passwordMatches) {
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }

        // 로그인 성공
        return memberOpt.get();
    }
}
