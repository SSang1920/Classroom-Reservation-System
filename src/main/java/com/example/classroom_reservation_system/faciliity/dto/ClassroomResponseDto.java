package com.example.classroom_reservation_system.faciliity.dto;

import com.example.classroom_reservation_system.faciliity.entity.Classroom;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClassroomResponseDto {
    private Long id;
    private String name;
    private int capacity;

    public static ClassroomResponseDto from(Classroom classroom) {
        return new ClassroomResponseDto(
                classroom.getId(),
                classroom.getBuilding().getName() + " " + classroom.getName(),
                classroom.getCapacity()
        );
    }
}
