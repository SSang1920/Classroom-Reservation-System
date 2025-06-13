package com.example.classroom_reservation_system.repository.member;

import com.example.classroom_reservation_system.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // memberUuid로 찾기
    Optional<Member> findByMemberUuid(String memberUuid);
}
