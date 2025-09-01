package com.backend.recruitAi.interview.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.math.BigInteger;

@Data
@NoArgsConstructor
public class DailyStatsDto {

    private LocalDate date;
    private Long count;

    public DailyStatsDto(LocalDate date, Long count) {
        this.date = date;
        this.count = count;
    }

    public DailyStatsDto(LocalDate date, BigInteger count) {
        this.date = date;
        this.count = count.longValue();
    }
}