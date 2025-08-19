// src/main/java/com/backend/recruitAi/interview/service/TempMediaService.java
package com.backend.recruitAi.interview.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class TempMediaService {

    @Value("${app.temp-media.dir:./temp-media}")
    private String tempDir;

    @Value("${app.temp-media.ttl-seconds:1800}") // 기본 30분
    private long ttlSeconds;

    // 간단히 In-Memory Map 사용 (운영에서 필요하면 Redis 등으로 교체 가능)
    private final Map<String, Meta> metaMap = new ConcurrentHashMap<>();

    private record Meta(String path, long expiresAtEpochSec) {}

    private String key(String interviewId, int seq) {
        return "media:%s:%d".formatted(interviewId, seq);
    }

    private void ensureDir() throws IOException {
        Files.createDirectories(Path.of(tempDir));
    }

    /**
     * 단일(원본) 영상 저장.
     * 같은 파일시스템이면 move, 아니면 copy로 보관 디렉토리에 저장하고 TTL 기록.
     */
    public Path save(File src, String interviewId, int seq) throws IOException {
        ensureDir();

        String filename = "%s_%d_%d.mp4".formatted(
                interviewId, seq, System.currentTimeMillis());
        Path target = Path.of(tempDir, filename);

        try {
            Files.move(src.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception moveFail) {
            Files.copy(src.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
        }

        long expiresAt = Instant.now().getEpochSecond() + ttlSeconds;
        metaMap.put(key(interviewId, seq), new Meta(target.toString(), expiresAt));
        log.info("[TempMedia] saved -> {}", target);
        return target;
    }

    /** 단일(원본) 영상 조회 */
    public Resource getOriginal(String interviewId, int seq) {
        Meta meta = metaMap.get(key(interviewId, seq));
        if (meta == null) return null;

        File f = new File(meta.path);
        if (!f.exists()) {
            metaMap.remove(key(interviewId, seq));
            return null;
        }
        return new FileSystemResource(f);
    }

    /** 프론트가 ‘다 봄’ 알리면 즉시 삭제 */
    public void ackAndDelete(String interviewId, int seq) {
        String k = key(interviewId, seq);
        Meta meta = metaMap.remove(k);
        if (meta == null) return;

        try {
            Files.deleteIfExists(Path.of(meta.path));
            log.info("[TempMedia] deleted {}", meta.path);
        } catch (IOException e) {
            log.warn("[TempMedia] delete failed {}: {}", meta.path, e.getMessage());
        }
    }

    /** 5분마다 만료 파일 정리 */
    @Scheduled(fixedDelay = 300_000)
    public void sweepExpired() {
        long now = Instant.now().getEpochSecond();
        metaMap.entrySet().removeIf(e -> {
            Meta m = e.getValue();
            if (m.expiresAtEpochSec <= now) {
                try { Files.deleteIfExists(Path.of(m.path)); }
                catch (IOException ignored) {}
                log.info("[TempMedia] expired -> deleted {}", m.path);
                return true;
            }
            return false;
        });
    }
}
