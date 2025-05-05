package repository;

import com.example.classroom_reservation_system.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 학번, 교수번호, 관리자번호 중 하나 찾기
    Optional<Member> findByStudentIdOrProfessorIdOrAdminId(String studentId, String professorId, String adminId);

    // memberUuid로 찾기
    Optional<Member> findByMemberUuid(String memberUuid);

    boolean existsByStudentIdOrProfessorIdOrAdminId(String studentId, String professorId, String adminId);
}
