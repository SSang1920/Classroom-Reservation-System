package com.example.classroom_reservation_system.repository.member;

import com.example.classroom_reservation_system.entity.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {

    Optional<Professor> findByProfessorId(String professorId);

    boolean existsByProfessorId(String professorId);


}
