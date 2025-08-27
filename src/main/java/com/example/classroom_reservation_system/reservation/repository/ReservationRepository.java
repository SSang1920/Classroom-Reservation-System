package com.example.classroom_reservation_system.reservation.repository;

import com.example.classroom_reservation_system.faciliity.entity.Classroom;
import com.example.classroom_reservation_system.member.entity.Member;
import com.example.classroom_reservation_system.reservation.entity.Reservation;
import com.example.classroom_reservation_system.reservation.entity.ReservationState;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)  //조회한 데이터 쓰기 기능 잠금
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

    /**
     * 특정 시간 사이에 시작하고, 특정 상태가 아닌 모든 예약을 조회.
     * @param reservationState 제외할 예약 상태 (CANCELED)
     * @param start 시작 시간
     * @param end 종료 시간
     * @return 조건에 맞는 예약 목록
     */
    List<Reservation> findAllByReservationStateNotAndStartTimeBetween(
            ReservationState reservationState,
            LocalDateTime start,
            LocalDateTime end
    );

    /**
     * 강의실에 예약목록조회
     * @param classroomId 조회할 강의실 Id
     * @param  reservationState 제외할 예약
     * @param start 조회 시작 시간
     * @param end 조회 종료 시간
     * @return 조건에 맞는 예약
     */
    List<Reservation> findByClassroom_IdAndReservationStateNotAndStartTimeBetween(
            Long classroomId,
            ReservationState reservationState,
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("SELECT r FROM Reservation r JOIN FETCH r.classroom WHERE r.id = :reservationId")
    Optional<Reservation> findByIdWithClassroom(@Param("reservationId") Long reservationId);

    boolean existsByClassroomAndIdNotAndReservationStateNotAndEndTimeAfterAndStartTimeBefore(
            Classroom classroom,
            Long id,
            ReservationState reservationState,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    /**
     * 예약 수정할때 다른 사람의 예약은 비활성화 처리 (현재 변경할 예약의 예약됨은 건드릴수잇게)
     */
    List<Reservation> findByClassroom_IdAndIdNotAndReservationStateNotAndStartTimeBetween(
            Long classroomId,
            Long id,
            ReservationState reservationState,
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("SELECT count(r) FROM Reservation r WHERE DATE(r.startTime) = :date")
    long countByDate(@Param("date") LocalDate date);
}
