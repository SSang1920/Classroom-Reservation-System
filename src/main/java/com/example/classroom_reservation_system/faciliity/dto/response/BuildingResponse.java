package com.example.classroom_reservation_system.faciliity.dto.response;

import com.example.classroom_reservation_system.faciliity.entity.Building;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BuildingResponse {

    private Long id;
    private String name;

    public static BuildingResponse from(Building building){
        return BuildingResponse.builder()
                .id(building.getId())
                .name(building.getName())
                .build();
    }

}
