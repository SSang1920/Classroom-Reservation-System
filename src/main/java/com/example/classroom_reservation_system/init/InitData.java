package com.example.classroom_reservation_system.init;

import com.example.classroom_reservation_system.faciliity.entity.Building;
import com.example.classroom_reservation_system.faciliity.entity.Classroom;
import com.example.classroom_reservation_system.faciliity.entity.ClassroomState;
import com.example.classroom_reservation_system.faciliity.repository.BuildingRepository;
import com.example.classroom_reservation_system.faciliity.repository.ClassroomRepository;
import com.example.classroom_reservation_system.member.entity.Admin;
import com.example.classroom_reservation_system.member.entity.Professor;
import com.example.classroom_reservation_system.member.entity.Student;
import com.example.classroom_reservation_system.member.repository.AdminRepository;
import com.example.classroom_reservation_system.member.repository.ProfessorRepository;
import com.example.classroom_reservation_system.member.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        // 페이징 테스트
        if (studentRepository.findByStudentId("testuser0").isEmpty()) {
            for (int i = 0; i < 100; i++) {
                Student testStudent = Student.builder()
                        .studentId("testuser" + i)
                        .name("테스트학생" + i)
                        .password(passwordEncoder.encode("test1234"))
                        .email("testuser" + i + "@test.com")
                        .build();
                studentRepository.save(testStudent);
            }
        }

        createFacilities();
    }
    private void createFacilities() {
        if (classroomRepository.findByName("공학관-101호").isPresent()){
            return ;
        }

        List<Building> buildings = List.of("공학관", "인문관", "자연관").stream()
                .map(name -> Building.builder().name(name).build())
                .collect(Collectors.toList());
        buildingRepository.saveAll(buildings);

        List<Classroom> classroomsTosave = new ArrayList<>();
        for (Building building : buildings) {
            for (int floor = 1; floor <= 3; floor++) {
                for (int room = 1; room <= 10; room++) {
                    String roomNumber = String.format("%d%02d", floor, room);
                    String classroomName = String.format("%s-%s호", building.getName(), roomNumber);
                    int capacity = 20 + (int) (Math.random() * 31);

                   classroomsTosave.add(Classroom.builder()
                           .name(classroomName)
                           .capacity(capacity)
                           .building(building)
                           .state(ClassroomState.AVAILABLE)
                           .build());
                }
            }
        }
        classroomRepository.saveAll(classroomsTosave);
    }
}
