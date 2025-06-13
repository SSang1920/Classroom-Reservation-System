package com.example.classroom_reservation_system.service;

import com.example.classroom_reservation_system.entity.Member;
import com.example.classroom_reservation_system.entity.Role;
import com.example.classroom_reservation_system.exception.CustomException;
import com.example.classroom_reservation_system.exception.ErrorCode;
import com.example.classroom_reservation_system.repository.member.AdminRepository;
import com.example.classroom_reservation_system.repository.member.ProfessorRepository;
import com.example.classroom_reservation_system.repository.member.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.classroom_reservation_system.repository.member.MemberRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberLoginService {

    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 로그인 처리
     * @param id studentId, professorId, adminId 중 하나
     * @param password
     * @return 인증된 Member 객체
     */
    public Member login(String id, String password) {

        // ID로 Member 조회 (학생, 교수, 관리자)
        Optional<? extends Member> memberOpt =
                studentRepository.findByStudentId(id).map(m -> (Member) m)
                        .or(() -> professorRepository.findByProfessorId(id).map(m -> (Member) m))
                        .or(() -> adminRepository.findByAdminId(id).map(m -> (Member) m));

        Member member = memberOpt.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        // 로그인 성공
        return member;
    }
}
