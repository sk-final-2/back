// src/main/java/com/backend/recruitAi/interview/controller/MediaController.java
package com.backend.recruitAi.interview.controller;

import com.backend.recruitAi.media.LocalMediaStore;
import com.backend.recruitAi.media.MediaStore;
import com.backend.recruitAi.media.S3MediaStore;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.http.MediaTypeFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/interview")
public class MediaController {

    private final MediaStore mediaStore;

    @GetMapping("/media")
    public ResponseEntity<?> getMedia(
            @RequestParam("interviewId") String interviewId,
            @RequestParam("seq") int seq
    ) throws IOException {

        // 1) 운영: S3 presigned GET으로 리다이렉트
        if (mediaStore instanceof S3MediaStore s3) {
            String url = s3.presignedGet(interviewId, seq, java.time.Duration.ofMinutes(10));
            return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT) // 307 권장
                    .location(URI.create(url))
                    .build(); // 리다이렉트에 Accept-Ranges 불필요
        }

        // 2) dev: Resource 그대로 내려주기 (원하면 StreamingResponseBody로도 가능)
        if (mediaStore instanceof LocalMediaStore local) {
            try {
                Resource res = local.resolve(interviewId, seq);
                MediaType type = MediaTypeFactory.getMediaType(res.getFilename())
                        .orElse(MediaType.valueOf("video/mp4"));

                return ResponseEntity.ok()
                        .contentType(type)
                        .contentLength(res.contentLength())
                        .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                        .header(HttpHeaders.CONTENT_DISPOSITION, contentDispositionInline(interviewId, seq))
                        .body(res);
            } catch (FileNotFoundException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }

        // 3) 혹시 다른 구현체면 스트리밍으로 대응
        try {
            StreamingResponseBody body = mediaStore.stream(interviewId, seq);
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf("video/mp4"))
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDispositionInline(interviewId, seq))
                    .body(body);
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    private String contentDispositionInline(String interviewId, int seq) {
        String name = interviewId + "_" + seq + ".mp4";
        String encoded = URLEncoder.encode(name, StandardCharsets.UTF_8).replace("+", "%20");
        return "inline; filename=\"" + name + "\"; filename*=UTF-8''" + encoded;
    }
}
