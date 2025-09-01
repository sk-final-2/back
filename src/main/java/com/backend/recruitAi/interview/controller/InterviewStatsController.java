package com.backend.recruitAi.interview.controller;

import com.backend.recruitAi.interview.dto.DailyStatsDto;
import com.backend.recruitAi.interview.dto.MonthlyStatsDto;
import com.backend.recruitAi.interview.service.InterviewStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
public class InterviewStatsController {

    private final InterviewStatsService interviewStatsService;

    // 총 면접 시도 횟수 API
    @GetMapping("/total")
    public Map<String, Object> getTotalInterviews() {
        long count = interviewStatsService.getTotalInterviews();
        return Map.of("success", true, "data", Map.of("count", count));
    }

    // 일별 면접 시도 횟수 API (별도 엔드포인트)
    @GetMapping("/daily-interview-attempts")
    public Map<String, Object> getDailyInterviewStats(
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate) {

        List<DailyStatsDto> dailyStatDtos = interviewStatsService.getDailyInterviewAttempts(startDate, endDate);
        return Map.of("success", true, "data", dailyStatDtos);
    }

    // ✅ 월별 면접 시도 횟수 API (별도 엔드포인트)
    @GetMapping("/monthly-interview-attempts")
    public Map<String, Object> getMonthlyInterviewStats(
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate) {

        List<MonthlyStatsDto> monthlyStatDtos = interviewStatsService.getMonthlyInterviewAttempts(startDate, endDate);
        return Map.of("success", true, "data", monthlyStatDtos);
    }
}