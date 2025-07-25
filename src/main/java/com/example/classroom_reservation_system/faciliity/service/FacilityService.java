package com.example.classroom_reservation_system.faciliity.service;

import com.example.classroom_reservation_system.common.exception.CustomException;
import com.example.classroom_reservation_system.common.exception.ErrorCode;
import com.example.classroom_reservation_system.faciliity.dto.BuildingResponseDto;
import com.example.classroom_reservation_system.faciliity.dto.ClassroomInBuildingResponseDto;
import com.example.classroom_reservation_system.faciliity.entity.Building;
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
public class FacilityService {
    private  final BuildingRepository buildingRepository;
    private final ClassroomRepository classroomRepository;

    //모든 건물 조회
    public List<BuildingResponseDto> getAllBuildings(){
        return buildingRepository.findAll().stream()
                .map(BuildingResponseDto::from)
                .collect(Collectors.toList());
    }

    //건물에 있는 강의실 조회
    public List<ClassroomInBuildingResponseDto> getClassroomsByBuilding(Long buildingId){
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(()-> new CustomException(ErrorCode.BUILDING_NOT_FOUND));

        return classroomRepository.findByBuildingOrderByNameAsc(building).stream()
                .map(ClassroomInBuildingResponseDto::from)
                .collect(Collectors.toList());
    }
}
