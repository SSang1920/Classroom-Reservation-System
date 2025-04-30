package com.example.classroom_reservation_system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "Admin")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Admin extends Member {

    @Column(name = "admin_id", length = 10, unique = true)
    private String adminId;

    @Override
    public String getId() {
        return adminId;
    }
}
