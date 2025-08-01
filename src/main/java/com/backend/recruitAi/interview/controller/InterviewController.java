package com.backend.recruitAi.interview.controller;

import com.backend.recruitAi.global.exception.BusinessException;
import com.backend.recruitAi.global.exception.ErrorCode;
import com.backend.recruitAi.global.response.ResponseDto;
import com.backend.recruitAi.interview.dto.FirstQuestionRequestDto;
import com.backend.recruitAi.interview.dto.FirstQuestionResponseDto;
import com.backend.recruitAi.interview.dto.OcrResponseDto;
import com.backend.recruitAi.interview.service.FirstQuestionService;
import com.backend.recruitAi.interview.service.OcrService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/interview")
public class InterviewController {

    private final OcrService ocrService;
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

    @PostMapping("/first-question")
    public ResponseDto<FirstQuestionResponseDto> getFirstQuestion(
            @RequestBody FirstQuestionRequestDto request) {
        FirstQuestionResponseDto response = firstQuestionService.handleFirstQuestion(request);
        return ResponseDto.success("첫 번째 질문 생성 성공", response);
    }
}