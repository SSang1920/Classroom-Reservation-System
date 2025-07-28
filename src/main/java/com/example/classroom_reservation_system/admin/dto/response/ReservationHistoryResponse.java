package com.example.classroom_reservation_system.admin.dto.response;

import com.example.classroom_reservation_system.history.entity.History;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReservationHistoryResponse {
    private String state;
    private LocalDateTime changedAt;
    private String classroomName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public static ReservationHistoryResponse from(History history){
        return ReservationHistoryResponse.builder()
                .state(history.getHistoryState().getDescription())
                .changedAt(history.getCreatedAt())
                .classroomName(history.getClassroom().getName())
                .startTime(history.getStartTime())
                .endTime(history.getEndTime())
                .build();
    }

}
