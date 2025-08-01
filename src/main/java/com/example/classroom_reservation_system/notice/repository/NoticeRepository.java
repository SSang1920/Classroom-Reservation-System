package com.example.classroom_reservation_system.notice.repository;

import com.example.classroom_reservation_system.notice.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // 모든 공지사항을 최신순으로 조회
    @Override
    @EntityGraph(attributePaths = {"author"})
    Page<Notice> findAll(Pageable pageable);
}
