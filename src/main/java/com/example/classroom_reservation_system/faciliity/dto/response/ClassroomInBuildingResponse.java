package com.example.classroom_reservation_system.faciliity.dto.response;

import com.example.classroom_reservation_system.faciliity.entity.Classroom;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClassroomInBuildingResponse {

    private Long id;
    private String name;
    private int capacity;
    private String info;

    public static ClassroomInBuildingResponse from(Classroom classroom) {
        return ClassroomInBuildingResponse.builder()
                .id(classroom.getId())
                .name(classroom.getName())
                .capacity(classroom.getCapacity())
                .info(classroom.getEquipmentInfo())
                .build();
    }
}
