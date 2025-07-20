package com.example.classroom_reservation_system.reservation.entity;

import com.example.classroom_reservation_system.faciliity.entity.Classroom;
import com.example.classroom_reservation_system.history.entity.History;
import com.example.classroom_reservation_system.history.entity.HistoryState;
import com.example.classroom_reservation_system.member.entity.Member;
import com.example.classroom_reservation_system.notification.entity.Notification;
import com.example.classroom_reservation_system.common.exception.CustomException;
import com.example.classroom_reservation_system.common.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "reservation", indexes = {
        // 특정 강의실의 특정 시간대 예약 조회 성능 최적화
        @Index(name = "idx_reservation_classroom_time", columnList = "classroom_id, start_time, end_time"),
        // 사용자별 예약 조회 성능 최적화
        @Index(name = "idx_reservation_member", columnList = "member_id")
})
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE Reservation SET deleted_at = NOW() WHERE reservation_id = ?")  // 논리적 삭제 추가
@SQLRestriction("deleted_at IS NULL")   // 모든 조회 쿼리에 조건을 추가하여, 논리적으로 삭제된 데이터는 조회하지 않도록 함
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"member", "classroom", "histories", "notifications"})  // 모든 연관 필드 제외
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

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<History> histories = new ArrayList<>();

    @OneToMany(mappedBy = "reservation")
    private List<Notification> notifications = new ArrayList<>();

    @ElementCollection(targetClass = TimePeriod.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "reservation_periods", joinColumns = @JoinColumn(name = "reservation_id"))
    @Enumerated(EnumType.STRING)
    @Column(name ="period", nullable = false)
    private Set<TimePeriod> periods = EnumSet.noneOf(TimePeriod.class);


    /**
     * 예약 생성자
     */
    @Builder
    private Reservation(Member member, Classroom classroom, LocalDateTime startTime, LocalDateTime endTime, ReservationState reservationState, Set<TimePeriod> periods) {
        this.member = member;
        this.classroom = classroom;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reservationState = reservationState;
        this.periods = (periods !=null) ? periods : EnumSet.noneOf(TimePeriod.class);

    }

    /**
     * 비즈니스 로직: 예약 생성 메서드
     * 예약을 생성하고, 관련된 초기 히스토리와 알림 함께 생성
     * @return 생성된 Reservation 객체
     */
    public static Reservation create(Member member, Classroom classroom, LocalDateTime startTime, LocalDateTime endTime, Set<TimePeriod> periods) {
        Reservation reservation = Reservation.builder()
                .member(member)
                .classroom(classroom)
                .startTime(startTime)
                .endTime(endTime)
                .reservationState(ReservationState.RESERVED)
                .periods(periods)
                .build();

        // 연관관계 설정
        classroom.addReservation(reservation);
        reservation.addHistory(HistoryState.RESERVED);

        return reservation;
    }

    /**
     * 비즈니스 로직: 예약 상태 변경과 기록 처리
     */
    public void cancel() {
        // 예약이 취소 불가능한 상태인지 검사
        if (this.reservationState == ReservationState.CANCELED || this.reservationState == ReservationState.COMPLETED) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_CANCELLABLE);
        }

        // 예약 상태를 'CANCELED' 변경
        this.reservationState = ReservationState.CANCELED;

        // 히스토리 기록 추가
        this.addHistory(HistoryState.CANCELED);

    }

    public void cancelByAdmin(){
        // 예약이 취소 불가능한 상태인지 검사
        if (this.reservationState == ReservationState.CANCELED || this.reservationState == ReservationState.COMPLETED) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_CANCELLABLE);
        }

        // 예약 상태를 'CANCELED' 변경
        this.reservationState = ReservationState.CANCELED;

        // 히스토리 기록 추가
        this.addHistory(HistoryState.CANCELED_BY_ADMIN);

    }

    public void complete() {
        if (this.reservationState == ReservationState.COMPLETED) {
            throw new CustomException(ErrorCode.RESERVATION_ALREADY_COMPLETED);
        }

        if (this.reservationState == ReservationState.CANCELED) {
            throw new CustomException(ErrorCode.RESERVATION_ALREADY_CANCELED);
        }

        this.reservationState = ReservationState.COMPLETED;
        this.addHistory(HistoryState.COMPLETED);
    }

    public void autoComplete() {
        //이미 처리된 예약은 건너띔
        if (this.reservationState == ReservationState.COMPLETED || this.reservationState == ReservationState.CANCELED) {
           return;
        }

        //종료 시간이 되지 않은 예약은 건너뜀
        if (this.endTime.isAfter(LocalDateTime.now())){
            return;
        }

        this.reservationState = ReservationState.COMPLETED;
        this.addHistory(HistoryState.COMPLETED_BY_SYSTEM);
    }


    /**
     * 연관관계 편의 메서드 - 예약이력 추가 (내부용)
     */
    private void addHistory(HistoryState state){
        History history = History.builder()
                .member(this.member)
                .classroom(this.classroom)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .historyState(state)
                .build();

        // Reservation -> History 양방향 관계 설정
        this.histories.add(history);
        history.linkToReservation(this);
    }


}
