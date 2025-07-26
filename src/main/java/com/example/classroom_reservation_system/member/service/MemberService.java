package com.example.classroom_reservation_system.member.service;

import com.example.classroom_reservation_system.member.dto.response.MyInfoResponse;
import com.example.classroom_reservation_system.member.entity.Member;
import com.example.classroom_reservation_system.common.exception.CustomException;
import com.example.classroom_reservation_system.common.exception.ErrorCode;
import com.example.classroom_reservation_system.member.repository.AdminRepository;
import com.example.classroom_reservation_system.member.repository.MemberRepository;
import com.example.classroom_reservation_system.member.repository.ProfessorRepository;
import com.example.classroom_reservation_system.member.repository.StudentRepository;
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

    /**
     * 현재 로그인한 사용자의 계정을 삭제
     * @param memberUuid 현재 로그인한 사용자의 UUID
     */
    @Transactional
    public void deleteMyAccount(String memberUuid) {
        Member member = memberRepository.findByMemberUuid(memberUuid)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        memberRepository.delete(member);
    }

    /**
     * 내 정보 조회
     * @param memberUuid 현재 로그인한 사용자의 UUID
     */
    public MyInfoResponse getMyInfo(String memberUuid) {
        Member member = memberRepository.findByMemberUuid(memberUuid)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        return MyInfoResponse.from(member);
    }
}
