package service;

import com.example.classroom_reservation_system.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import repository.MemberRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
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
        Optional<Member> findMemberId = memberRepository.findByStudentIdOrProfessorIdOrAdminId(id, id, id);

        // ID에 해당하는 Member가 없는 경우
        Member member = findMemberId.orElseThrow(() ->
                new IllegalArgumentException("아이디가 존재하지 않습니다."));

        // 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 로그인 성공
        return member;
    }
}
