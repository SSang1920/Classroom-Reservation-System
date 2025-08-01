package com.example.classroom_reservation_system.reservation.service;


import com.example.classroom_reservation_system.common.exception.CustomException;
import com.example.classroom_reservation_system.common.exception.ErrorCode;
import com.example.classroom_reservation_system.notification.service.NotificationService;
import com.example.classroom_reservation_system.request.event.ReservationChangeRequestedEvent;
import com.example.classroom_reservation_system.reservation.entity.Reservation;
import com.example.classroom_reservation_system.reservation.event.ReservationStatusChangedEvent;
import com.example.classroom_reservation_system.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final ReservationRepository reservationRepository;

    @Async
    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleReservationStatusChanged(ReservationStatusChangedEvent event){
        log.info("ReservationStatusChangedEvent 수신: 예약 ID {}", event.getReservation().getId());
        // 이벤트로부터 예약 정보와 메시지를 받아 알림을 생성하고 전송

        Reservation managedReservation = reservationRepository.findById(event.getReservation().getId())
                        .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        notificationService.send(managedReservation, event.getMessage());
    }

    @Async
    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleReservationChangeRequested(ReservationChangeRequestedEvent event){
        log.info("ReservationChangedRequestedEvent 수신 : {}", event.getMessage());

        Reservation managedReservation = reservationRepository.findById(event.getReservation().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        notificationService.sendToAdmins(managedReservation, event.getMessage());
    }
}
