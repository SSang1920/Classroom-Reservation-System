package com.example.classroom_reservation_system.init;

import com.example.classroom_reservation_system.faciliity.entity.Building;
import com.example.classroom_reservation_system.faciliity.entity.Classroom;
import com.example.classroom_reservation_system.faciliity.entity.ClassroomState;
import com.example.classroom_reservation_system.faciliity.repository.BuildingRepository;
import com.example.classroom_reservation_system.faciliity.repository.ClassroomRepository;
import com.example.classroom_reservation_system.member.entity.Admin;
import com.example.classroom_reservation_system.member.entity.Professor;
import com.example.classroom_reservation_system.member.entity.Role;
import com.example.classroom_reservation_system.member.entity.Student;
import com.example.classroom_reservation_system.member.repository.AdminRepository;
import com.example.classroom_reservation_system.member.repository.ProfessorRepository;
import com.example.classroom_reservation_system.member.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InitData implements CommandLineRunner {

    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final BuildingRepository buildingRepository;
    private final ClassroomRepository classroomRepository;

    @Override
    public void run(String... args) {
        // 관리자 계정
        if (adminRepository.findByAdminId("admin001").isEmpty()) {
            Admin admin = Admin.builder()
                    .adminId("admin001")
                    .name("관리자 계정")
                    .password(passwordEncoder.encode("admin1234"))
                    .email("admin001@naver.com")
                    .build();
            adminRepository.save(admin);
        }

        // 학생 계정
        if (studentRepository.findByStudentId("stu2023").isEmpty()) {
            Student student = Student.builder()
                    .studentId("stu2023")
                    .name("학생 계정")
                    .password(passwordEncoder.encode("student1234"))
                    .email("stu2023@naver.com")
                    .build();
            studentRepository.save(student);
        }

        if (studentRepository.findByStudentId("sangjun00").isEmpty()) {
            Student student = Student.builder()
                    .studentId("sangjun00")
                    .name("김상준")
                    .password(passwordEncoder.encode("qwer1234"))
                    .email("bh0710403@naver.com")
                    .build();
            studentRepository.save(student);
        }

        if (studentRepository.findByStudentId("sangyoon053").isEmpty()) {
            Student student = Student.builder()
                    .studentId("sangyoon053")
                    .name("용상윤")
                    .password(passwordEncoder.encode("asdfasdf"))
                    .email("sangyoon053@naver.com")
                    .build();
            studentRepository.save(student);
        }

        // 교수 계정
        if (professorRepository.findByProfessorId("pro2023").isEmpty()) {
            Professor professor = Professor.builder()
                    .professorId("pro2023")
                    .name("교수 계정")
                    .password(passwordEncoder.encode("professor1234"))
                    .email("pro2023@naver.com")
                    .build();
            professorRepository.save(professor);
        }

        createFacilities();
    }
    private void createFacilities() {
        // 1. 요청하신 'if...isEmpty' 패턴을 적용하여 건물 데이터를 생성합니다.
        if (buildingRepository.findByName("공학관").isEmpty()) {
            buildingRepository.save(Building.builder().name("공학관").build());
        }
        if (buildingRepository.findByName("인문관").isEmpty()) {
            buildingRepository.save(Building.builder().name("인문관").build());
        }

        // 2. 강의실을 생성하려면 방금 생성했거나 이미 존재하던 건물 엔티티가 필요하므로 다시 조회합니다.
        Building engineeringBuilding = buildingRepository.findByName("공학관")
                .orElseThrow(() -> new IllegalStateException("데이터 초기화 오류: 공학관을 찾을 수 없습니다."));
        Building humanitiesBuilding = buildingRepository.findByName("인문관")
                .orElseThrow(() -> new IllegalStateException("데이터 초기화 오류: 인문관을 찾을 수 없습니다."));

        // 3. 요청하신 'if...isEmpty' 패턴을 적용하여 강의실 데이터를 생성합니다.
        if (classroomRepository.findByName("공학관-101호").isEmpty()) {
            classroomRepository.save(Classroom.builder().name("공학관-101호").capacity(50).building(engineeringBuilding).state(ClassroomState.AVAILABLE).equipmentInfo("프로젝터, 화이트보드").build());
        }

        if (classroomRepository.findByName("인문관-203호").isEmpty()) {
            classroomRepository.save(Classroom.builder().name("인문관-203호").capacity(30).building(humanitiesBuilding).state(ClassroomState.AVAILABLE).equipmentInfo("프로젝터, 스마트 TV").build());
        }
    }
}
