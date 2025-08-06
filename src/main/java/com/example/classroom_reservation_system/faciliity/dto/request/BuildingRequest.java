package com.example.classroom_reservation_system.faciliity.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BuildingRequest {

    @NotBlank(message = "건물 이름은 필수입니다.")
    private String name;
}
