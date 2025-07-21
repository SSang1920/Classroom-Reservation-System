package com.example.classroom_reservation_system.admin.service;

import com.example.classroom_reservation_system.admin.dto.response.AdminMemberListResponse;
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
public class AdminService {

    private final MemberRepository memberRepository;

    public Page<AdminMemberListResponse> searchMembers(
            String loginId, String name, String email, Role role, LocalDate date, Pageable pageable
            ) {
        // 메서드 호출하고, 결과를 DTO로 반환
        return memberRepository.search(loginId, name, email, role, date, pageable)
                .map(AdminMemberListResponse::from);
    }
}
