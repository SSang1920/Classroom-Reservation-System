package com.example.classroom_reservation_system.admin.service;

import com.example.classroom_reservation_system.admin.dto.response.AdminMemberListResponse;
import com.example.classroom_reservation_system.common.exception.CustomException;
import com.example.classroom_reservation_system.common.exception.ErrorCode;
import com.example.classroom_reservation_system.member.entity.Member;
import com.example.classroom_reservation_system.member.entity.Role;
import com.example.classroom_reservation_system.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원 목록을 검색 조건에 따라 조회
     * @param loginId       회원 아이디
     * @param name          회원 이름
     * @param email         회원 이메일
     * @param role          회원 역할
     * @param date          회원 가입일
     * @param pageable      페이지 정보
     * @return
     */
    public Page<AdminMemberListResponse> searchMembers(
            String loginId, String name, String email, Role role, LocalDate date, Pageable pageable
            ) {
        // 메서드 호출하고, 결과를 DTO로 반환
        return memberRepository.search(loginId, name, email, role, date, pageable)
                .map(AdminMemberListResponse::from);
    }

    /**
     * 관리자가 특정 회원을 삭제
     *
     * @param memberPkId 삭제할 회원의 PK
     */
    @Transactional
    public void deleteMember(Long memberPkId) {
        // 삭제하려는 회원이 존재하는지 확인
        Member member = memberRepository.findById(memberPkId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 삭제하려는 회원이 관리자인지 확인
        if (member.getRole() == Role.ADMIN) {
            throw new CustomException(ErrorCode.CANNOT_DELETE_ADMIN);
        }

        memberRepository.delete(member);
    }
}
