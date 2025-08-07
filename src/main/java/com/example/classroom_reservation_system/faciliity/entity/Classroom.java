package com.example.classroom_reservation_system.faciliity.entity;

import com.example.classroom_reservation_system.common.exception.CustomException;
import com.example.classroom_reservation_system.common.exception.ErrorCode;
import com.example.classroom_reservation_system.reservation.entity.Reservation;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "classroom", indexes = {
        @Index(name = "idx_classroom_building_id", columnList = "building_id"),
        @Index(name = "idx_classroom_state", columnList = "classroom_state")
})
@SQLDelete(sql = "UPDATE classroom SET deleted_at = NOW() WHERE classroom_id = ?")  // 논리적 삭제
@SQLRestriction("deleted_at IS NULL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString(exclude = {"building", "reservations"})
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "classroom_id")
    private Long id;

    @Column(name = "classroom_name", length = 20, nullable = false)
    private String name;

    @Column(nullable = false)
    private int capacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "classroom_state", nullable = false)
    private ClassroomState state = ClassroomState.AVAILABLE;

    @Lob
    private String equipmentInfo;       // 비품 정보

    @Lob
    private String unavailableReason;    // 사용 불가 사유

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder.Default
    @OneToMany(mappedBy = "classroom", orphanRemoval = true)
    private List<Reservation> reservations  = new ArrayList<>();

    /**
     * 비즈니스 로직: 강의실 상태 변경 메서드 (관리자용 기능)
     */
    public void markAsUnavailable(String reason) {
        // 이미 진행 중인 예약이 있는지 확인
        boolean hasActiveReservations = reservations.stream()
                .anyMatch(r -> r.getEndTime().isAfter(LocalDateTime.now()));

        if (hasActiveReservations) {
            throw new CustomException(ErrorCode.CLASSROOM_HAS_ACTIVE_RESERVATIONS);
        }

        this.state = ClassroomState.UNAVAILABLE;
        this.unavailableReason = reason;
    }

    /**
     * 비즈니스 로직: 강의실 이름 변경 메서드
     */
    public void updateDetails(String name, Integer capacity, String info) {
        this.name = name;
        this.capacity = capacity;
        this.equipmentInfo = info;
    }

    public void makeAvailable() {
        if (this.state == ClassroomState.AVAILABLE) {
            return;
        }

        this.state = ClassroomState.AVAILABLE;
        this.unavailableReason = null;        // 사유 초기화
    }

    /**
     * 연관관계 편의 메서드 - 예약 추가, 제거
     */
    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
    }

    public void removeReservation(Reservation reservation) {
        reservations.remove(reservation);
    }

    /**
     * 연관관계 편의 메서드 - Building과 연관관계 설정
     */
    public void linkToBuilding(Building building) {
        this.building = building;
    }
}
