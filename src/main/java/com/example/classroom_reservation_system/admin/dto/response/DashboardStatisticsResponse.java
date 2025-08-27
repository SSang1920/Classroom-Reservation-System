package com.example.classroom_reservation_system.admin.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardStatisticsResponse {

    // KPI
    private long totalMembers;
    private long todayReservations;
    private long totalBuildings;
    private long totalClassrooms;
    private long pendingRequests;

    // Chart Data
    private List<String> reservationChartLabels;
    private List<Long> reservationChartData;
}
