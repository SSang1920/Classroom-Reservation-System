package com.example.classroom_reservation_system.request.entity;

import com.example.classroom_reservation_system.reservation.entity.Reservation;
import com.example.classroom_reservation_system.reservation.entity.TimePeriod;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Slf4j
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ReservationChangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    @Column(nullable = false)
    private String originalClassroomName;

    @Column(nullable = false)
    private LocalDateTime originalStartTime;

    @Column(nullable = false)
    private LocalDateTime originalEndTime;

    private Long newClassroomId;
    private LocalDate newReservationDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "change_request_periods", joinColumns = @JoinColumn(name = "request_id"))
    @Enumerated(EnumType.STRING)
    private Set<TimePeriod> newPeriods;

    @Column(length = 500)
    private String requestMessage;

    @Column(length = 500)
    private String responseMessage;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime processedAt;


    public void approve(String message){
        this.status = RequestStatus.APPROVED;
        this.responseMessage = message;
        this.processedAt = LocalDateTime.now();
    }

    public void reject(String message){
        this.status = RequestStatus.REJECTED;
        this.responseMessage = message;
        this.processedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (this.status != RequestStatus.PENDING) {
            log.warn("이미 처리된 변경 요청(ID: {})은 자동 취소할 수 없습니다. 현재 상태 : {}", this.getId(), this.status);
            return;
        }
        log.info("연관된 예약 취소로 인해 변경 요청(ID: {}) 상태를 PENDING에서 CANCELED로 변경합니다.", this.getId());
        this.status = RequestStatus.CANCELED;
        this.responseMessage = "연관된 예약이 취소되어 해당 요청이 자동으로 취소되었습니다.";
        this.processedAt = LocalDateTime.now();
    }
}
