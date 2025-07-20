package com.example.classroom_reservation_system.member.repository;

import com.example.classroom_reservation_system.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    // memberUuid로 찾기
    Optional<Member> findByMemberUuid(String memberUuid);

    boolean existsByEmail(String email);
}
