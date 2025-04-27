package com.example.classroom_reservation_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.cglib.core.Local;

import javax.management.Notification;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "Reservation")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Reservation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long reservationid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Long member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroomd_id", nullable = false)
    private Long classroom;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime starttime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endtime;

    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_state, nullable = false")
    private ReservationState reservationState;

    @Column(name = "create_at", nullable = false , updatable = false)
    private LocalDateTime createat;

    @OneToMany(mappedBy = "reservation",fetch = FetchType.LAZY)
    private List<History> histories = new ArrayList<>();

    @OneToMany(mappedBy = "reservation",fetch = FetchType.LAZY)
    private List<Notification> notifications = new ArrayList<>();
}
