package com.backend.recruitAi.interview.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class EvaluateService {

    @Value("${evaluate.server.url}")
    private String evaluateUrl; // 예: http://evaluate-ai.recruitai.local:8002/evaluate

    private final WebClient webClient;

    public Mono<EvaluateResponse> send(String question, String answer) {
        EvaluateRequest req = new EvaluateRequest(question, answer);

        return webClient.post()
                .uri(evaluateUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .retrieve()
                .bodyToMono(EvaluateResponse.class)
                // 타임아웃/재시도(429/5xx만 재시도)
                .timeout(Duration.ofSeconds(15))
                .retryWhen(Retry.backoff(2, Duration.ofMillis(300))
                        .filter(this::isRetryable))
                // 실패 시 기본 응답으로 복구(서비스 전체 장애 방지)
                .onErrorResume(ex -> Mono.just(EvaluateResponse.failed("평가 실패: " + ex.getMessage())));
    }

    private boolean isRetryable(Throwable t) {
        if (t instanceof WebClientResponseException w) {
            int s = w.getStatusCode().value();
            return s == 429 || (s >= 500 && s < 600);
        }
        // 네트워크/타임아웃 등도 재시도
        return true;
    }

    // ===== DTOs =====
    record EvaluateRequest(String question, String answer) {}

    @Data
    public static class EvaluateResponse {
        private double score;
        private String feedback;
        private String improve;

        public static EvaluateResponse failed(String msg) {
            EvaluateResponse r = new EvaluateResponse();
            r.score = 0.0;
            r.feedback = msg;
            r.improve = "";
            return r;
        }
    }
}
