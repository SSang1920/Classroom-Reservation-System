package com.example.classroom_reservation_system.faciliity.controller;


import com.example.classroom_reservation_system.common.dto.ApiSuccessResponse;
import com.example.classroom_reservation_system.faciliity.dto.BuildingResponseDto;
import com.example.classroom_reservation_system.faciliity.dto.ClassroomInBuildingResponseDto;
import com.example.classroom_reservation_system.faciliity.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/facilities")
@RequiredArgsConstructor
public class FacilityController {

    private final FacilityService facilityService;

    /**
     * 모든 건물 목록 조회 API
     * JS가 페이지 로딩할떄 API 호출
     */
    @GetMapping("/buildings")
    public ResponseEntity<ApiSuccessResponse<List<BuildingResponseDto>>> getAllBuildings() {
        List<BuildingResponseDto> buildings = facilityService.getAllBuildings();

        return ResponseEntity.ok(ApiSuccessResponse.of(200, "모든 건물 목록 조회 성공",buildings));
    }

    /**
     * 건물에 속한 모든 강의실 목록 조회 API
     * 프론트에서 건물 선택시 API호출
     */
    @GetMapping("/buildings/{buildingId}/classrooms")
    public ResponseEntity<ApiSuccessResponse<List<ClassroomInBuildingResponseDto>>> getClassroomsByBuilding(@PathVariable Long buildingId) {
        List<ClassroomInBuildingResponseDto> classrooms = facilityService.getClassroomsByBuilding(buildingId);

        return ResponseEntity.ok(ApiSuccessResponse.of(200, "건물 내 강의실 목록 조회 성공", classrooms));
    }
}
