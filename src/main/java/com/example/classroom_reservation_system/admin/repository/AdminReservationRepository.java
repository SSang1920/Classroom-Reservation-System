package com.example.classroom_reservation_system.admin.repository;

import com.example.classroom_reservation_system.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminReservationRepository extends JpaRepository<Reservation, Long> , AdminReservationRepositoryCustom {

}
