package com.example.classroom_reservation_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Building")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Building {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "building_id")
    private Long buildingid;

    @Column(length = 20, nullable = false)
    private String buildingname;

    @OneToMany(mappedBy = "building" ,fetch = FetchType.LAZY)
    private List<Classroom> classrooms = new ArrayList<>();
}
