package com.example.classroom_reservation_system.admin.dto.response;

import com.example.classroom_reservation_system.member.entity.Member;
import com.example.classroom_reservation_system.member.entity.Role;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AdminMemberListResponse {

    private Long memberPkId;
    private String loginId;
    private String name;
    private String email;
    private Role role;
    private LocalDateTime createdAt;

    /**
     * DTO 변환 메서드
     * @param member
     * @return AdminMemberListResponse
     */
    public static AdminMemberListResponse from(Member member) {
        return AdminMemberListResponse.builder()
                .memberPkId(member.getMemberId())
                .loginId(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .role(member.getRole())
                .createdAt(member.getCreatedAt())
                .build();
    }
}
