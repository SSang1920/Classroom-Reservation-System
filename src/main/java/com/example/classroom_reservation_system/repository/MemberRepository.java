package com.example.classroom_reservation_system.repository;

import com.example.classroom_reservation_system.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 학번, 교수번호, 관리자번호 중 하나 찾기
    @Query("""
    SELECT m FROM Member m
    WHERE (TYPE(m) = Student AND TREAT(m AS Student).studentId = :id)
       OR (TYPE(m) = Professor AND TREAT(m AS Professor).professorId = :id)
       OR (TYPE(m) = Admin AND TREAT(m AS Admin).adminId = :id)
""")
    Optional<Member> findByUserId(@Param("id") String id);

    // memberUuid로 찾기
    Optional<Member> findByMemberUuid(String memberUuid);

    @Query("""
    SELECT COUNT(m) > 0 FROM Member m
    WHERE (TYPE(m) = Student AND TREAT(m AS Student).studentId = :id)
       OR (TYPE(m) = Professor AND TREAT(m AS Professor).professorId = :id)
       OR (TYPE(m) = Admin AND TREAT(m AS Admin).adminId = :id)
""")
    boolean existsByUserId(@Param("id") String id);
}
