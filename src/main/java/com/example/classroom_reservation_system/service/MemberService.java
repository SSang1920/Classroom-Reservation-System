package com.example.classroom_reservation_system.service;

import com.example.classroom_reservation_system.entity.Member;
import com.example.classroom_reservation_system.exception.CustomException;
import com.example.classroom_reservation_system.exception.ErrorCode;
import com.example.classroom_reservation_system.repository.member.AdminRepository;
import com.example.classroom_reservation_system.repository.member.MemberRepository;
import com.example.classroom_reservation_system.repository.member.ProfessorRepository;
import com.example.classroom_reservation_system.repository.member.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final AdminRepository adminRepository;

    /**
     * UUID로 회원 찾기
     * @param memberUuid
     */
    public Member findByMemberUuid(String memberUuid) {
        return memberRepository.findByMemberUuid(memberUuid)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    /**
     * 아이디로 회원 찾기
     * @param id
     */
    public Optional<Member> findMemberById(String id) {
        return studentRepository.findByStudentId(id)
                .map(Member.class::cast)
                .or(() -> professorRepository.findByProfessorId(id).map(Member.class::cast))
                .or(() -> adminRepository.findByAdminId(id).map(Member.class::cast));
    }
}
