package com.backend.recruitAi.interview.service;

import com.backend.recruitAi.config.FirstAskServerProperties;
import com.backend.recruitAi.global.exception.BusinessException;
import com.backend.recruitAi.global.exception.ErrorCode;
import com.backend.recruitAi.interview.dto.FirstAskPythonRequestDto;
import com.backend.recruitAi.interview.dto.FirstAskPythonResponseDto;
import com.backend.recruitAi.interview.dto.FirstQuestionRequestDto;
import com.backend.recruitAi.interview.dto.FirstQuestionResponseDto;
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

    public FirstQuestionResponseDto handleFirstQuestion(FirstQuestionRequestDto request) {
        try {
            // 1. UUID 생성
            String interviewId = UUID.randomUUID().toString();

            // 2. Python 서버 전송용 DTO 생성
            FirstAskPythonRequestDto pythonRequest = new FirstAskPythonRequestDto(
                    request.getOcrText(),
                    request.getJob(),
                    interviewId,
                    request.getSeq()
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<FirstAskPythonRequestDto> requestEntity = new HttpEntity<>(pythonRequest, headers);

            // 3. Python 서버 호출
            ResponseEntity<FirstAskPythonResponseDto> response =
                    restTemplate.postForEntity(
                            firstAskServerProperties.getUrl(),
                            requestEntity,
                            FirstAskPythonResponseDto.class
                    );

            // 4. 응답 검증
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new BusinessException(ErrorCode.FIRST_QUESTION_FAILED);
            }

            FirstAskPythonResponseDto pythonResponse = response.getBody();

            // 5. 프론트 응답 변환
            return new FirstQuestionResponseDto(
                    pythonResponse.getInterviewId(),
                    pythonResponse.getInterviewQuestion(),
                    request.getSeq()
            );

        } catch (Exception e) {
            throw new BusinessException(ErrorCode.FIRST_QUESTION_FAILED);
        }
    }
}

