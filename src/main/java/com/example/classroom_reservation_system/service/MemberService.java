package com.example.classroom_reservation_system.service;

import com.example.classroom_reservation_system.entity.Member;
import com.example.classroom_reservation_system.exception.CustomException;
import com.example.classroom_reservation_system.exception.ErrorCode;
import com.example.classroom_reservation_system.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public Member findByMemberUuid(String memberUuid) {
        return memberRepository.findByMemberUuid(memberUuid)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
