// AvgScoreService.java
package com.backend.recruitAi.result.service;

import com.backend.recruitAi.interview.entity.Interview;
import com.backend.recruitAi.result.dto.AvgScoreDto;
import com.backend.recruitAi.result.entity.InterviewResult;
import com.backend.recruitAi.result.repository.InterviewResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvgScoreService {
    private final InterviewResultRepository repo;

    @Transactional(readOnly = true)
    @Cacheable(value = "avgScoreByJob",
            key = "T(org.springframework.util.StringUtils).trimAllWhitespace(#job == null ? '' : #job).toUpperCase()",
            unless = "#result == null")
    public AvgScoreDto getAvgByJob(String job) {
        return repo.findAverageScoresByJob(job).orElse(null);
    }

    // 저장 직후 응답용으로만 쓰는 논-캐시 버전
    @Transactional(readOnly = true)
    public AvgScoreDto computeAvgByJobNoCache(String job) {
        return repo.findAverageScoresByJob(job).orElse(null);
    }

    @CacheEvict(value = "avgScoreByJob",
            key = "T(org.springframework.util.StringUtils).trimAllWhitespace(#interview.job == null ? '' : #interview.job).toUpperCase()")
    public void evictByInterview(Interview interview) { /* no-op */ }

}