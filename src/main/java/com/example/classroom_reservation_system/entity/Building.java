package com.example.classroom_reservation_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Building")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "building_id")
    private Long id;

    @Column(name = "building_name", length = 20, nullable = false)
    private String buildingName;

    @Builder.Default
    @OneToMany(mappedBy = "building" ,fetch = FetchType.LAZY)
    private List<Classroom> classrooms = new ArrayList<>();

    public void addClassroom(Classroom classroom){
        classrooms.add(classroom);
    }

    public void removeClassroom(Classroom classroom){
        classrooms.remove(classroom);
    }
}
