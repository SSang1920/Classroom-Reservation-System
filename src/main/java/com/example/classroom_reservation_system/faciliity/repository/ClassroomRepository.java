package com.example.classroom_reservation_system.faciliity.repository;

import com.example.classroom_reservation_system.faciliity.entity.Building;
import com.example.classroom_reservation_system.faciliity.entity.Classroom;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    Optional<Classroom> findByName(String name);

    List<Classroom> findByBuildingOrderByNameAsc(Building building);

    List<Classroom> findByBuildingIdOrderByNameAsc(Long buildingId);

    boolean existsByBuildingAndName(Building building, @NotBlank(message = "강의실 이름은 필수입니다.") String name);
}
