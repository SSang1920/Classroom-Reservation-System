package com.example.classroom_reservation_system.faciliity.service;

import com.example.classroom_reservation_system.faciliity.dto.ClassroomResponseDto;
import com.example.classroom_reservation_system.faciliity.repository.ClassroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClassroomService {
    private final ClassroomRepository classroomRepository;

    public List<ClassroomResponseDto> getAllClassrooms() {
        return classroomRepository.findAll().stream()
                .map(ClassroomResponseDto::from)
                .collect(Collectors.toList());
    }
}
