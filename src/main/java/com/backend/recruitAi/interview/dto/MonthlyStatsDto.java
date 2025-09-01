package com.backend.recruitAi.interview.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@NoArgsConstructor
public class MonthlyStatsDto {

    private String date;
    private Long count;

    public MonthlyStatsDto(String date, Long count) {
        this.date = date;
        this.count = count;
    }

    public MonthlyStatsDto(String date, BigInteger count) {
        this.date = date;
        this.count = count.longValue();
    }

    public MonthlyStatsDto(String date, long count) {
        this.date = date;
        this.count = count;
    }
}
