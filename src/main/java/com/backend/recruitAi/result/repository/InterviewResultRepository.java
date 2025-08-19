package com.backend.recruitAi.result.repository;

import com.backend.recruitAi.result.dto.AvgScoreDto;
import com.backend.recruitAi.result.entity.InterviewResult;
import com.backend.recruitAi.interview.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewResultRepository extends JpaRepository<InterviewResult, Long> {
    List<InterviewResult> findByInterview(Interview interview);
    List<InterviewResult> findAllByInterview(Interview interview);

    @Query("SELECT new com.backend.recruitAi.result.dto.AvgScoreDto(" +
            "AVG(ir.score) as score, " +
            "AVG(ir.emotion_score) as emotionScore, " +
            "AVG(ir.blink_score) as blinkScore, " +
            "AVG(ir.eye_score) as eyeScore, " +
            "AVG(ir.head_score) as headScore, " +
            "AVG(ir.hand_score) as handScore) " +
            "FROM InterviewResult ir")
    Optional<AvgScoreDto> findAllAverageScores(); // 매개변수가 필요 없습니다.
}