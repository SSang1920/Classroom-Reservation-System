package com.example.classroom_reservation_system.admin.service;

import com.example.classroom_reservation_system.admin.dto.response.AdminMemberListResponse;
import com.example.classroom_reservation_system.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final MemberRepository memberRepository;

    public Page<AdminMemberListResponse> getMemberList(Pageable pageable) {
        return memberRepository.findAll(pageable)
                .map(AdminMemberListResponse::from);
    }
}
