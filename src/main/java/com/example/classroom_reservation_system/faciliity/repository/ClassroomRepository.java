package com.example.classroom_reservation_system.faciliity.repository;

import com.example.classroom_reservation_system.faciliity.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
}
