package com.example.classroom_reservation_system.faciliity.repository;

import com.example.classroom_reservation_system.faciliity.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BuildingRepository extends JpaRepository<Building, Long> {

    Optional<Building> findByName(String name);

    List<Building> findAllByOrderByNameAsc();

    boolean existsByName(String name);
}
