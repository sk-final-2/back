// src/main/java/com/backend/recruitAi/media/LocalMediaStore.java
package com.backend.recruitAi.media;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.nio.file.*;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class LocalMediaStore implements MediaStore {

    @Value("${app.temp-media.dir:./temp-media}")
    private String tempDir;

    @Value("${app.temp-media.ttl-seconds:1800}")
    private long ttlSeconds;

    private record Meta(String path, long expiresAt) {}
    private final Map<String, Meta> meta = new ConcurrentHashMap<>();

    private String key(String interviewId, int seq) { return "media:%s:%d".formatted(interviewId, seq); }
    private void ensureDir() throws IOException { Files.createDirectories(Path.of(tempDir)); }

    @Override
    public SaveResult save(File src, String interviewId, int seq) throws IOException {
        ensureDir();
        String filename = "%s_%d_%d.mp4".formatted(interviewId, seq, System.currentTimeMillis());
        Path target = Path.of(tempDir, filename);
        try {
            Files.move(src.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            Files.copy(src.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
        }
        long expires = Instant.now().getEpochSecond() + ttlSeconds;
        meta.put(key(interviewId, seq), new Meta(target.toString(), expires));
        log.info("[LocalMediaStore] saved {}", target);
        long size = Files.size(target);
        return new SaveResult(target.toString(), size, null);
    }

    // ✅ dev에서 바로 스트리밍이 필요할 때 사용
    @Override
    public StreamingResponseBody stream(String interviewId, int seq) throws IOException {
        Meta m = meta.get(key(interviewId, seq));
        if (m == null) throw new FileNotFoundException("media not found");
        File f = new File(m.path);
        if (!f.exists()) {
            meta.remove(key(interviewId, seq));
            throw new FileNotFoundException("media not found");
        }
        return os -> {
            try (InputStream in = new BufferedInputStream(new FileInputStream(f))) {
                in.transferTo(os);
            }
        };
    }

    // ✅ dev에서 Resource 그대로 내려보내고 싶을 때 사용할 헬퍼
    public Resource resolve(String interviewId, int seq) throws IOException {
        Meta m = meta.get(key(interviewId, seq));
        if (m == null) throw new FileNotFoundException("media not found");
        File f = new File(m.path);
        if (!f.exists()) throw new FileNotFoundException("media not found");
        return new FileSystemResource(f);
    }

    @Override
    public void ackAndDelete(String interviewId, int seq) {
        Meta m = meta.remove(key(interviewId, seq));
        if (m == null) return;
        try { Files.deleteIfExists(Path.of(m.path)); } catch (IOException ignored) {}
        log.info("[LocalMediaStore] deleted {}", m.path);
    }

    @Scheduled(fixedDelay = 300_000)
    public void sweep() {
        long now = Instant.now().getEpochSecond();
        meta.entrySet().removeIf(e -> {
            Meta m = e.getValue();
            if (m.expiresAt <= now) {
                try { Files.deleteIfExists(Path.of(m.path)); } catch (IOException ignored) {}
                log.info("[LocalMediaStore] expired -> {}", m.path);
                return true;
            }
            return false;
        });
    }
}
