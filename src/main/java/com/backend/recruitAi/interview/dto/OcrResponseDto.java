package com.backend.recruitAi.interview.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OcrResponseDto {
    private String ocrOutput;
}