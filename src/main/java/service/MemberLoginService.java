package service;

import com.example.classroom_reservation_system.entity.Member;
import com.example.classroom_reservation_system.exception.CustomException;
import com.example.classroom_reservation_system.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberLoginService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 로그인 처리
     * @param id studentId, professorId, adminId 중 하나
     * @param password
     * @return 인증된 Member 객체
     */
    public Member login(String id, String password) {

        // ID로 Member 조회 (학생, 교수, 관리자)
        Member member = memberRepository.findByStudentIdOrProfessorIdOrAdminId(id, id, id)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        // 로그인 성공
        return member;
    }
}
