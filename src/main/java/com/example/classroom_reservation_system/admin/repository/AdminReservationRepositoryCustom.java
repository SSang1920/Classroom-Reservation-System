package com.example.classroom_reservation_system.admin.repository;

import com.example.classroom_reservation_system.reservation.entity.Reservation;
import com.example.classroom_reservation_system.reservation.entity.ReservationState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface AdminReservationRepositoryCustom {
    Page<Reservation> search(
            String username,
            Long classroomId,
            LocalDate startDate,
            LocalDate endDate,
            ReservationState state,
            Pageable pageable
    );


}
