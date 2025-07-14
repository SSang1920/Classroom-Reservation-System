package com.example.classroom_reservation_system.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "student")
@DiscriminatorValue("STUDENT")
@PrimaryKeyJoinColumn(name = "member_id")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class Student extends Member {

    @Column(name = "student_id", length = 15, unique = true, nullable = false)
    private String studentId;

    @Override
    public String getId() {
        return studentId;
    }
}
