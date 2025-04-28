package com.example.classroom_reservation_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Classroom")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Classroom {

    @Id @Column(name = "classroom_id")
    private Long classroomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id",nullable = false)
    private Building building;

    @Column(length = 20, nullable = false)
    private String classroomname;

    @Column(nullable = false)
    private int capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private State state;

    @OneToMany(mappedBy = "classroom", fetch = FetchType.LAZY)
    private List<Reservation> reservations  = new ArrayList<>();

    @OneToMany(mappedBy = "classroom",fetch =  FetchType.LAZY)
    private List<History> histories = new ArrayList<>();

}
