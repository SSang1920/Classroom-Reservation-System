package com.example.classroom_reservation_system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "Professor")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Professor extends Member {

    @Column(name = "professor_id", length = 15, unique = true)
    private String professorId;

    @Override
    public String getId() {
        return professorId;
    }
}
