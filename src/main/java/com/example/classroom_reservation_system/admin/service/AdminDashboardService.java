package com.example.classroom_reservation_system.admin.service;

import com.example.classroom_reservation_system.admin.dto.response.DashboardStatisticsResponse;
import com.example.classroom_reservation_system.faciliity.repository.BuildingRepository;
import com.example.classroom_reservation_system.faciliity.repository.ClassroomRepository;
import com.example.classroom_reservation_system.member.repository.MemberRepository;
import com.example.classroom_reservation_system.request.entity.RequestStatus;
import com.example.classroom_reservation_system.request.repository.ReservationChangeRequestRepository;
import com.example.classroom_reservation_system.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminDashboardService {

    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final BuildingRepository buildingRepository;
    private final ClassroomRepository classroomRepository;
    private final ReservationChangeRequestRepository requestRepository;

    public DashboardStatisticsResponse getDashboardStatistics() {
        // KPI 데이터 계산
        long totalMembers = memberRepository.count();
        long todayReservations = reservationRepository.countByDate(LocalDate.now());
        long totalBuildings = buildingRepository.count();
        long totalClassrooms = classroomRepository.count();
        long pendingRequests = requestRepository.countByStatus(RequestStatus.PENDING);

        // 차트 데이터 계산 (최근 7일)
        List<String> chartLabels = new ArrayList<>();
        List<Long> chartData = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);
            chartLabels.add(dayOfWeek);
            chartData.add(reservationRepository.countByDate(date));
        }

        // DTO 반환
        return DashboardStatisticsResponse.builder()
                .totalMembers(totalMembers)
                .todayReservations(todayReservations)
                .totalBuildings(totalBuildings)
                .totalClassrooms(totalClassrooms)
                .pendingRequests(pendingRequests)
                .reservationChartLabels(chartLabels)
                .reservationChartData(chartData)
                .build();
    }
}
