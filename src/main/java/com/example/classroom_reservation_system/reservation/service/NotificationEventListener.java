package com.example.classroom_reservation_system.reservation.service;


import com.example.classroom_reservation_system.notification.service.NotificationService;
import com.example.classroom_reservation_system.reservation.event.ReservationStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @EventListener
    public void handleReservationStatusChanged(ReservationStatusChangedEvent event){
        log.info("ReservationStatusChangedEvent 수신: 예약 ID {}", event.getReservation().getId());
        // 이벤트로부터 예약 정보와 메시지를 받아 알림을 생성하고 전송
        notificationService.send(event.getReservation(), event.getMessage());
    }
}
