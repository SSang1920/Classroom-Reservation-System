package com.example.classroom_reservation_system.reservation.scheduler;

import com.example.classroom_reservation_system.reservation.entity.Reservation;
import com.example.classroom_reservation_system.reservation.entity.ReservationState;
import com.example.classroom_reservation_system.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationScheduler {

    private final ReservationRepository reservationRepository;

    /**
     * 매 정각마다 실행되어, 종료 시간이 지난 예약을 자동으로 'COMPLETED' 상태로 변경
     * cron = "0 0 * * * *" 매시간 0분 0초에 실행
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void autoCompleteReservations() {
        log.info("예약 자동 완료 스케쥴러 실행: {}", LocalDateTime.now());

        // 1. 현재 시간 이전에 종료되었어야 하는 'RESERVED' 상태의 모든 예약 조회
        List<Reservation> expiredReservations = reservationRepository.findAllByReservationStateAndEndTimeBefore(
                ReservationState.RESERVED,
                LocalDateTime.now()
        );

        // 2. 조회된 예약들의 상태를 자동 완료 처리 (엔티티의 autoComplete 메소드 호출
        expiredReservations.forEach(Reservation::autoComplete);

        log.info("총 {}건의 예약을 자동으로 완료 처리했습니다.", expiredReservations.size());
    }
}
