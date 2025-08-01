package com.example.classroom_reservation_system.notification.service;

import com.example.classroom_reservation_system.common.exception.CustomException;
import com.example.classroom_reservation_system.common.exception.ErrorCode;
import com.example.classroom_reservation_system.member.entity.Admin;
import com.example.classroom_reservation_system.member.entity.Member;
import com.example.classroom_reservation_system.member.repository.AdminRepository;
import com.example.classroom_reservation_system.member.repository.MemberRepository;
import com.example.classroom_reservation_system.notification.dto.NotificationResponseDto;
import com.example.classroom_reservation_system.notification.entity.Notification;
import com.example.classroom_reservation_system.notification.repository.EmitterRepository;
import com.example.classroom_reservation_system.notification.repository.NotificationRepository;
import com.example.classroom_reservation_system.reservation.entity.Reservation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    // SSE연결 타임아웃 시간 (1시간)
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    // 의존성 주입
    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final EmitterRepository emitterRepository;
    private final AdminRepository adminRepository;

    /**
     * 현재 로그인한 회원 알림 목록 조회
     * @param memberUuid 현재 로그인한 사용자의 UUID
     * @return 해당 사용자의 알림 목록 (DTO)
     */
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getMyNotifications(String memberUuid){
        // 1. UUID로 회원 조회
        Member member = memberRepository.findByMemberUuid(memberUuid)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 2. 회원의 알림을 최신순으로 조회
        List<Notification> notifications = notificationRepository.findAllByMemberOrderByCreatedAtDesc(member);

        // 3. 조회된 알림 엔티티를 DTO로 변환하여 반환
        return notifications.stream()
                .map(NotificationResponseDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 알림 읽음 처리
     * @param notificationId 읽음 처리할 알림의 ID
     * @param memberUuid 현재 로그인한 사용자의 UUID
     */
    @Transactional
    public void readNotification(Long notificationId, String memberUuid){

        // 1. ID로 알림 엔티티 조회
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));

        // 2.알림이 현재 로그인한 회원의 것인지 확인
        if(!notification.getMember().getMemberUuid().equals(memberUuid)){
            throw new CustomException(ErrorCode.NOTIFICATION_ACCESS_DENIED);

        }

        // 3. 엔티티  내부의 읽음 처리 메소드 호출
        notification.markAsRead();
    }

    @Transactional
    public void readAllNotifications(String memberUuid) {
        Member member = memberRepository.findByMemberUuid(memberUuid)
                .orElseThrow(()-> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        notificationRepository.markAllReadByMember(member);
    }

    /**
     * 읽지 않은 알림 개수 조회
     * @param MemberUUid 현재 로그인한 사용자의 UUID
     * @retrun 읽지 않은 알림 개수
     */
    @Transactional(readOnly = true)
    public long getUnreadNotificationCount(String MemberUUid){
        Member member = memberRepository.findByMemberUuid(MemberUUid)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        return notificationRepository.countByMemberAndIsReadIsFalse(member);
    }


    /**
     * SSE 구독
     * @param memberUuid 구독을 요청한 회원의 UUID
     * @param lastEventId 클라이언트가 마지막으로 수신한 이벤트의 ID
     * @return SseEmitter 객체
     */
    public SseEmitter subscribe(String memberUuid, String lastEventId){
        Member member = memberRepository.findByMemberUuid(memberUuid)
                .orElseThrow(()->new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Long memberId = member.getMemberId();

        // 1. 각 클라이언트를 식별한 고유한 ID를 생성 (memberId + 현재 시간)
        String emitterId = memberId + "_" + System.currentTimeMillis();

        // 2. SseEmitter 생성 ,  타임아웃 시간 설정 후 저장
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        // 3. 연결이 종료되거나 타임아웃 되면 저장소에서 emitter 삭제
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        // 4. 첫 연결시, 503 에러 방지를 위한 더미 데이터 전송
        try {
            emitter.send(SseEmitter.event()
                    .id(emitterId)
                    .name("connect")
                    .data("SSE connection established"));
        } catch (IOException e) {
            emitterRepository.deleteById(emitterId);
            log.warn("SSE 연결 오류 발생: {}", emitterId, e);
        }

        // 5. 클라이언트 재연결시, 놓친 이벤트있으면 전송
        if(lastEventId != null && !lastEventId.isEmpty()){

            //memberId를 사용해 사용자의 이벤트 캐시 조회
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithByMemberId(String.valueOf(memberId));

            //lastEventId보다 최신 이벤트들을 필터링 하여 클라이언트에게 전송
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) <0 )
                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getKey(), entry.getValue()));
        }

        return emitter;
    }

    /**
     * [내부 호출용] 실제 알림을 생성하고 클라이언트에게 전송
     * 예약 생성/취소 등 다른 서비스에서 이벤트 리스너를 통해 호출
     */
    @Transactional
    public void send(Reservation reservation, String message){
        Notification notification = createNotification(reservation, message);
        String eventId = String.valueOf(reservation.getMember().getMemberId()) + "_" + System.currentTimeMillis();

        // 해당 사용자에게 연결된 모든 Emitter를 찾아 알림을 전송
        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByMemberId(String.valueOf(reservation.getMember().getMemberId()));

        // 재연결 시 유실되지 않도록 캐시에 저장
        emitterRepository.saveEventCache(eventId, NotificationResponseDto.from(notification));

        emitters.forEach(
                (emitterId, emitter) -> {
                    sendToClient(emitter, emitterId, eventId, NotificationResponseDto.from(notification));
                }
        );
    }

    /**
     * 관리자 그룹에게 알림 전송
     */
    @Transactional
    public void sendToAdmins(Reservation reservation, String message){
        List<Admin> admins = adminRepository.findAll();

        for(Member admin : admins){
            Notification notification = createNotificationForAdmin(admin, reservation, message);
            String eventId = String.valueOf(admin.getMemberId()) + "_" + System.currentTimeMillis();

            //관리자에게 연결된 모든 Emitter을 찾아 알림 전송
            Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByMemberId(String.valueOf(admin.getMemberId()));

            emitterRepository.saveEventCache(eventId, NotificationResponseDto.from(notification));

            emitters.forEach(
                    (emitterId, emitter) -> sendToClient(emitter, emitterId, eventId, NotificationResponseDto.from(notification))
            );
        }
    }

    // 알림 엔티티를 생성하고 DB에 저장하는 private 메서드
    private Notification createNotification(Reservation reservation, String message) {
        Notification notification = Notification.builder()
                .member(reservation.getMember())
                .reservation(reservation)
                .message(message)
                .isRead(false)
                .build();
        return notificationRepository.save(notification);
    }

    private Notification createNotificationForAdmin(Member admin, Reservation reservation, String message){
        Notification notification = Notification.builder()
                .member(admin)
                .reservation(reservation)
                .message(message)
                .isRead(false)
                .build();
        return notificationRepository.save(notification);
    }



    // 클라이언트에게 데이터를 전송하는 private 헬퍼 메서드
    private void sendToClient(SseEmitter emitter, String emitterId, String eventId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .name("notification") // 프론트엔드에서 이 이름으로 이벤트를 수신
                    .data(data));
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
            log.debug("SSE 연결 오류 발생: {}", emitterId, exception);
        }
    }

}
