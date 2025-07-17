package com.example.classroom_reservation_system.reservation.scheduler;

import com.example.classroom_reservation_system.reservation.entity.Reservation;
import com.example.classroom_reservation_system.reservation.entity.ReservationState;
import com.example.classroom_reservation_system.reservation.repository.ReservationRepository;
import com.example.classroom_reservation_system.reservation.service.ReservationService;
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

    private final ReservationService reservationService;

    /**
     * 매일 새벽 4시에 실행되어, 종료 시간이 지난 예약을 자동으로 'COMPLETED' 상태로 변경
     * cron = "0 0 4 * * *"
     */
    @Scheduled(cron = "0 0 4 * * *")
    public void runAutoCompleteReservations() {
        log.info("예약 자동 완료 스케쥴러 실행 시간 : {}", LocalDateTime.now());
        reservationService.autoCompleteReservations();
        log.info("예약 자동 완료 스케쥴러를 종료합니다.");
    }
}
