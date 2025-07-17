package com.example.classroom_reservation_system.faciliity;

import com.example.classroom_reservation_system.common.dto.ApiSuccessResponse;
import com.example.classroom_reservation_system.faciliity.dto.ClassroomResponseDto;
import com.example.classroom_reservation_system.faciliity.service.ClassroomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/classrooms")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomService classroomService;

    @GetMapping
    public ResponseEntity<ApiSuccessResponse<List<ClassroomResponseDto>>> getAllClassrooms() {
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "강의실 목록 조회 성공", classroomService.getAllClassrooms()));
    }
}
