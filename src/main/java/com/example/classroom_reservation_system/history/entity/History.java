package com.example.classroom_reservation_system.history.entity;

import com.example.classroom_reservation_system.faciliity.entity.Classroom;
import com.example.classroom_reservation_system.reservation.entity.Reservation;
import com.example.classroom_reservation_system.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "history", indexes = {
        // 특정 예약에 대한 이력을 조회하는 경우
        @Index(name = "idx_history_reservation", columnList = "reservation_id"),
        // 특정 사용자의 예약 이력을 조회하는 경우
        @Index(name = "idx_history_member", columnList = "member_id"),
        // 특정 기간의 이력을 조회하는 경우
        @Index(name = "idx_history_created_at", columnList = "created_at")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString(exclude = {"reservation", "member", "classroom"})     // 모든 연관 필드 제외
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id", nullable = false)
    private Classroom classroom;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "history_state", nullable = false, updatable = false)
    private HistoryState historyState;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * 연관관계 편의 메서드 - Reservation과 연관관계 설정
     */
    public void linkToReservation(Reservation reservation) {
        this.reservation = reservation;
    }
}
