package com.example.classroom_reservation_system.admin.service;

import com.example.classroom_reservation_system.common.exception.CustomException;
import com.example.classroom_reservation_system.common.exception.ErrorCode;
import com.example.classroom_reservation_system.faciliity.dto.request.BuildingRequest;
import com.example.classroom_reservation_system.faciliity.dto.request.ClassroomRequest;
import com.example.classroom_reservation_system.faciliity.dto.request.ClassroomUnavailableRequest;
import com.example.classroom_reservation_system.faciliity.dto.response.BuildingResponse;
import com.example.classroom_reservation_system.faciliity.dto.response.ClassroomInBuildingResponse;
import com.example.classroom_reservation_system.faciliity.entity.Building;
import com.example.classroom_reservation_system.faciliity.entity.Classroom;
import com.example.classroom_reservation_system.faciliity.repository.BuildingRepository;
import com.example.classroom_reservation_system.faciliity.repository.ClassroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminFacilityService {

    private final BuildingRepository buildingRepository;
    private final ClassroomRepository classroomRepository;

    /**
     * 모든 건물 목록 조회
     */
    public List<BuildingResponse> getAllBuildings() {
        return buildingRepository.findAllByOrderByNameAsc().stream()
                .map(BuildingResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 새로운 건물 생성
     */
    @Transactional
    public BuildingResponse createBuilding(BuildingRequest request) {
        // 건물 이름 중복 검사
        if (buildingRepository.existsByName(request.getName())) {
            throw new CustomException(ErrorCode.DUPLICATE_BUILDING_NAME);
        }
        Building newBuilding = Building.builder()
                .name(request.getName())
                .build();

        Building savedBuilding = buildingRepository.save(newBuilding);
        return BuildingResponse.from(savedBuilding);
    }

    /**
     * 기존 건물 정보 수정
     */
    @Transactional
    public BuildingResponse updateBuilding(Long buildingId, BuildingRequest request) {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new CustomException(ErrorCode.BUILDING_NOT_FOUND));

        // 수정하려는 이름이 현재 이름과 다르면서, 다른 건물에서 이미 사용 중인지 검사
        if (!building.getName().equals(request.getName()) && buildingRepository.existsByName(request.getName())) {
            throw new CustomException(ErrorCode.DUPLICATE_BUILDING_NAME);
        }

        building.updateName(request.getName());
        return BuildingResponse.from(building);
    }

    /**
     * 건물 삭제 (논리적 삭제)
     */
    @Transactional
    public void deleteBuilding(Long buildingId) {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new CustomException(ErrorCode.BUILDING_NOT_FOUND));

        buildingRepository.delete(building);
    }

    /**
     * 특정 건물에 속한 모든 강의실 목록 조회
     */
    public List<ClassroomInBuildingResponse> getClassroomsByBuilding(Long buildingId) {
        if (!buildingRepository.existsById(buildingId)) {
            throw new CustomException(ErrorCode.BUILDING_NOT_FOUND);
        }

        return classroomRepository.findByBuildingIdOrderByNameAsc(buildingId).stream()
                .map(ClassroomInBuildingResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 건물에 새로운 강의실 생성
     */
    @Transactional
    public ClassroomInBuildingResponse createClassroom(Long buildingId, ClassroomRequest request) {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new CustomException(ErrorCode.BUILDING_NOT_FOUND));

        // 해당 건물 내에서 강의실 이름이 중복되는지 검사
        if (classroomRepository.existsByBuildingAndName(building, request.getName())) {
            throw new CustomException(ErrorCode.DUPLICATE_CLASSROOM_NAME);
        }

        Classroom newClassroom = Classroom.builder()
                .name(request.getName())
                .capacity(request.getCapacity())
                .equipmentInfo(request.getInfo())
                .build();

        building.addClassroom(newClassroom);
        Classroom savedClassroom = classroomRepository.save(newClassroom);

        return ClassroomInBuildingResponse.from(savedClassroom);
    }

    /**
     * 기존 강의실 정보 수정
     */
    @Transactional
    public ClassroomInBuildingResponse updateClassroom(Long classroomId, ClassroomRequest request) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLASSROOM_NOT_FOUND));

        // 수정하려는 이름이 현재 이름과 다르면서, 같은 건물 내 다른 강의실에서 이미 사용 중인지 검사
        if (!classroom.getName().equals(request.getName()) &&
                classroomRepository.existsByBuildingAndName(classroom.getBuilding(), request.getName())) {
            throw new CustomException(ErrorCode.DUPLICATE_CLASSROOM_NAME);
        }

        classroom.updateDetails(
                request.getName(),
                request.getCapacity(),
                request.getInfo()
        );
        return ClassroomInBuildingResponse.from(classroom);
    }

    /**
     * 강의실 삭제 (논리적 삭제)
     */
    @Transactional
    public void deleteClassroom(Long classroomId) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLASSROOM_NOT_FOUND));

        classroomRepository.delete(classroom);
    }

    /**
     * 강의실을 사용 불가로 변경
     */
    @Transactional
    public void markClassroomAsUnavailable(Long classroomId, ClassroomUnavailableRequest request) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLASSROOM_NOT_FOUND));
        classroom.markAsUnavailable(request.getReason());
    }

    /**
     * 강의실을 사용 가능으로 변경
     */
    @Transactional
    public void markClassroomAsAvailable(Long classroomId) {
        Classroom classroom = classroomRepository.findById(classroomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLASSROOM_NOT_FOUND));
        classroom.makeAvailable();
    }
}
