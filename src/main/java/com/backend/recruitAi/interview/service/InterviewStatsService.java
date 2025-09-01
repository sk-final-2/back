package com.backend.recruitAi.interview.service;

import com.backend.recruitAi.interview.dto.DailyStatsDto;
import com.backend.recruitAi.interview.dto.MonthlyStatsDto;
import com.backend.recruitAi.interview.repository.InterviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InterviewStatsService {

    private final InterviewRepository interviewRepository;

    // 총 면접 시도 횟수
    public long getTotalInterviews() {
        return interviewRepository.count();
    }

    // ✅ 날짜별 면접 시도 횟수
    public List<DailyStatsDto> getDailyInterviewAttempts(LocalDate startDate, LocalDate endDate) {
        // Repository 메서드에 LocalDate 전달
        List<Object[]> results = interviewRepository.countDailyInterviews(startDate, endDate);
        List<DailyStatsDto> dailyStatDtos = new ArrayList<>();

        // Repository에서 반환된 Object[]를 DTO로 변환
        for (Object[] row : results) {
            String dateStr = (String) row[0]; // DATE_FORMAT으로 반환된 String
            Long count = ((Number) row[1]).longValue(); // Long 타입으로 변환

            dailyStatDtos.add(new DailyStatsDto(LocalDate.parse(dateStr), count));
        }
        return dailyStatDtos;
    }

    // ✅ 월별 면접 시도 횟수
    public List<MonthlyStatsDto> getMonthlyInterviewAttempts(LocalDate startDate, LocalDate endDate) {
        // Repository 메서드에 LocalDate 전달
        List<Object[]> results = interviewRepository.countMonthlyInterviews(startDate, endDate);
        List<MonthlyStatsDto> monthlyStatDtos = new ArrayList<>();

        // Repository에서 반환된 Object[]를 DTO로 변환
        for (Object[] row : results) {
            String dateStr = (String) row[0]; // DATE_FORMAT으로 반환된 String
            Long count = ((Number) row[1]).longValue(); // Long 타입으로 변환

            monthlyStatDtos.add(new MonthlyStatsDto(dateStr, count));
        }
        return monthlyStatDtos;
    }
}