package com.example.classroom_reservation_system.member.repository;

import com.example.classroom_reservation_system.member.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByAdminId(String adminId);

    boolean existsByAdminId(String adminId);

}
