package com.example.classroom_reservation_system.admin.service;

import com.example.classroom_reservation_system.admin.dto.request.AdminReservationUpdateRequest;
import com.example.classroom_reservation_system.admin.dto.response.AdminReservationDetailResponse;
import com.example.classroom_reservation_system.admin.dto.response.AdminReservationResponse;
import com.example.classroom_reservation_system.admin.repository.AdminReservationRepository;
import com.example.classroom_reservation_system.common.exception.CustomException;
import com.example.classroom_reservation_system.common.exception.ErrorCode;
import com.example.classroom_reservation_system.faciliity.entity.Classroom;
import com.example.classroom_reservation_system.faciliity.repository.ClassroomRepository;
import com.example.classroom_reservation_system.request.entity.ReservationChangeRequest;
import com.example.classroom_reservation_system.reservation.entity.Reservation;
import com.example.classroom_reservation_system.reservation.entity.ReservationState;
import com.example.classroom_reservation_system.reservation.entity.TimePeriod;
import com.example.classroom_reservation_system.reservation.event.ReservationStatusChangedEvent;
import com.example.classroom_reservation_system.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminReservationService {
    private final ReservationRepository reservationRepository;
    private final AdminReservationRepository adminReservationRepository;
    private  final ClassroomRepository classroomRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 관리자용 예약 목록 검색
     * @param username
     * @param classroomId
     * @param startDate
     * @param endDate
     * @param state
     * @param pageable
     * @return DTO(페이징된 예약 목록)
     */
    public Page<AdminReservationResponse> getReservations(
            String username,
            Long classroomId,
            LocalDate startDate,
            LocalDate endDate,
            ReservationState state,
            Pageable pageable
    ) {
        // Querydsl을 사용하여 동적 조건으로 예약 검색
        Page<Reservation> reservations = adminReservationRepository.search(
                username, classroomId, startDate, endDate, state, pageable
        );
        // 조회된 엔티티 DTO로 변환
        return reservations.map(AdminReservationResponse::from);
    }
    /**
     * 단일 예약 상세 정보 조회
     */
    public AdminReservationDetailResponse getReservationDetails(Long reservationId){
        Reservation reservation = adminReservationRepository.findByIdWithHistories(reservationId)
                .orElseThrow(()-> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        return AdminReservationDetailResponse.from(reservation);
    }

    /**
     * 관리자에 의해 예약 취소
     * @param reservationId 취소할 예약 ID
     */
    @Transactional
    public void cancelReservationByAdmin(Long reservationId){
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        reservation.cancelByAdmin();

        String message = String.format("관리자에 의해 '%s' 예약이 취소되었습니다.", reservation.getClassroom().getName());
        eventPublisher.publishEvent(new ReservationStatusChangedEvent(this, reservation, message));
    }

    @Transactional
    public void updateReservationByAdmin(Long reservationId, AdminReservationUpdateRequest request){
        // 수정할 예약
        Reservation reservation = reservationRepository.findByIdWithClassroom(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        performReservationUpdate(
                reservation,
                request.getClassroomId(),
                request.getReservationDate(),
                EnumSet.copyOf(request.getPeriods())
        );

        reservation.updateByAdmin();
        // 예약 수정
        String message = String.format("관리자에 의해 '%s' 예약이 수정되었습니다. (수정된 시간: %s)",
                reservation.getClassroom().getName(), request.getReservationDate());
        eventPublisher.publishEvent(new ReservationStatusChangedEvent(this, reservation, message));

    }

    /**
     * 예약 변경 요청에 따라 예약 수정
     */
    @Transactional
    public void updateReservationByChangeRequest(Long reservationId, ReservationChangeRequest request){
        Reservation reservation = reservationRepository.findByIdWithClassroom(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        performReservationUpdate(
                reservation,
                request.getNewClassroomId(),
                request.getNewReservationDate(),
                request.getNewPeriods()
        );
    }

    /**
     * 예약 수정 처리 공통 로직
     */
    private void performReservationUpdate(Reservation reservation, Long newClassroomId, LocalDate newReservationDate, Set<TimePeriod> newPeriods){
        Classroom newClassroom = classroomRepository.findById(newClassroomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLASSROOM_NOT_FOUND));

        if(newPeriods == null || newPeriods.isEmpty()){
            throw new CustomException(ErrorCode.INVALID_RESERVATION_TIME);
        }

        List<TimePeriod> sortedPeriods = newPeriods.stream().sorted().collect(Collectors.toList());
        LocalDateTime newStartTime = newReservationDate.atTime(sortedPeriods.get(0).getStartTime());
        LocalDateTime newEndTime = newReservationDate.atTime(sortedPeriods.get(sortedPeriods.size() - 1).getEndTime());

        boolean isOverlapping = reservationRepository.existsByClassroomAndIdNotAndReservationStateNotAndEndTimeAfterAndStartTimeBefore(
                newClassroom,
                reservation.getId(),
                ReservationState.CANCELED,
                newStartTime,
                newEndTime
        );

        if(isOverlapping){
            throw new CustomException(ErrorCode.CLASSROOM_ALREADY_RESERVED);
        }

        reservation.updateDetails(
                newClassroom,
                newStartTime,
                newEndTime,
                newPeriods
        );
    }
}
