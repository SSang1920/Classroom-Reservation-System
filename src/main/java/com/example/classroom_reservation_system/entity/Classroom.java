package com.example.classroom_reservation_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Classroom")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "classroom_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id",nullable = false)
    private Building building;

    @Column(name = "classroom_name", length = 20, nullable = false)
    private String classroomName;

    @Column(nullable = false)
    private int capacity;

    @Enumerated(EnumType.STRING)
    @Column(name = "classroom_state", nullable = false)
    private ClassroomState classroomState;

    @Builder.Default
    @OneToMany(mappedBy = "classroom", fetch = FetchType.LAZY)
    private List<Reservation> reservations  = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "classroom", fetch = FetchType.LAZY)
    private List<History> histories = new ArrayList<>();


    public void addReservation(Reservation reservation){
        reservations.add(reservation);
        reservation.setClassroom(this);
    }

    public void removeReservation(Reservation reservation){
        reservations.remove(reservation);
        reservation.setClassroom(null);
    }

    public void addHistory(History history){
        histories.add(history);
        history.setClassroom(this);
    }

    public void removeClassroom(History history){
        histories.remove(history);
        history.setClassroom(null);
    }
}
