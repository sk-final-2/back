// src/main/java/com/backend/recruitAi/global/response/PageResponse.java
package com.backend.recruitAi.result.dto;

import org.springframework.data.domain.Page;
import java.util.List;

public record PageResponse<T>(
        List<T> content,
        int page,           // 현재 페이지(0-based)
        int size,           // 페이지 크기
        long totalElements, // 전체 개수
        int totalPages,     // 전체 페이지 수
        boolean first,
        boolean last,
        boolean empty
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.isEmpty()
        );
    }
}
