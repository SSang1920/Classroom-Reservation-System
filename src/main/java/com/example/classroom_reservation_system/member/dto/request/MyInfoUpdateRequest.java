package com.example.classroom_reservation_system.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyInfoUpdateRequest {

    private String name;
    private String email;
}
