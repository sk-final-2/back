package com.backend.recruitAi.video.controller;

import com.backend.recruitAi.config.SttServerProperties;
import com.backend.recruitAi.global.exception.ErrorCode;
import com.backend.recruitAi.global.response.ResponseDto;
import com.backend.recruitAi.member.repository.MemberRepository;
import com.backend.recruitAi.member.service.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/video")
public class VideoStreamController {

    private final MemberRepository memberRepository;
    private final SttServerProperties sttServerProperties;
    private final ObjectMapper objectMapper;
    private final WebClient.Builder webClientBuilder;

    @PostMapping("/upload-and-forward")
    public ResponseDto<String> uploadAndForwardVideo(
            @RequestParam("file") MultipartFile multipartFile,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String userEmail = userDetails.getUsername();
        Long memberId = userDetails.getMember().getId();

        System.out.println("🎥 Received video stream from user: " + userEmail + " (Member ID: " + memberId + ")");

        File tempFile = null;
        String pythonResponseJsonString = null;
        String sttTextResult = null;

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

            WebClient webClient = webClientBuilder.baseUrl(sttServerProperties.getUrl()).build();
            System.out.println(sttServerProperties.getUrl());
            pythonResponseJsonString = webClient.post()
                    .uri("")
                    .header("X-User-Email", userEmail)
                    .header("X-Member-Id", String.valueOf(memberId))
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(formData))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println("✅ Video stream forwarded to STT server. STT Server Raw Response: " + pythonResponseJsonString);

            // 추가한부분/여기부터 (JSON 파싱 및 STT 텍스트 추출 로직)
            if (pythonResponseJsonString != null && !pythonResponseJsonString.isEmpty()) {
                JsonNode rootNode = objectMapper.readTree(pythonResponseJsonString);
                if (rootNode.has("text")) {
                    sttTextResult = rootNode.get("text").asText();
                    System.out.println("✅ Parsed STT Text Result: " + sttTextResult);

                } else {
                    System.err.println("⚠️ STT server response did not contain 'text' field. Response: " + pythonResponseJsonString);
                    return ResponseDto.error(ErrorCode.STT_PROCESSING_FAILED);
                }
            } else {
                System.err.println("⚠️ STT server returned an empty or null response.");
                return ResponseDto.error(ErrorCode.STT_PROCESSING_FAILED);
            }
            // /여기까지 (JSON 파싱 및 STT 텍스트 추출 로직 끝)

            return ResponseDto.success("Video stream forwarded and STT processed successfully. STT Result: " + sttTextResult);

        } catch (IOException e) {
            System.err.println("❌ Error processing video file: " + e.getMessage());
            return ResponseDto.error(ErrorCode.FILE_PROCESSING_ERROR);
        } catch (Exception e) {
            System.err.println("❌ Error forwarding video stream or parsing STT response for user " + userEmail + ": " + e.getMessage());
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
