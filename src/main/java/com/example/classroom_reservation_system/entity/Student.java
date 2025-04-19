package com.example.classroom_reservation_system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "Student")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Student extends Member {

    @Column(name = "student_id", length = 10, unique = true)
    private String studentId;

    private int year;
}
