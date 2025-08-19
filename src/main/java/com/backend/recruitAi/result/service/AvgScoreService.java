// AvgScoreService.java
package com.backend.recruitAi.result.service;

import com.backend.recruitAi.result.dto.AvgScoreDto;
import com.backend.recruitAi.result.entity.InterviewResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AvgScoreService {

    // 평균점수계산로직
//    public AvgScoreDto calculateAverageScores(List<InterviewResult> results) {
//        if (results == null || results.isEmpty()) {
//            return new AvgScoreDto(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
//        }
//
//        long totalScore = results.stream().mapToInt(InterviewResult::getScore).sum();
//        long totalEmotionScore = results.stream().mapToInt(InterviewResult::getEmotion_score).sum();
//        long totalBlinkScore = results.stream().mapToInt(InterviewResult::getBlink_score).sum();
//        long totalEyeScore = results.stream().mapToInt(InterviewResult::getEye_score).sum();
//        long totalHeadScore = results.stream().mapToInt(InterviewResult::getHead_score).sum();
//        long totalHandScore = results.stream().mapToInt(InterviewResult::getHand_score).sum();
//        int resultCount = results.size();
//
//        return new AvgScoreDto(
//                 ((totalScore / resultCount),
//                 (totalEmotionScore / resultCount),
//                (totalBlinkScore / resultCount),
//                 (totalEyeScore / resultCount),
//                 (totalHeadScore / resultCount),
//                 (totalHandScore / resultCount)
//        );
//    }
}