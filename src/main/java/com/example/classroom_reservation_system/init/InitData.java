package com.example.classroom_reservation_system.init;

import com.example.classroom_reservation_system.entity.Admin;
import com.example.classroom_reservation_system.entity.Professor;
import com.example.classroom_reservation_system.entity.Role;
import com.example.classroom_reservation_system.entity.Student;
import com.example.classroom_reservation_system.repository.member.AdminRepository;
import com.example.classroom_reservation_system.repository.member.ProfessorRepository;
import com.example.classroom_reservation_system.repository.member.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.example.classroom_reservation_system.repository.member.MemberRepository;

@Component
@RequiredArgsConstructor
public class InitData implements CommandLineRunner {

    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 관리자 계정
        if (adminRepository.findByAdminId("admin001").isEmpty()) {
            Admin admin = Admin.builder()
                    .adminId("admin001")
                    .name("관리자 계정")
                    .password(passwordEncoder.encode("admin1234"))
                    .email("admin001@naver.com")
                    .role(Role.ADMIN)
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
                    .role(Role.STUDENT)
                    .year(1)
                    .build();
            studentRepository.save(student);
        }

        if (studentRepository.findByStudentId("sangjun00").isEmpty()) {
            Student student = Student.builder()
                    .studentId("sangjun00")
                    .name("김상준")
                    .password(passwordEncoder.encode("qwer1234"))
                    .email("bh0710403@naver.com")
                    .role(Role.STUDENT)
                    .year(4)
                    .build();
            studentRepository.save(student);
        }

        if (studentRepository.findByStudentId("sangyoon053").isEmpty()) {
            Student student = Student.builder()
                    .studentId("sangyoon053")
                    .name("용상윤")
                    .password(passwordEncoder.encode("asdfasdf"))
                    .email("sangyoon053@naver.com")
                    .role(Role.ADMIN)
                    .year(4)
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
                    .role(Role.PROFESSOR)
                    .build();
            professorRepository.save(professor);
        }
    }
}
