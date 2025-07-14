package com.example.classroom_reservation_system.member.service;

import com.example.classroom_reservation_system.member.dto.request.SignUpRequest;
import com.example.classroom_reservation_system.member.entity.Professor;
import com.example.classroom_reservation_system.member.entity.Role;
import com.example.classroom_reservation_system.member.entity.Student;
import com.example.classroom_reservation_system.common.exception.CustomException;
import com.example.classroom_reservation_system.common.exception.ErrorCode;
import com.example.classroom_reservation_system.member.repository.MemberRepository;
import com.example.classroom_reservation_system.member.repository.ProfessorRepository;
import com.example.classroom_reservation_system.member.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberRegistrationService {

    private final StudentRepository studentRepository;
    private final ProfessorRepository professorRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    private static final Pattern ID_PATTERN = Pattern.compile("^[a-zA-Z0-9]{6,15}$");

    /**
     * 회원가입
     */
    public void signUp(SignUpRequest request) {

        if (request.getRole() == null) {
            throw new CustomException(ErrorCode.VALIDATION_FAIL);
        }
        // ADMIN으로 회원가입 요청 차단
        if (request.getRole() == Role.ADMIN) {
            throw new CustomException(ErrorCode.INVALID_ROLE_FOR_SIGNUP);
        }

        // 비밀번호, 비밀번호 확인 일치 여부 체크
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new CustomException(ErrorCode.PASSWORD_CONFIRM_NOT_MATCH);
        }

        // 중복 검사
        validateIdFormat((request.getId()));
        checkDuplicateId(request.getId());
        checkDuplicateEmail(request.getEmail());

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 역할에 따라 Member 생성
        switch (request.getRole()) {
            case STUDENT -> {
                Student student = Student.builder()
                        .studentId(request.getId())
                        .name(request.getName())
                        .password(encodedPassword)
                        .email(request.getEmail())
                        .role(Role.STUDENT)
                        .build();

                studentRepository.save(student);
            }

            case PROFESSOR -> {
                Professor professor = Professor.builder()
                        .professorId(request.getId())
                        .name(request.getName())
                        .password(encodedPassword)
                        .email(request.getEmail())
                        .role(Role.PROFESSOR)
                        .build();

                professorRepository.save(professor);
            }

            default -> throw new CustomException(ErrorCode.VALIDATION_FAIL);
        }
    }

    /**
     * 아이디 형식 검증
     * @param id
     */
    public void validateIdFormat(String id) {
        if (!ID_PATTERN.matcher(id).matches()) {
            throw new CustomException(ErrorCode.INVALID_ID_FORMAT);
        }
    }

    /**
     * 아이디 중복 검사 로직
     * @param id 아이디
     */
    public void checkDuplicateId(String id) {
        if (memberService.findMemberById(id).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_ID);
        }
    }

    /**
     * 이메일 중복 검사
     * @param email
     */
    public void checkDuplicateEmail(String email){
        boolean exists = memberRepository.existsByEmail(email);

        if (exists) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
    }
}
