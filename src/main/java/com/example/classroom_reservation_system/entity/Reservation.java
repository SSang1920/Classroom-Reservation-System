package com.example.classroom_reservation_system.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Reservation")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

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
    @Column(name = "reservation_state", nullable = false)
    private ReservationState reservationState;

    @CreatedDate
    @Column(name = "created_at", nullable = false , updatable = false)
    private LocalDateTime createdAt;

    @Builder.Default
    @OneToMany(mappedBy = "reservation", fetch = FetchType.LAZY)
    private List<History> histories = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "reservation", fetch = FetchType.LAZY)
    private List<Notification> notifications = new ArrayList<>();



    public void addHistory(History history){
        histories.add(history);
        history.setReservation(this);
    }

    public void removeHistory(History history){
        histories.remove(history);
        history.setReservation(null);
    }

    public void addNotification(Notification notification){
        notifications.add(notification);
        notification.setReservation(this);
    }

    public void removeNotification(Notification notification){
        notifications.remove(notification);
        notification.setReservation(null);
    }
}
