package com.example.classroom_reservation_system.reservation.repository;

import com.example.classroom_reservation_system.faciliity.entity.Classroom;
import com.example.classroom_reservation_system.member.entity.Member;
import com.example.classroom_reservation_system.reservation.entity.Reservation;
import com.example.classroom_reservation_system.reservation.entity.ReservationState;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)  //조회한 데이터에 쓰기 기능 잠금
    boolean existsByClassroomAndReservationStateNotAndEndTimeAfterAndStartTimeBefore(
            Classroom classroom,
            ReservationState reservationState,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    /**
     * 스케쥴러가 자동으로 완료처리할 예약 조회
     * @param reservationState 조회할 예약 상태
     * @param now 현재 시간
     * @return 조건에 맞는 예약 목록
     */
    List<Reservation> findAllByReservationStateAndEndTimeBefore(
            ReservationState reservationState,
            LocalDateTime now
            );

    // View를 위한 내 예약 목록 조회
    List<Reservation> findAllByMemberOrderByStartTimeDesc(Member member);
}
