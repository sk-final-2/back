package com.backend.recruitAi.interview.service;

import com.backend.recruitAi.interview.entity.InterviewResult;
import com.backend.recruitAi.interview.repository.InterviewResultRepository;
import com.backend.recruitAi.member.entity.Member; // Member 엔티티 임포트
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

    // 인터뷰 결과 저장
    @Transactional
    public InterviewResult saveInterviewResult(InterviewResult interviewResult) {
        return interviewResultRepository.save(interviewResult);
    }
    // 인터뷰 결과 조회
    public List<InterviewResult> getAllInterviewResults() {
        return interviewResultRepository.findAll();
    }
    //특정 ID의 인터뷰 결과 조회
    public Optional<InterviewResult> getInterviewResultById(Long id) {
        return interviewResultRepository.findById(id);
    }
    //특정 멤버의 모든 인터뷰 결과 조회
    public List<InterviewResult> getInterviewResultsByMember(Member member) {
        return interviewResultRepository.findByMember(member);
    }

    @Transactional //인터뷰 결과 삭제
    public void deleteInterviewResult(Long id) {
        if (!interviewResultRepository.existsById(id)) {
            throw new NoSuchElementException("InterviewResult with ID " + id + " not found.");
        }
        interviewResultRepository.deleteById(id);
    }
}
