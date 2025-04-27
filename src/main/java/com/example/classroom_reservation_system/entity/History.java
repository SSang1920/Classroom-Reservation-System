package com.example.classroom_reservation_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "History")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class History {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="history_id")
    private Long historyid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name  ="Reservation_id", nullable = false)
    private Long reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Long member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id", nullable = false)
    private Long classroom;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime starttime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endtime;

    @Enumerated(EnumType.STRING)
    @Column(name = "history_state, nullable = false")
    private Historystate historystate;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createat;

    @Column(name = "update_at", nullable = false)
    private LocalDateTime updateat;

    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedat;
}
