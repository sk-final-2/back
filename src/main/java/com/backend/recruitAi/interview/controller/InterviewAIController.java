package com.backend.recruitAi.interview.controller;

import com.backend.recruitAi.global.exception.BusinessException;
import com.backend.recruitAi.global.exception.ErrorCode;
import com.backend.recruitAi.global.response.ResponseDto;
import com.backend.recruitAi.interview.dto.*;
import com.backend.recruitAi.interview.service.*;
import com.backend.recruitAi.interview.redis.RedisInterviewService;
import com.backend.recruitAi.member.service.CustomUserDetails;
import com.backend.recruitAi.result.dto.InterviewResponseDto;
import com.backend.recruitAi.result.dto.InterviewResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.time.Duration;
import com.backend.recruitAi.interview.service.TempMediaService;

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
    private final ResultService resultService;
    private final TrackingService trackingService;
    private final TempMediaService tempMediaService;

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
    public ResponseDto<?> handleAnswer(
            @RequestParam("file") MultipartFile file,
            @RequestParam("seq") int seq,
            @RequestParam("interviewId") String interviewId,
            @RequestParam("question") String question,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null || userDetails.getMember() == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        File tempFile = null;
        File emotionTempFile = null;
        File trackingTempFile = null;

        boolean savedToMedia = false; // 보관 성공 여부 플래그

        try {
            // 1) 원본 임시 파일 생성
            tempFile = File.createTempFile("upload_", ".mp4");
            file.transferTo(tempFile);

            // 2) 감정/트래킹용 복사본 생성
//            emotionTempFile = File.createTempFile("upload_emotion_", ".mp4");
//            Files.copy(tempFile.toPath(), emotionTempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
//
//            trackingTempFile = File.createTempFile("upload_tracking_", ".mp4");
//            Files.copy(tempFile.toPath(), trackingTempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
//
//            // 3-1) Emotion 서버 비동기 전송
//            File finalEmotionFile = emotionTempFile;
//            emotionService.sendToEmotionServer(finalEmotionFile, interviewId, seq)
//                    .doFinally(signal -> {
//                        if (finalEmotionFile.exists()) finalEmotionFile.delete();
//                    })
//                    .subscribe(emoRes -> redisInterviewService.savePartialEmotion(interviewId, seq, emoRes));
//
//            // 3-2) Tracking 서버 비동기 전송
//            File finalTrackingFile = trackingTempFile;
//            trackingService.sendToTrackingServer(finalTrackingFile, interviewId, seq)
//                    .doFinally(signal -> {
//                        if (finalTrackingFile.exists()) finalTrackingFile.delete();
//                    })
//                    .subscribe(traRes -> redisInterviewService.savePartialTracking(interviewId, seq, traRes));
//
//            // 4) STT 서버 요청 (원본 사용)
//            Map<String, Object> sttRes = sttService.sendToSttServer(tempFile, interviewId, seq)
//                    .doOnNext(res -> redisInterviewService.savePartialSTT(interviewId, seq, res, question))
//                    .block();

            // 5) ★정상 흐름에서만 원본 영상 임시보관에 저장
            tempMediaService.save(tempFile, interviewId, seq);
            savedToMedia = true; // 보관 성공

//            return ResponseDto.success(new AnswerResponseDto(
//                    interviewId,
//                    (String) sttRes.get("new_question"),
//                    Boolean.parseBoolean(String.valueOf(sttRes.get("keepGoing")))
//            ));
            return ResponseDto.success("");

        } catch (Exception e) {
            // 실패 시 에러 반환
            return ResponseDto.error(ErrorCode.INTERNAL_SERVER_ERROR);
        } finally {
            // 실패했거나 보관하지 못했으면 원본 임시파일 정리
            if (!savedToMedia && tempFile != null && tempFile.exists()) {
                try { Files.deleteIfExists(tempFile.toPath()); } catch (Exception ignore) {}
            }
            // emotionTempFile / trackingTempFile은 각 doFinally에서 삭제됨
        }
    }

    @PostMapping("/first-question")
    public ResponseDto<FirstQuestionResponseDto> getFirstQuestion(
            @RequestBody FirstQuestionRequestDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null || userDetails.getMember() == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        FirstQuestionResponseDto response = firstQuestionService.handleFirstQuestion(
                request,
                userDetails.getMember().getId()
        );
        return ResponseDto.success("첫 번째 질문 생성 성공", response);
    }


    private final RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/end")
    public ResponseDto<?> endInterview(@RequestBody InterviewEndRequestDto interviewEndRequestDto) {
        redisTemplate.opsForValue().set("interview:" + interviewEndRequestDto.getInterviewId() + ":lastSeq", interviewEndRequestDto.getLastSeq());
        redisTemplate.expire("interview:" + interviewEndRequestDto.getInterviewId() + ":lastSeq", Duration.ofHours(1));
        redisInterviewService.tryPublishIfComplete(interviewEndRequestDto.getInterviewId());
        return ResponseDto.success("면접 종료. 분석 대기 중");

    }
    @PostMapping("/result")
    public ResponseDto<InterviewResponseDto> getInterviewResult(@RequestBody InterviewEndRequestDto interviewEndRequestDto, @AuthenticationPrincipal CustomUserDetails userDetails){

        if (userDetails == null || userDetails.getMember() == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        InterviewResponseDto interviewResponseDto = resultService.saveAndGetInterviewResult(interviewEndRequestDto.getInterviewId(),userDetails.getMember().getId());
        return ResponseDto.success(interviewResponseDto);
    }

}