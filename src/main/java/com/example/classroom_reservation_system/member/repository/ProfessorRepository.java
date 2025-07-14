package com.example.classroom_reservation_system.member.repository;

import com.example.classroom_reservation_system.member.entity.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {

    Optional<Professor> findByProfessorId(String professorId);

    boolean existsByProfessorId(String professorId);


}
