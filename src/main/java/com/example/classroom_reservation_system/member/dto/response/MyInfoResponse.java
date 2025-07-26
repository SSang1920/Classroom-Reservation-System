package com.example.classroom_reservation_system.member.dto.response;

import com.example.classroom_reservation_system.member.entity.Member;
import com.example.classroom_reservation_system.member.entity.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyInfoResponse {

    private String loginId;
    private String name;
    private String email;
    private Role role;

    public static MyInfoResponse from(Member member) {
        return MyInfoResponse.builder()
                .loginId(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .role(member.getRole())
                .build();
    }
}
