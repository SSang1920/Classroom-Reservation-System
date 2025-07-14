package com.example.classroom_reservation_system.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "professor")
@DiscriminatorValue("PROFESSOR")
@PrimaryKeyJoinColumn(name = "member_id")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class Professor extends Member {

    @Column(name = "professor_id", length = 15, unique = true, nullable = false)
    private String professorId;

    @Override
    public String getId() {
        return professorId;
    }
}
