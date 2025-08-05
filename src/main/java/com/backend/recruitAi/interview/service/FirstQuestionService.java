package com.backend.recruitAi.interview.service;

import com.backend.recruitAi.config.FirstAskServerProperties;
import com.backend.recruitAi.global.exception.BusinessException;
import com.backend.recruitAi.global.exception.ErrorCode;
import com.backend.recruitAi.interview.dto.FirstAskPythonRequestDto;
import com.backend.recruitAi.interview.dto.FirstAskPythonResponseDto;
import com.backend.recruitAi.interview.dto.FirstQuestionRequestDto;
import com.backend.recruitAi.interview.dto.FirstQuestionResponseDto;
import com.backend.recruitAi.interview.entity.Interview;
import com.backend.recruitAi.member.entity.Member;
import com.backend.recruitAi.member.repository.MemberRepository;
import com.backend.recruitAi.result.repository.InterviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FirstQuestionService {

    private final FirstAskServerProperties firstAskServerProperties;
    private final RestTemplate restTemplate = new RestTemplate();
    private final InterviewRepository interviewRepository;
    private final MemberRepository memberRepository;

    public FirstQuestionResponseDto handleFirstQuestion(FirstQuestionRequestDto request, Long memberId) {
        try {
            // 1. UUID 생성
            String interviewId = UUID.randomUUID().toString();

            // 2. 현재 로그인한 회원 조회
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

            // 3. Interview 엔티티 생성 및 저장
            Interview interview = Interview.builder()
                    .member(member)
                    .uuid(interviewId)
                    .job(request.getJob())
                    .career(request.getCareer())
                    .type(request.getInterviewType())
                    .level(request.getLevel())
                    .language(request.getLanguage())
                    .build();

            interviewRepository.save(interview);

            // 4. Python 서버 전송용 DTO 생성
            FirstAskPythonRequestDto pythonRequest = new FirstAskPythonRequestDto(
                    request.getOcrText(),
                    request.getJob(),
                    interviewId,
                    request.getSeq(),
                    request.getCareer(),        // 경력
                    request.getInterviewType(), // 면접 유형
                    request.getLevel(),         // 난이도
                    request.getLanguage()       // 언어
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<FirstAskPythonRequestDto> requestEntity = new HttpEntity<>(pythonRequest, headers);

            // 5. Python 서버 호출
            ResponseEntity<FirstAskPythonResponseDto> response =
                    restTemplate.postForEntity(
                            firstAskServerProperties.getUrl(),
                            requestEntity,
                            FirstAskPythonResponseDto.class
                    );

            // 6. 응답 검증
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new BusinessException(ErrorCode.FIRST_QUESTION_FAILED);
            }

            FirstAskPythonResponseDto pythonResponse = response.getBody();

            // 7. 프론트 응답 변환
            return new FirstQuestionResponseDto(
                    pythonResponse.getInterviewId(),
                    pythonResponse.getInterviewQuestion(),
                    request.getSeq()
            );

        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.FIRST_QUESTION_FAILED);
        }
    }
}

