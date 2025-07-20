package com.example.classroom_reservation_system.faciliity.dto;

import com.example.classroom_reservation_system.faciliity.entity.Building;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BuildingResponseDto {

    private Long id;
    private String name;

    public static BuildingResponseDto from(Building building){
        return BuildingResponseDto.builder()
                .id(building.getId())
                .name(building.getName())
                .build();
    }

}
