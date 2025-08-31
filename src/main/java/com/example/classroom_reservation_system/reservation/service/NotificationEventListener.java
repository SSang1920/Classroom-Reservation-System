package com.example.classroom_reservation_system.reservation.service;


import com.example.classroom_reservation_system.common.exception.CustomException;
import com.example.classroom_reservation_system.common.exception.ErrorCode;
import com.example.classroom_reservation_system.history.entity.History;
import com.example.classroom_reservation_system.history.entity.HistoryState;
import com.example.classroom_reservation_system.notification.entity.NotificationType;
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

import java.util.Comparator;

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
        Reservation reservation = reservationRepository.findById(event.getReservation().getId())
                .orElseThrow(() -> {
                    log.error("알림 리스너 오류: ID {}에 해당하는 예약을 찾을 수 없습니다.", event.getReservation().getId());
                    return new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
                });

        HistoryState latestHistoryState = reservation.getHistories().stream()
                .max(Comparator.comparing(History::getCreatedAt))
                .map(History::getHistoryState)
                .orElseThrow(()-> new CustomException(ErrorCode.HISTORY_NOT_FOUND));

        NotificationType notificationType = NotificationType.RESERVATION_EVENT; // 기본값

        if(latestHistoryState == HistoryState.UPDATED_BY_REQUEST || event.getMessage().contains("거절되었습니다")){
            notificationType = NotificationType.CHANGE_REQUEST_PROCESSED;
        }

        notificationService.send(reservation, event.getMessage(), notificationType);
    }

    @Async
    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleReservationChangeRequested(ReservationChangeRequestedEvent event){
        log.info("ReservationChangedRequestedEvent 수신 : {}", event.getMessage());

        Reservation reservation = event.getReservation();

        notificationService.sendToAdmins(reservation, event.getMessage(), NotificationType.CHANGE_REQUEST_RECEIVED);
    }
}
