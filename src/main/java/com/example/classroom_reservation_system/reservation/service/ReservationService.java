package com.example.classroom_reservation_system.reservation.service;

import com.example.classroom_reservation_system.common.exception.CustomException;
import com.example.classroom_reservation_system.common.exception.ErrorCode;
import com.example.classroom_reservation_system.faciliity.entity.Classroom;
import com.example.classroom_reservation_system.faciliity.repository.ClassroomRepository;
import com.example.classroom_reservation_system.member.entity.Member;
import com.example.classroom_reservation_system.member.service.MemberService;
import com.example.classroom_reservation_system.reservation.dto.AvailableTimeDto;
import com.example.classroom_reservation_system.reservation.dto.request.ReservationRequest;
import com.example.classroom_reservation_system.reservation.dto.response.ReservationResponse;
import com.example.classroom_reservation_system.reservation.entity.Reservation;
import com.example.classroom_reservation_system.reservation.entity.ReservationState;
import com.example.classroom_reservation_system.reservation.entity.TimePeriod;
import com.example.classroom_reservation_system.reservation.event.ReservationStatusChangedEvent;
import com.example.classroom_reservation_system.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    // 클래스 레벨에 @Transactional(readOnly = true)를 선언하면,
    // 데이터 변경이 없는 조회 메서드의 성능을 최적화할 수 있습니다.
    // 데이터 변경이 필요한 메서드에는 별도로 @Transactional을 붙여 쓰기 가능하도록 오버라이드합니다.

    private final ReservationRepository reservationRepository;
    private final MemberService memberService; //회원 정보를 가져옴
    private final ClassroomRepository classroomRepository; // 강의실 정보 가져옴
    private final ApplicationEventPublisher eventPublisher; // 이벤트 발행기

    /**
     * 예약 생성
     * @param memberUuid 예약 요청한 회원의 uuid
     * @param request 예약에 필요한 정보(강의실 ID, 시작 종료 시간)
     * @return 생성된 예약의 고유 ID
     */
    @Transactional
    public Long createReservation(String memberUuid, ReservationRequest request){
        // 1.회원 & 강의실 엔티티 조회
        Member member = memberService.findByMemberUuid(memberUuid);
        Classroom classroom = classroomRepository.findById(request.getClassroomId())
                .orElseThrow(() -> new CustomException(ErrorCode.CLASSROOM_NOT_FOUND));

        // 2. 예약 목록을 시간순으로 정렬
        List<TimePeriod> periods = request.getPeriods();
        periods.sort(Comparator.naturalOrder()); // ENUM 정의순으로 정렬

        // 3.DB에 저장될 시작& 종료 시간 계산
        LocalDate date = request.getReservationDate();
        LocalDateTime finalStartTime = date.atTime(periods.get(0).getStartTime());
        LocalDateTime finalEndTime = date.atTime(periods.get(periods.size() - 1).getEndTime());


        // 4. 예약 중복 확인 (Pessimistic Lock)
        boolean isOverlapping = reservationRepository.existsByClassroomAndReservationStateNotAndEndTimeAfterAndStartTimeBefore(
                classroom,
                ReservationState.CANCELED,
                finalStartTime,
                finalEndTime
        );

        // 5. 중복 시 예외 발생
        if (isOverlapping){
            throw new CustomException(ErrorCode.CLASSROOM_ALREADY_RESERVED);
        }

        // 6. 예약 생성
        Reservation reservation = Reservation.create(
                member,
                classroom,
                finalStartTime,
                finalEndTime,
                EnumSet.copyOf(periods) //List 를 EnumSet으로 변환하여 전달
        );

        // 7. 예약 저장
        Reservation savedReservation = reservationRepository.save(reservation);

        // 8. 예약 성공 이벤트 발행
        String message = String.format("'%s' 예약이 완료되었습니다.", savedReservation.getClassroom().getName());
        eventPublisher.publishEvent(new ReservationStatusChangedEvent(this, savedReservation, message));

        return savedReservation.getId();
    }

    /**
     * 사용자가 예약 취소
     * @param reservationId 취소할 예약 ID
     * @param memberUuid 요청한 회원 UUID
     */
    @Transactional
    public void cancelReservation(Long reservationId, String memberUuid){
        Reservation reservation = findAndValidateOwner(reservationId, memberUuid);
        reservation.cancel();

        // 예약 취소 이벤트 발행
        String message = String.format("'%s' 예약이 취소되었습니다.", reservation.getClassroom().getName());
        eventPublisher.publishEvent(new ReservationStatusChangedEvent(this, reservation, message));
    }

    /**
     * 사용자가 사용 완료를 누름
     * @param reservationId 완료할 예약 ID
     * @param memberUuid 요청한 회원의 UUID
     */
    @Transactional
    public void completeReservation(Long reservationId, String memberUuid){
        Reservation reservation = findAndValidateOwner(reservationId, memberUuid);
        reservation.complete();
    }

    /**
     * 예약 조회, 현재 사용자가 예약을 한사람인지 검증
     * @param reservationId 예약 ID
     * @param memberUuid 현재 사용자의 UUID
     * @return 검증된 reservation 엔티티
     */
    private Reservation findAndValidateOwner(Long reservationId, String memberUuid){
        Member member = memberService.findByMemberUuid(memberUuid);
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));


        if (!reservation.getMember().equals(member)) {
            throw new CustomException(ErrorCode.RESERVATION_ACCESS_DENIED);
        }

        return reservation;
    }
    /**
     * 특정 강의실 특정 날짜에 대한 예약 목록 조회
     */
    public Set<TimePeriod> getReservedPeriodsForDate(Long classroomId, LocalDate date, Long excludeReservationId){

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Reservation> reservationsOnDate;


        if(excludeReservationId != null){
            //특정 예약 제외하고 조회
            reservationsOnDate = reservationRepository
                    .findByClassroom_IdAndIdNotAndReservationStateNotAndStartTimeBetween(
                            classroomId,
                            excludeReservationId,
                            ReservationState.CANCELED,
                            startOfDay,
                            endOfDay
                    );

        } else{
            // Repository를 통해 해당 날짜의 모든 예약 조회
            reservationsOnDate = reservationRepository
                    .findByClassroom_IdAndReservationStateNotAndStartTimeBetween(
                            classroomId,
                            ReservationState.CANCELED,
                            startOfDay,
                            endOfDay
                    );

        }

         //예약과 겹치는 교시를 찾아 set으로 모아줌
        return reservationsOnDate.stream()
                .flatMap(reservation -> reservation.getPeriods().stream())
                .collect(Collectors.toSet());

    }

    /**
     * 특정 날짜 예약 가능한 시간 목록 조회
     */
    public List<AvailableTimeDto> getAvailableTimes(Long classroomId, LocalDate date){
        Set<TimePeriod> reservedPeriods = getReservedPeriodsForDate(classroomId, date, null);

        return Arrays.stream(TimePeriod.values())
                .filter(period -> !reservedPeriods.contains(period))
                .map(AvailableTimeDto::from)
                .collect(Collectors.toList());
    }

    /**
     * API/View를 위한 내 예약 목록 조회 (DTO 변환)
     * @param memberUuid 현재 사용자 UUID
     * @return 예약 목록 (DTO)
     */
    public List<ReservationResponse> getMyReservationsApi(String memberUuid) {
        Member member = memberService.findByMemberUuid(memberUuid);
        List<Reservation> reservations = reservationRepository.findAllByMemberOrderByStartTimeDesc(member);
        return reservations.stream()
                .map(ReservationResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 스케줄러에 의해 호출될 자동 예약 완료 처리 메서드
     */
    @Transactional
    public void autoCompleteReservations() {
        // 종료 시간이 지났지만, 아직 '예약됨' 상태인 모든 예약을 조회
        List<Reservation> targets = reservationRepository.findAllByReservationStateAndEndTimeBefore(
                ReservationState.RESERVED,
                java.time.LocalDateTime.now()
        );

        // 각 예약을 사용 완료 처리
        targets.forEach(Reservation::autoComplete);
    }

}
