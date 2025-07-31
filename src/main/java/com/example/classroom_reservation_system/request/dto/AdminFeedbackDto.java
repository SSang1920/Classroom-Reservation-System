package com.example.classroom_reservation_system.request.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminFeedbackDto {

    private boolean approve;
    private String responseMessage;
}
