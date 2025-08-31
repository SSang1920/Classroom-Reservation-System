package com.example.classroom_reservation_system.reservation.service;

import com.example.classroom_reservation_system.faciliity.entity.Building;
import com.example.classroom_reservation_system.faciliity.entity.Classroom;
import com.example.classroom_reservation_system.faciliity.repository.BuildingRepository;
import com.example.classroom_reservation_system.faciliity.repository.ClassroomRepository;
import com.example.classroom_reservation_system.member.entity.Member;
import com.example.classroom_reservation_system.member.entity.Student;
import com.example.classroom_reservation_system.member.repository.MemberRepository;
import com.example.classroom_reservation_system.notification.repository.NotificationRepository;
import com.example.classroom_reservation_system.reservation.dto.request.ReservationRequest;
import com.example.classroom_reservation_system.reservation.entity.TimePeriod;
import com.example.classroom_reservation_system.reservation.repository.ReservationRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ReservationConcurrencyTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Member testMember1;
    private Member testMember2;
    private Classroom testClassroom;
    private Building testBuilding;

    @BeforeEach
    void setUp() {
        // 1. 테스트용 Building 생성
        testBuilding = buildingRepository.save(Building.builder()
                .name("Test Building")
                .build());

        // 2. Member를 구체 클래스인 Student로 생성하고, 필수 필드인 studentId 추가
        testMember1 = memberRepository.save(Student.builder()
                .name("concurrencyUser1")
                .email("c_user1@test.com")
                .studentId("c_user1") // 학생 고유 ID 추가
                .password(passwordEncoder.encode("password"))
                .build());
        testMember2 = memberRepository.save(Student.builder()
                .name("concurrencyUser2")
                .email("c_user2@test.com")
                .studentId("c_user2") // 학생 고유 ID 추가
                .password(passwordEncoder.encode("password"))
                .build());

        // 3. Classroom 생성 시 Building 연관관계 설정
        testClassroom = classroomRepository.save(Classroom.builder()
                .name("Concurrency Room")
                .capacity(10)
                .building(testBuilding)
                .build());
    }

    @AfterEach
    void tearDown() {
        transactionTemplate.execute(status -> {
            // 외래 키 제약 조건을 일시적으로 비활성화하여 테스트 데이터를 안정적으로 정리합니다.

            entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
            entityManager.createQuery("DELETE FROM PasswordResetToken").executeUpdate();
            entityManager.createQuery("DELETE FROM RefreshToken").executeUpdate();
            entityManager.createQuery("DELETE FROM History").executeUpdate();
            notificationRepository.deleteAllInBatch();
            reservationRepository.deleteAllInBatch();
            memberRepository.deleteAllInBatch();
            classroomRepository.deleteAllInBatch();
            buildingRepository.deleteAllInBatch();

            entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
            return null;
        });
        }

    @Test
    @DisplayName("두 명의 사용자가 동시에 같은 강의실을 예약할 때, 한 명만 성공해야 한다")
    void reservationConcurrencyTest() throws InterruptedException {
        // given: 2개의 스레드를 생성하고, 모든 스레드가 동시에 시작하도록 CountDownLatch를 사용합니다.
        final int numberOfThreads = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // ReflectionTestUtils를 사용하여 생성자 없이 ReservationRequest 객체를 생성하고 필드 값을 설정합니다.
        ReservationRequest request = new ReservationRequest();
        ReflectionTestUtils.setField(request, "classroomId", testClassroom.getId());
        ReflectionTestUtils.setField(request, "reservationDate", LocalDate.now().plusDays(1));
        ReflectionTestUtils.setField(request, "periods", List.of(TimePeriod.PERIOD_1, TimePeriod.PERIOD_2));

        // when: 2개의 스레드가 각각 다른 사용자로 동시에 예약을 요청합니다.
        for (int i = 0; i < numberOfThreads; i++) {
            final Member member = (i % 2 == 0) ? testMember1 : testMember2;
            executorService.submit(() -> {
                try {
                    latch.countDown();
                    latch.await(); // 모든 스레드가 준비될 때까지 대기
                    reservationService.createReservation(member.getMemberUuid(), request);
                } catch (Exception e) {
                    System.out.println("예약 실패 (예상된 결과): " + e.getMessage());
                }
            });
        }

        executorService.shutdown();
        boolean finished = executorService.awaitTermination(1, java.util.concurrent.TimeUnit.MINUTES);
        assertThat(finished).isTrue(); // 1분 안에 모든 작업이 끝나야 함

        // then: 최종적으로 데이터베이스에 저장된 예약은 단 1개여야 합니다.
        long reservationCount = reservationRepository.count();
        assertThat(reservationCount).isEqualTo(1);
    }
}