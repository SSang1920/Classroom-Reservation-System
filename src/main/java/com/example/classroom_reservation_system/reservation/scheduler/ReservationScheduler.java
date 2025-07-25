package com.example.classroom_reservation_system.reservation.scheduler;

import com.example.classroom_reservation_system.reservation.entity.Reservation;
import com.example.classroom_reservation_system.reservation.entity.ReservationState;
import com.example.classroom_reservation_system.reservation.event.ReservationStatusChangedEvent;
import com.example.classroom_reservation_system.reservation.repository.ReservationRepository;
import com.example.classroom_reservation_system.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ReservationRepository reservationRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 매일 새벽 4시에 종료 시간이 지난 예약을 자동으로 'COMPLETED' 상태로 변경
     * cron = "0 0 4 * * *"
     */
    @Scheduled(cron = "0 0 4 * * *")
    public void runAutoCompleteReservations() {
        log.info("예약 자동 완료 스케쥴러 실행 시간 : {}", LocalDateTime.now());
        reservationService.autoCompleteReservations();
        log.info("예약 자동 완료 스케쥴러를 종료합니다.");
    }

    /**
     * 30분마다 실행되어 1시간 뒤에 시작되는 예약에 대해 알림을 전송
     * cron = "0 0/30 * * * *" (0, 30분에 실행)
     */
    @Scheduled(cron = "0 0/30 * * * *")
    @Transactional(readOnly = true)
    public void notifyBeforeReservation() {
        // 1. 지금 시간부터 1시간 뒤의 시간을 계산
        LocalDateTime oneHourFromNow = LocalDateTime.now().plusHours(1);
        // 2.30분 간격으로 조회 (예: 14:00:00 ~ 14:00:59 사이)
        LocalDateTime oneHourAndThirtyMinutesFromNow = oneHourFromNow.plusMinutes(30);

        // 3. Repository 메서드를 호출하여 '취소되지 않은' 예약 중 알림 대상을 검색
        List<Reservation> upcomingReservations = reservationRepository.findAllByReservationStateNotAndStartTimeBetween(
                ReservationState.CANCELED,
                oneHourFromNow,
                oneHourAndThirtyMinutesFromNow
        );

        // 4. 찾은 예약들에 대해 '예약 시간 알림 이벤트'를 발행.
        for (Reservation reservation : upcomingReservations) {
            log.info("1시간 전 알림 발송 대상: 예약 ID {}", reservation.getId());
            String message = String.format("'%s' 예약 시작까지 1시간 남았습니다.", reservation.getClassroom().getName());
            eventPublisher.publishEvent(new ReservationStatusChangedEvent(this, reservation, message));
        }
    }
}
