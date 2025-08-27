package com.example.classroom_reservation_system.request.service;


import com.example.classroom_reservation_system.request.entity.RequestStatus;
import com.example.classroom_reservation_system.request.entity.ReservationChangeRequest;
import com.example.classroom_reservation_system.request.repository.ReservationChangeRequestRepository;
import com.example.classroom_reservation_system.reservation.entity.Reservation;
import com.example.classroom_reservation_system.reservation.entity.ReservationState;
import com.example.classroom_reservation_system.reservation.event.ReservationStatusChangedEvent;
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
public class ChangeRequestEventListener {

    private final ReservationChangeRequestRepository changeRequestRepository;


    @Async
    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleReservationCancellation(ReservationStatusChangedEvent event){
        Reservation reservation = event.getReservation();

        if(reservation.getReservationState() == ReservationState.CANCELED ||reservation.getReservationState() == ReservationState.CANCELED_BY_ADMIN){
            log.info("예약(ID: {}) 취소 감지. 연관된 PENDING 상태의 변경 요청을 확인합니다.", reservation.getId());
            changeRequestRepository.findByReservationAndStatus(reservation, RequestStatus.PENDING)
                    .ifPresent(ReservationChangeRequest::cancel);
        }
    }
}
