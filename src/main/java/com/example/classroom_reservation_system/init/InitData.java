package com.example.classroom_reservation_system.init;

import com.example.classroom_reservation_system.entity.Admin;
import com.example.classroom_reservation_system.entity.Professor;
import com.example.classroom_reservation_system.entity.Role;
import com.example.classroom_reservation_system.entity.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.example.classroom_reservation_system.repository.MemberRepository;

@Component
@RequiredArgsConstructor
public class InitData implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 관리자 계정
        if (memberRepository.findByUserId("admin001").isEmpty()) {
            Admin admin = Admin.builder()
                    .adminId("admin001")
                    .name("관리자 계정")
                    .password(passwordEncoder.encode("admin1234"))
                    .role(Role.ADMIN)
                    .build();
            memberRepository.save(admin);
        }

        // 학생 계정
        if (memberRepository.findByUserId("stu2023").isEmpty()) {
            Student student = Student.builder()
                    .studentId("stu2023")
                    .name("학생 계정")
                    .password(passwordEncoder.encode("student1234"))
                    .role(Role.STUDENT)
                    .year(1)
                    .build();
            memberRepository.save(student);
        }

        // 교수 계정
        if (memberRepository.findByUserId("pro2023").isEmpty()) {
            Professor professor = Professor.builder()
                    .professorId("pro2023")
                    .name("교수 계정")
                    .password(passwordEncoder.encode("professor1234"))
                    .role(Role.PROFESSOR)
                    .build();
            memberRepository.save(professor);
        }
    }
}
