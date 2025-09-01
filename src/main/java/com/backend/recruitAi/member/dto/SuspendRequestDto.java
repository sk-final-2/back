package com.backend.recruitAi.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuspendRequestDto {
    private String reason; // 드롭다운에서 선택된 사유를 받습니다.
    private LocalDate suspendedUntil;
}
