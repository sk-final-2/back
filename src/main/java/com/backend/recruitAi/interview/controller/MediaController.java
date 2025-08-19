// src/main/java/com/backend/recruitAi/interview/controller/MediaController.java
package com.backend.recruitAi.interview.controller;

import com.backend.recruitAi.global.response.ResponseDto;
import com.backend.recruitAi.interview.service.TempMediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interview/media")
@RequiredArgsConstructor
public class MediaController {

    private final TempMediaService tempMediaService;

    /** 인터뷰 영상 조회: (interviewId, seq)만으로 원본 영상 반환 */
    @GetMapping
    public ResponseEntity<Resource> getMedia(
            @RequestParam String interviewId,
            @RequestParam int seq
    ) {
        Resource res = tempMediaService.getOriginal(interviewId, seq);
        if (res == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("video/mp4"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION,
                "inline; filename=\"" + interviewId + "_" + seq + ".mp4\"");

        return new ResponseEntity<>(res, headers, HttpStatus.OK);
    }

    /** 프론트가 ‘결과 다 봄’ 알리면 즉시 삭제 */
    @PostMapping("/ack")
    public ResponseDto<?> ackAndDelete(
            @RequestParam String interviewId,
            @RequestParam int seq
    ) {
        // 서비스 메서드명과 맞춤
        tempMediaService.ackAndDelete(interviewId, seq);
        return ResponseDto.success("영상 삭제 완료");
    }
}
