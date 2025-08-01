package com.example.classroom_reservation_system.notification.repository;

import com.example.classroom_reservation_system.member.entity.Member;
import com.example.classroom_reservation_system.notification.entity.Notification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository  extends JpaRepository<Notification, Long> {

    /**
     * 사용자가 알림 조회할 때 연관된 Member 함께 조회
     */
    @EntityGraph(attributePaths = {"member"})
    @Override
    @NonNull
    Optional<Notification> findById(@NonNull Long id);

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

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP WHERE n.member = :member And n.isRead = false")
    void markAllReadByMember(@Param("member") Member member);
}
