package com.backend.recruitAi.result.service;

import com.backend.recruitAi.result.entity.InterviewResult;
import com.backend.recruitAi.interview.entity.Interview;
import com.backend.recruitAi.result.repository.InterviewResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterviewResultService {
    private final InterviewResultRepository interviewResultRepository;

    @Transactional
    public InterviewResult saveAnswerAnalysis(InterviewResult interviewResult) {
        return interviewResultRepository.save(interviewResult);
    }

    public Optional<InterviewResult> getAnswerAnalysisById(Long id) {
        return interviewResultRepository.findById(id);
    }

    public List<InterviewResult> getAnswerAnalysesByInterviewResult(Interview interview) {
        return interviewResultRepository.findByInterview(interview);
    }

    @Transactional
    public void deleteAnswerAnalysis(Long id, Long memberId) {
        InterviewResult analysis = interviewResultRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("AnswerAnalysis with ID " + id + " not found."));

        if (!analysis.getInterview().getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("Invalid interview result ID or member mismatch.");
        }

        interviewResultRepository.deleteById(id);
    }
}