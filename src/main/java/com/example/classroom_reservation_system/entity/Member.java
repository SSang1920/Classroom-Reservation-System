package com.example.classroom_reservation_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "Member")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "member_uuid", updatable = false, nullable = false, unique = true, length = 36)
    private String memberUuid;

    @Column(length = 100, nullable = false)
    private String password;

    @Column(length = 20, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "member")
    private List<Notification> notifications = new ArrayList<>();

    @PrePersist
    public void generateUUID() {
        if (memberUuid == null) {
            memberUuid = UUID.randomUUID().toString();
        }
    }
}
