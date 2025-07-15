package com.example.classroom_reservation_system.faciliity.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "building", indexes = {
        @Index(name = "idx_building_name", columnList = "building_name")
})
@SQLDelete(sql = "UPDATE building SET deleted_at = NOW() WHERE building_id = ?")    // 논리적 삭제 추가
@SQLRestriction("deleted_at IS NULL")       // 모든 조회 쿼리에 조건을 추가하여, 논리적으로 삭제된 데이터는 조회하지 않도록 함
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString(exclude = "classrooms")
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "building_id")
    private Long id;

    @Column(name = "building_name", length = 20, nullable = false, unique = true)
    private String name;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder.Default
    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Classroom> classrooms = new ArrayList<>();

    /**
     * 연관관계 편의 메서드 - 강의실 추가, 제거
     */
    public void addClassroom(Classroom classroom){
        classrooms.add(classroom);
        classroom.linkToBuilding(this);
    }

    public void removeClassroom(Classroom classroom){
        classrooms.remove(classroom);
        classroom.linkToBuilding(null);
    }
}
