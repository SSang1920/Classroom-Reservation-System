package com.example.classroom_reservation_system.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "admin")
@DiscriminatorValue("ADMIN")
@PrimaryKeyJoinColumn(name = "member_id")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class Admin extends Member {

    @Column(name = "admin_id", length = 15, unique = true, nullable = false)
    private String adminId;

    @Override
    public String getId() {
        return adminId;
    }
}
