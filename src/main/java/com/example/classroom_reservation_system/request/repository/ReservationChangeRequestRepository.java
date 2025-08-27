package com.example.classroom_reservation_system.request.repository;

import com.example.classroom_reservation_system.request.entity.RequestStatus;
import com.example.classroom_reservation_system.request.entity.ReservationChangeRequest;
import com.example.classroom_reservation_system.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ReservationChangeRequestRepository extends JpaRepository<ReservationChangeRequest, Long> {

    @Query("SELECT rcr FROM ReservationChangeRequest rcr " +
            "JOIN FETCH rcr.reservation r " +
            "JOIN FETCH r.member m " +
            "JOIN FETCH r.classroom c "+
            "ORDER BY rcr.createdAt DESC")
    List<ReservationChangeRequest> findAllWithDetails();

    boolean existsByReservationIdAndStatus(Long reservationId, RequestStatus requestStatus);

    @Query("SELECT rcr.reservation.id FROM ReservationChangeRequest rcr WHERE rcr.reservation.id IN :reservationIds AND rcr.status = 'PENDING'")
    Set<Long> findReservationIdsWithPendingRequests(@Param("reservationIds") List<Long> reservationIds);

    long countByStatus(RequestStatus requestStatus);

    Optional<ReservationChangeRequest> findByReservationAndStatus(Reservation reservation, RequestStatus status);
}
