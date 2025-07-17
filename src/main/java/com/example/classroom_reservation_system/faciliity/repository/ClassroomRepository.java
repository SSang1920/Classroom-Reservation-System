package com.example.classroom_reservation_system.faciliity.repository;

import com.example.classroom_reservation_system.faciliity.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    Optional<Classroom> findByName(String name);
}
