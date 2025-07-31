package com.example.classroom_reservation_system.request.repository;

import com.example.classroom_reservation_system.request.entity.ReservationChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReservationChangeRequestRepository extends JpaRepository<ReservationChangeRequest, Long> {

    @Query("SELECT rcr FROM ReservationChangeRequest rcr " +
            "JOIN FETCH rcr.reservation r " +
            "JOIN FETCH r.member m " +
            "JOIN FETCH r.classroom c "+
            "ORDER BY rcr.createdAt DESC")
    List<ReservationChangeRequest> findAllWithDetails();

}
