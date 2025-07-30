package com.backend.recruitAi.video.controller;

import com.backend.recruitAi.config.SttServerProperties;
import com.backend.recruitAi.global.exception.ErrorCode;
import com.backend.recruitAi.global.response.ResponseDto; // ResponseDto 클래스의 정확한 경로
import com.backend.recruitAi.member.repository.MemberRepository; // MemberRepository 인터페이스의 정확한 경로
import com.backend.recruitAi.member.service.CustomUserDetails; // CustomUserDetails 클래스의 정확한 경로 (member.service 패키지에 있음)

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // Spring Security 관련 임포트
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/video")
public class VideoStreamController {

    private final MemberRepository memberRepository;
    private final SttServerProperties sttServerProperties;

    @PostMapping("/upload-and-forward")
    public ResponseDto<String> uploadAndForwardVideo(
            @RequestParam("file") MultipartFile multipartFile,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String userEmail = userDetails.getUsername(); // CustomUserDetails의 getUsername 메서드
        // userDetails.getMember().getId() 사용 시, CustomUserDetails 안에 getMember()가 Member 객체를 반환하는지 확인 필요
        Long memberId = userDetails.getMember().getId(); // CustomUserDetails에서 Member 객체를 통해 ID 가져오기

        System.out.println("🎥 Received video stream from user: " + userEmail + " (Member ID: " + memberId + ")");

        File tempFile = null;

        try {
            String originalFileName = multipartFile.getOriginalFilename();
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            tempFile = File.createTempFile("upload_", fileExtension);
            multipartFile.transferTo(tempFile);
            FileSystemResource resource = new FileSystemResource(tempFile);

            MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
            formData.add("file", resource);

            WebClient webClient = WebClient.create();

            String pythonResponse = webClient.post()
                    .uri(sttServerProperties.getUrl())
                    .header("X-User-Email", userEmail)
                    .header("X-Member-Id", String.valueOf(memberId))
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(formData))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println("✅ Video stream forwarded to STT server. STT Server Response: " + pythonResponse);
            return ResponseDto.success(pythonResponse); // ResponseDto.success() 사용

        } catch (IOException e) {
            System.err.println("❌ Error processing video file: " + e.getMessage());
            return ResponseDto.error(ErrorCode.FILE_PROCESSING_ERROR);
        } catch (Exception e) {
            System.err.println("❌ Error forwarding video stream for user " + userEmail + ": " + e.getMessage());
            return ResponseDto.error(ErrorCode.STT_PROCESSING_FAILED);
        } finally {
            if (tempFile != null && tempFile.exists()) {
                if (tempFile.delete()) {
                    System.out.println("🗑️ Temporary file deleted: " + tempFile.getAbsolutePath());
                } else {
                    System.err.println("⚠️ Failed to delete temporary file: " + tempFile.getAbsolutePath());
                }
            }
        }
    }
}
