package com.ticketsystem.zimsmartvillages.controller;

import com.ticketsystem.zimsmartvillages.dto.*;
import com.ticketsystem.zimsmartvillages.service.ReportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    private final ReportsService reportsService;

    @Autowired
    public ReportsController(ReportsService reportsService) {
        this.reportsService = reportsService;
    }

    @GetMapping("/ticket-trends")
    public ResponseEntity<TicketTrendsDTO> getTicketTrends(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(reportsService.getTicketTrends(startDate, endDate));
    }

    @GetMapping("/status-distribution")
    public ResponseEntity<StatusDistributionDTO> getStatusDistribution() {
        return ResponseEntity.ok(reportsService.getStatusDistribution());
    }

    @GetMapping("/priority-analysis")
    public ResponseEntity<PriorityAnalysisDTO> getPriorityAnalysis() {
        return ResponseEntity.ok(reportsService.getPriorityAnalysis());
    }

    @GetMapping("/user-activity")
    public ResponseEntity<List<UserActivityDTO>> getUserActivity(@RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(reportsService.getUserActivity(days));
    }

    @GetMapping("/performance-metrics")
    public ResponseEntity<PerformanceMetricsDTO> getPerformanceMetrics(
            @RequestParam(required = false) Long userId) {
        return ResponseEntity.ok(reportsService.getPerformanceMetrics(userId));
    }

    @GetMapping("/response-time")
    public ResponseEntity<ResponseTimeAnalysisDTO> getResponseTimeAnalysis(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(reportsService.getResponseTimeAnalysis(startDate, endDate));
    }
}
