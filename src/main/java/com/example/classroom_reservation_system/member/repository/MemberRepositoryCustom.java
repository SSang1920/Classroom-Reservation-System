package com.example.classroom_reservation_system.member.repository;

import com.example.classroom_reservation_system.member.entity.Member;
import com.example.classroom_reservation_system.member.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface MemberRepositoryCustom {
    Page<Member> search(String loginId, String name, String email, Role role, LocalDate date, Pageable pageable);
}
