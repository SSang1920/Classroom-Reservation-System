package com.example.classroom_reservation_system.notification.repository;

import com.example.classroom_reservation_system.member.entity.Member;
import com.example.classroom_reservation_system.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository  extends JpaRepository<Notification, Long> {

    /**
     * 특정 회원의 모든 알림을 최신순으로 조회
     * @param member 알림을 조회할 회원
     * @return 해당 회원의 알림 목록
     */
    List<Notification> findAllByMemberOrderByCreatedAtDesc(Member member);

    /**
     * 특정 회원의 읽지 않은 알림 수를 조회합니다.
     * @param member 알림 수를 조회할 회원
     * @return 해당 회원의 읽지 않은 알림 개수
     */
    long countByMemberAndIsReadIsFalse(Member member);
}
