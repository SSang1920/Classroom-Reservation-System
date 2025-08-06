package com.example.classroom_reservation_system.faciliity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ClassroomRequest {

    @NotBlank(message = "강의실 이름은 필수입니다.")
    private String name;

    @NotNull(message = "수용 인원은 필수입니다.")
    @Positive(message = "수용 인원은 1 이상의 숫자여야 합니다.")
    private Integer capacity;

    private String info;
}
