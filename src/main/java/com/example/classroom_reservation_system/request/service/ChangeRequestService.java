package com.example.classroom_reservation_system.request.service;

import com.example.classroom_reservation_system.admin.service.AdminReservationService;
import com.example.classroom_reservation_system.common.exception.CustomException;
import com.example.classroom_reservation_system.common.exception.ErrorCode;
import com.example.classroom_reservation_system.member.entity.Member;
import com.example.classroom_reservation_system.request.dto.AdminFeedbackDto;
import com.example.classroom_reservation_system.request.dto.StudentChangeRequestDto;
import com.example.classroom_reservation_system.request.dto.response.ReservationChangeRequestResponseDto;
import com.example.classroom_reservation_system.request.entity.RequestStatus;
import com.example.classroom_reservation_system.request.entity.ReservationChangeRequest;
import com.example.classroom_reservation_system.request.event.ReservationChangeRequestedEvent;
import com.example.classroom_reservation_system.request.repository.ReservationChangeRequestRepository;
import com.example.classroom_reservation_system.reservation.entity.Reservation;
import com.example.classroom_reservation_system.reservation.event.ReservationStatusChangedEvent;
import com.example.classroom_reservation_system.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChangeRequestService {

    private final ReservationRepository reservationRepository;
    private final ReservationChangeRequestRepository requestRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final AdminReservationService adminReservationService;

    public void createChangeRequest(Long reservationId, StudentChangeRequestDto dto, Member student){
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        if(!reservation.getMember().getMemberId().equals(student.getMemberId())){
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        // [추가] 디버깅을 위한 null 체크
        if (reservation.getEndTime() == null) {
            // 이 로그가 찍힌다면 Reservation 데이터 자체에 문제가 있는 것입니다.
            log.error("심각한 오류: Reservation(ID: {})의 endTime이 null입니다!", reservation.getId());
            // 더 명확한 예외를 발생시켜 문제를 즉시 인지하게 합니다.
            throw new IllegalStateException("원본 예약의 종료 시간이 null이므로 변경 요청을 생성할 수 없습니다.");
        }

        ReservationChangeRequest request = ReservationChangeRequest.builder()
                .reservation(reservation)
                .status(RequestStatus.PENDING)
                .originalClassroomName(reservation.getClassroom().getName())
                .originalStartTime(reservation.getStartTime())
                .originalEndTime(reservation.getEndTime())
                .newClassroomId(dto.getNewClassroomId())
                .newReservationDate(dto.getNewReservationDate())
                .newPeriods(dto.getNewPeriods())
                .requestMessage(dto.getRequestMessage())
                .build();

        requestRepository.save(request);

        String message = String.format("학생 '%s'님이 예약(ID:%d) 변경을 요청했습니다.",student.getName(),reservationId);
        eventPublisher.publishEvent(new ReservationChangeRequestedEvent(this, reservation, message));
    }

    public void processChangeRequest(Long requestId, AdminFeedbackDto dto){
        ReservationChangeRequest request = requestRepository.findById(requestId)
                .orElseThrow(()-> new CustomException(ErrorCode.REQUEST_NOT_FOUND));

        if (request.getStatus() != RequestStatus.PENDING){
            throw new CustomException(ErrorCode.ALREADY_PROCESSED_REQUEST);
        }

        Reservation originalReservation = request.getReservation();
        String message;

        if(dto.isApprove()){
            request.approve(dto.getResponseMessage());

            adminReservationService.updateReservationByChangeRequest(originalReservation.getId(), request);

            message = String.format("요청하신 예약(ID:%d) 변경이 승인되었습니다.",originalReservation.getId());

        } else{
            request.reject(dto.getResponseMessage());

             message = String.format("요청하신 예약(ID:%d) 변경이 거절되었습니다. 사유: %s",originalReservation.getId(), dto.getResponseMessage());
        }

        eventPublisher.publishEvent(new ReservationStatusChangedEvent(this, originalReservation, message));
    }

    @Transactional(readOnly = true)
    public List<ReservationChangeRequestResponseDto> getAllChangeRequests(){
        return requestRepository.findAllWithDetails().stream()
                .map(ReservationChangeRequestResponseDto::from)
                .collect(Collectors.toList());
    }
}
