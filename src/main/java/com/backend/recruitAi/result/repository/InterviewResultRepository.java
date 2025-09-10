package com.backend.recruitAi.result.repository;

import com.backend.recruitAi.result.dto.AvgScoreDto;
import com.backend.recruitAi.result.dto.JobAvgScoreDto;
import com.backend.recruitAi.result.entity.InterviewResult;
import com.backend.recruitAi.interview.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface InterviewResultRepository extends JpaRepository<InterviewResult, Long> {
    List<InterviewResult> findByInterview(Interview interview);
    List<InterviewResult> findAllByInterview(Interview interview);

    //전체 평균 점수 구하기
    @Query("SELECT new com.backend.recruitAi.result.dto.AvgScoreDto(" +
            "AVG(ir.score) as score, " +
            "AVG(ir.emotion_score) as emotionScore, " +
            "AVG(ir.blink_score) as blinkScore, " +
            "AVG(ir.eye_score) as eyeScore, " +
            "AVG(ir.head_score) as headScore, " +
            "AVG(ir.hand_score) as handScore) " +
            "FROM InterviewResult ir")
    Optional<AvgScoreDto> findAllAverageScores(); // 매개변수가 필요 없습니다.

    //직무별 평균 점수 구하기
    @Query("SELECT new com.backend.recruitAi.result.dto.AvgScoreDto(" +
            "AVG(ir.score), " +
            "AVG(ir.emotion_score), " +
            "AVG(ir.blink_score), " +
            "AVG(ir.eye_score), " +
            "AVG(ir.head_score), " +
            "AVG(ir.hand_score)) " +
            "FROM InterviewResult ir " +
            "JOIN ir.interview i " +
            "WHERE i.job = :job")
    Optional<AvgScoreDto> findAverageScoresByJob(@Param("job") String job);

    // 여러 직무 평균 한 번에
    @Query("SELECT new com.backend.recruitAi.result.dto.JobAvgScoreDto(" +
            "UPPER(TRIM(i.job)), " +
            "AVG(ir.score), " +
            "AVG(ir.emotion_score), " +
            "AVG(ir.blink_score), " +
            "AVG(ir.eye_score), " +
            "AVG(ir.head_score), " +
            "AVG(ir.hand_score)) " +
            "FROM Interview i " +
            "LEFT JOIN i.answerAnalyses ir " +
            "WHERE UPPER(TRIM(i.job)) IN :jobs " +
            "GROUP BY UPPER(TRIM(i.job))")
    List<JobAvgScoreDto> findAverageScoresByJobs(@Param("jobs") Set<String> jobs);
}