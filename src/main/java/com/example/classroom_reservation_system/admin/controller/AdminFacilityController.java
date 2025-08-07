package com.example.classroom_reservation_system.admin.controller;

import com.example.classroom_reservation_system.admin.service.AdminFacilityService;
import com.example.classroom_reservation_system.common.dto.ApiSuccessResponse;
import com.example.classroom_reservation_system.faciliity.dto.request.BuildingRequest;
import com.example.classroom_reservation_system.faciliity.dto.request.ClassroomRequest;
import com.example.classroom_reservation_system.faciliity.dto.request.ClassroomUnavailableRequest;
import com.example.classroom_reservation_system.faciliity.dto.response.BuildingResponse;
import com.example.classroom_reservation_system.faciliity.dto.response.ClassroomInBuildingResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/facilities")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminFacilityController {

    private final AdminFacilityService adminFacilityService;

    /**
     * 모든 건물 목록 조회
     */
    @GetMapping("/buildings")
    public ResponseEntity<ApiSuccessResponse<List<BuildingResponse>>> getAllBuildings() {
        List<BuildingResponse> buildings = adminFacilityService.getAllBuildings();
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "모든 건물 목록 조회에 성공하였습니다.", buildings));
    }

    /**
     * 새로운 건물 생성
     */
    @PostMapping("/buildings")
    public ResponseEntity<ApiSuccessResponse<BuildingResponse>> createBuilding(@Valid @RequestBody BuildingRequest request) {
        BuildingResponse createBuilding = adminFacilityService.createBuilding(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiSuccessResponse.of(201, "건물이 성공적으로 추가되었습니다.", createBuilding));
    }

    /**
     * 기존 건물 정보 수정
     */
    @PutMapping("/buildings/{buildingId}")
    public ResponseEntity<ApiSuccessResponse<BuildingResponse>> updateBuilding(@PathVariable Long buildingId, @Valid @RequestBody BuildingRequest request) {
        BuildingResponse updatedBuilding = adminFacilityService.updateBuilding(buildingId, request);
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "건물 정보가 수정되었습니다.", updatedBuilding));
    }

    /**
     * 건물 삭제 (논리적 삭제)
     */
    @DeleteMapping("/buildings/{buildingId}")
    public ResponseEntity<ApiSuccessResponse<Void>> deleteBuilding(@PathVariable Long buildingId) {
        adminFacilityService.deleteBuilding(buildingId);
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "건물이 삭제되었습니다.", null));
    }

    /**
     * 특정 건물에 속한 모든 강의실 목록 조회
     */
    @GetMapping("/buildings/{buildingId}/classrooms")
    public ResponseEntity<ApiSuccessResponse<List<ClassroomInBuildingResponse>>> getClassroomsByBuilding(@PathVariable Long buildingId) {
        List<ClassroomInBuildingResponse> classrooms = adminFacilityService.getClassroomsByBuilding(buildingId);
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "건물 내 강의실 목록 조회에 성공하였습니다.", classrooms));
    }

    /**
     * 특정 건물에 새로운 강의실 생성
     */
    @PostMapping("buildings/{buildingId}/classrooms")
    public ResponseEntity<ApiSuccessResponse<ClassroomInBuildingResponse>> createClassroom(@PathVariable Long buildingId, @Valid @RequestBody ClassroomRequest request) {
        ClassroomInBuildingResponse createdClassroom = adminFacilityService.createClassroom(buildingId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiSuccessResponse.of(201, "강의실이 성공적으로 추가되었습니다.", createdClassroom));
    }

    /**
     * 기존 강의실 정보 수정
     */
    @PutMapping("classrooms/{classroomId}")
    public ResponseEntity<ApiSuccessResponse<ClassroomInBuildingResponse>> updateClassroom(@PathVariable Long classroomId, @Valid @RequestBody ClassroomRequest request) {
        ClassroomInBuildingResponse updatedClassroom = adminFacilityService.updateClassroom(classroomId, request);
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "강의실 정보가 수정되었습니다.", updatedClassroom));
    }

    /**
     * 강의실 삭제 (논리적 삭제)
     */
    @DeleteMapping("classrooms/{classroomId}")
    public ResponseEntity<ApiSuccessResponse<Void>> deleteClassroom(@PathVariable Long classroomId) {
        adminFacilityService.deleteClassroom(classroomId);
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "강의실이 삭제되었습니다.", null));
    }

    /**
     * 강의실을 사용 불가로 변경
     */
    @PostMapping("/classrooms/{classroomId}/unavailable")
    public ResponseEntity<ApiSuccessResponse<Void>> markClassroomAsUnavailable(@PathVariable Long classroomId, @RequestBody ClassroomUnavailableRequest request) {
        adminFacilityService.markClassroomAsUnavailable(classroomId, request);
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "강의실을 사용 불가로 변경했습니다.", null));
    }

    /**
     * 강의실을 사용 가능으로 변경
     */
    @PostMapping("/classrooms/{classroomId}/available")
    public ResponseEntity<ApiSuccessResponse<Void>> markClassroomAsAvailable(@PathVariable Long classroomId) {
        adminFacilityService.markClassroomAsAvailable(classroomId);
        return ResponseEntity.ok(ApiSuccessResponse.of(200, "강의실을 사용 가능으로 변경했습니다.", null));
    }
}
