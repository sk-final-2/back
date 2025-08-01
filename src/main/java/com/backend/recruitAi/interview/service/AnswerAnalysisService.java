package com.backend.recruitAi.interview.service;

import com.backend.recruitAi.interview.entity.AnswerAnalysis;
import com.backend.recruitAi.interview.entity.InterviewResult;
import com.backend.recruitAi.interview.repository.AnswerAnalysisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnswerAnalysisService {
    private final AnswerAnalysisRepository answerAnalysisRepository;
    // 답변 분석 결과 저장
    @Transactional
    public AnswerAnalysis saveAnswerAnalysis(AnswerAnalysis answerAnalysis) {
        return answerAnalysisRepository.save(answerAnalysis);
    }
    // 모든 답변 분석 결과 조회
    public List<AnswerAnalysis> getAllAnswerAnalyses() {
        return answerAnalysisRepository.findAll();
    }
    // 특정 ID의 답변 분석 결과 조회
    public Optional<AnswerAnalysis> getAnswerAnalysisById(Long id) {
        return answerAnalysisRepository.findById(id);
    }
    // 특정 InterviewResult에 속한 모든 답변 분석 결과 조회
    public List<AnswerAnalysis> getAnswerAnalysesByInterviewResult(InterviewResult interviewResult) {
        return answerAnalysisRepository.findByInterviewResult(interviewResult);
    }
    @Transactional // D: 답변 분석 결과 삭제
    public void deleteAnswerAnalysis(Long id) {
        if (!answerAnalysisRepository.existsById(id)) {
            throw new NoSuchElementException("AnswerAnalysis with ID " + id + " not found.");
        }
        answerAnalysisRepository.deleteById(id);
    }
}
