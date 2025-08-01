package com.example.classroom_reservation_system.notification.entity;

import com.example.classroom_reservation_system.member.entity.Member;
import com.example.classroom_reservation_system.reservation.entity.Reservation;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification", indexes = {
        @Index(name = "idx_notification_member_id", columnList = "member_id")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString(exclude = {"member", "reservation"})      // 모든 연관 필드 제외
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(nullable = false, length = 255)
    private String message;

    @Builder.Default
    @Column(name = "is_read")
    private boolean isRead = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    /**
     * 읽음 처리 메서드
     */
    public void markAsRead() {
        if (this.isRead) {
            return;
        }

        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    /**
     * 연관관계 편의 메서드 - Member와 연관관계 설정, 제거
     */
    public void assignToMember(Member member) {
        this.member = member;
    }

    public void unassignFromMember() {
        this.member = null;
    }

    /**
     * 연관관계 편의 메서드 - Reservation과 연관관계 설정
     */
    public void linkToReservation(Reservation reservation) {
        this.reservation = reservation;
    }
}
