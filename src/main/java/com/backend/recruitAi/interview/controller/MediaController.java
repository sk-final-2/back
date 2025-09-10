// MediaController.java  (기존 파일 대체)
package com.backend.recruitAi.interview.controller;

import com.backend.recruitAi.global.response.ResponseDto;
import com.backend.recruitAi.media.MediaStore;
import com.backend.recruitAi.media.S3MediaStore;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;

@RestController
@RequestMapping("/api/interview/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaStore mediaStore;

    @GetMapping
    public ResponseEntity<?> getMedia(@RequestParam String interviewId, @RequestParam int seq) throws IOException {
        // prod: S3 프리사인 302 리다이렉트
        if (mediaStore instanceof S3MediaStore s3Store) {
            String url = s3Store.presignedGet(interviewId, seq, java.time.Duration.ofMinutes(10));
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, url)
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .build(); // ResponseEntity<Void>
        }

        // dev: 로컬 스트리밍
        StreamingResponseBody body = mediaStore.stream(interviewId, seq);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("video/mp4"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"%s_%d.mp4\"".formatted(interviewId, seq))
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .body(body); // ResponseEntity<StreamingResponseBody>
    }

    @PostMapping("/ack")
    public ResponseDto<?> ackAndDelete(
            @RequestParam String interviewId,
            @RequestParam int seq
    ) {
        mediaStore.ackAndDelete(interviewId, seq);
        return ResponseDto.success("영상 삭제 완료");
    }
}
