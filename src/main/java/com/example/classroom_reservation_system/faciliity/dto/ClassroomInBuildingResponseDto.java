package com.example.classroom_reservation_system.faciliity.dto;

import com.example.classroom_reservation_system.faciliity.entity.Classroom;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClassroomInBuildingResponseDto {

    private Long id;
    private String name;
    private int capacity;

    public static ClassroomInBuildingResponseDto from(Classroom classroom) {
        return ClassroomInBuildingResponseDto.builder()
                .id(classroom.getId())
                .name(classroom.getName())
                .capacity(classroom.getCapacity())
                .build();
    }
}
