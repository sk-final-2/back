package com.backend.recruitAi.result.service;

import com.backend.recruitAi.result.entity.AnswerAnalysis;
import com.backend.recruitAi.result.entity.InterviewResult;
import com.backend.recruitAi.result.repository.AnswerAnalysisRepository;
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

    @Transactional
    public AnswerAnalysis saveAnswerAnalysis(AnswerAnalysis answerAnalysis) {
        return answerAnalysisRepository.save(answerAnalysis);
    }

    public Optional<AnswerAnalysis> getAnswerAnalysisById(Long id) {
        return answerAnalysisRepository.findById(id);
    }

    public List<AnswerAnalysis> getAnswerAnalysesByInterviewResult(InterviewResult interviewResult) {
        return answerAnalysisRepository.findByInterviewResult(interviewResult);
    }

    @Transactional
    public void deleteAnswerAnalysis(Long id, Long memberId) {
        AnswerAnalysis analysis = answerAnalysisRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("AnswerAnalysis with ID " + id + " not found."));

        if (!analysis.getInterviewResult().getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("Invalid interview result ID or member mismatch.");
        }

        answerAnalysisRepository.deleteById(id);
    }
}