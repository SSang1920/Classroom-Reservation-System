package com.example.classroom_reservation_system.admin.repository;

import com.example.classroom_reservation_system.reservation.entity.Reservation;
import com.example.classroom_reservation_system.reservation.entity.ReservationState;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.example.classroom_reservation_system.faciliity.entity.QClassroom.classroom;
import static com.example.classroom_reservation_system.member.entity.QMember.member;
import static com.example.classroom_reservation_system.reservation.entity.QReservation.reservation;

@Repository
@RequiredArgsConstructor
public class AdminReservationRepositoryImpl implements AdminReservationRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Reservation> search(
            String username,
            Long classroomId,
            LocalDate startDate,
            LocalDate endDate,
            ReservationState state,
            Pageable pageable
    ) {
        // 데이터 조회 쿼리
        List<Reservation> content = queryFactory
                .selectFrom(reservation)
                .join(reservation.member, member).fetchJoin()
                .join(reservation.classroom, classroom).fetchJoin()
                .where(
                        usernameContains(username),
                        classroomIdEq(classroomId),
                        startDateGoe(startDate),
                        endDateLoe(endDate),
                        stateEq(state)
                )
                .orderBy(reservation.startTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 카운트 조회 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(reservation.count())
                .from(reservation)
                .join(reservation.member, member)
                .join(reservation.classroom, classroom)
                .where(
                        usernameContains(username),
                        classroomIdEq(classroomId),
                        startDateGoe(startDate),
                        endDateLoe(endDate),
                        stateEq(state)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression usernameContains(String username) {
        return StringUtils.hasText(username) ? member.name.containsIgnoreCase(username) : null;
    }

    private BooleanExpression classroomIdEq(Long classroomId){
        return classroomId != null ? reservation.classroom.id.eq(classroomId) : null;
    }

    private BooleanExpression startDateGoe(LocalDate startDate){
        return startDate != null ? reservation.startTime.goe(startDate.atStartOfDay()) : null;
    }

    private BooleanExpression endDateLoe(LocalDate endDate){
        return endDate != null ? reservation.startTime.loe(endDate.atTime(LocalTime.MAX)) : null;
    }

    private BooleanExpression stateEq(ReservationState state) {
        return state != null ? reservation.reservationState.eq(state) : null;
    }
}

