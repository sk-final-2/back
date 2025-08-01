package com.backend.recruitAi.interview.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SttService {

    @Value("${stt.server.url}")
    private String sttUrl;  // 예: http://interview-ai:8001/stt-ask

    private final WebClient webClient;

    public Mono<Map<String, Object>> sendToSttServer(File file, String interviewId, int seq) {
        FileSystemResource resource = new FileSystemResource(file);

        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("file", resource);
        formData.add("interviewId", interviewId);
        formData.add("seq", seq);

        return webClient.post()
                .uri(sttUrl)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(formData))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {});
    }
}
