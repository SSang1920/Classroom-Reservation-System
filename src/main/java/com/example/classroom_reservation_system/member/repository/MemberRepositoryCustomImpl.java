package com.example.classroom_reservation_system.member.repository;

import com.example.classroom_reservation_system.member.entity.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.example.classroom_reservation_system.member.entity.QMember.member;

@Repository
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public MemberRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Member> search(String loginId, String name, String email, Role role, LocalDate date, Pageable pageable) {
        // 데이터 조회 쿼리
        List<Member> content = queryFactory
                .selectFrom(member)
                .where(
                        likeLoginId(loginId),
                        likeName(name),
                        likeEmail(email),
                        eqRole(role),
                        eqDate(date)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(member.createdAt.desc())
                .fetch();

        // 전체 카운트 조회 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(member.count())
                .from(member)
                .where(
                        likeLoginId(loginId),
                        likeName(name),
                        likeEmail(email),
                        eqRole(role),
                        eqDate(date)
                );

        // 페이지 객체 변환해서 반환
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression likeLoginId(String loginId) {
        if (!StringUtils.hasText(loginId)) {
            return null;
        }

        return member.as(QStudent.class).studentId.containsIgnoreCase(loginId)
                .or(member.as(QProfessor.class).professorId.containsIgnoreCase(loginId))
                .or(member.as(QAdmin.class).adminId.containsIgnoreCase(loginId));
    }

    private BooleanExpression likeName(String name) {
        return StringUtils.hasText(name) ? member.name.containsIgnoreCase(name) : null;
    }

    private BooleanExpression likeEmail(String email) {
        return StringUtils.hasText(email) ? member.email.containsIgnoreCase(email) : null;
    }

    private BooleanExpression eqRole(Role role) {
        return role != null ? member.role.eq(role) : null;
    }

    private BooleanExpression eqDate(LocalDate date) {
        if (date == null) {
            return null;
        }

        return member.createdAt.between(date.atStartOfDay(), date.atTime(LocalTime.MAX));
    }
}
