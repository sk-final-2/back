package com.backend.recruitAi.interview.controller;

import com.backend.recruitAi.global.exception.BusinessException;
import com.backend.recruitAi.global.exception.ErrorCode;
import com.backend.recruitAi.global.response.ResponseDto;
import com.backend.recruitAi.interview.dto.AnswerResponseDto;
import com.backend.recruitAi.interview.dto.OcrResponseDto;
import com.backend.recruitAi.interview.service.EmotionService;
import com.backend.recruitAi.interview.dto.FirstQuestionRequestDto;
import com.backend.recruitAi.interview.dto.FirstQuestionResponseDto;
import com.backend.recruitAi.interview.service.FirstQuestionService;
import com.backend.recruitAi.interview.service.OcrService;
import com.backend.recruitAi.interview.service.RedisInterviewService;
import com.backend.recruitAi.interview.service.SttService;
import com.backend.recruitAi.member.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/interview")
public class InterviewAIController {

    private final OcrService ocrService;
    private final SttService sttService;
    private final EmotionService emotionService;
    private final RedisInterviewService redisInterviewService;
    private final FirstQuestionService firstQuestionService;

    @PostMapping("/ocr")
    public ResponseDto<OcrResponseDto> ocrFromFile(@RequestPart("file") MultipartFile file) {
        try {
            File tempFile = File.createTempFile("uploaded-", file.getOriginalFilename());
            file.transferTo(tempFile);

            String ocrResult = ocrService.sendFileToPython(tempFile);
            OcrResponseDto response = new OcrResponseDto(ocrResult);

            return ResponseDto.success("OCR 성공", response);

        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_PROCESSING_FAILED);
        }
    }

    @PostMapping(value = "/answer", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDto<AnswerResponseDto> handleAnswer(
            @RequestParam("file") MultipartFile file,
            @RequestParam("seq") int seq,
            @RequestParam("interviewId") String interviewId,
            @RequestParam("question") String question,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        File tempFile = null;
        File emotionTempFile = null;

        try {
            // 1. 원본 임시 파일 생성
            tempFile = File.createTempFile("upload_", ".mp4");
            file.transferTo(tempFile);

            // 2. Emotion용 임시 파일 따로 복사
            emotionTempFile = File.createTempFile("upload_emotion_", ".mp4");
            Files.copy(tempFile.toPath(), emotionTempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // 3. Emotion 서버 비동기 전송 (파일은 따로 보냄)
            File finalEmotionFile = emotionTempFile;
            emotionService.sendToEmotionServer(finalEmotionFile, interviewId, seq)
                    .doFinally(signal -> {
                        // 비동기 끝난 후 emotion용 파일 삭제
                        if (finalEmotionFile.exists()) finalEmotionFile.delete();
                    })
                    .subscribe(emoRes -> {
                        redisInterviewService.savePartialEmotion(interviewId, seq, emoRes);
                    });

            // 4. STT 서버 요청 (원본 파일 사용)
            Map<String, Object> sttRes = sttService.sendToSttServer(tempFile, interviewId, seq)
                    .doOnNext(res -> redisInterviewService.savePartialSTT(interviewId, seq, res, question))
                    .block();

            return ResponseDto.success(new AnswerResponseDto(
                    interviewId,
                    (String) sttRes.get("new_question")
            ));

        } catch (Exception e) {
            return ResponseDto.error(ErrorCode.INTERNAL_SERVER_ERROR);
        } finally {
            if (tempFile != null && tempFile.exists()) tempFile.delete();
        }
    }

    @PostMapping("/first-question")
    public ResponseDto<FirstQuestionResponseDto> getFirstQuestion(
            @RequestBody FirstQuestionRequestDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        FirstQuestionResponseDto response = firstQuestionService.handleFirstQuestion(
                request,
                userDetails.getMember().getId()
        );
        return ResponseDto.success("첫 번째 질문 생성 성공", response);
    }
}