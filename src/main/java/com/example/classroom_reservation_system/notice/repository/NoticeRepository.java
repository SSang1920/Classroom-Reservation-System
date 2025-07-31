package com.example.classroom_reservation_system.notice.repository;

import com.example.classroom_reservation_system.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // 모든 공지사항을 최신순으로 조회
    List<Notice> findAllByOrderByCreatedAtDesc();
}
