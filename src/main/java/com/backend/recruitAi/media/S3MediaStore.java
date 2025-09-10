// S3MediaStore.java  (prod)
package com.backend.recruitAi.media;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;

@Service
@Profile("prod")
@RequiredArgsConstructor
@Slf4j
public class S3MediaStore implements MediaStore {

    private final S3Client s3;
    private final S3Presigner presigner;
    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.prefix.tmp:tmp}")
    private String prefixTmp;

    @Override
    public SaveResult save(File src, String interviewId, int seq) throws IOException {
        String key = "%s/%s/%d.mp4".formatted(prefixTmp, interviewId, seq);
        long size = Files.size(src.toPath());
        PutObjectRequest put = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType("video/mp4")
                .serverSideEncryption(ServerSideEncryption.AES256) // 필요 시 KMS로 변경
                .build();
        s3.putObject(put, RequestBody.fromFile(src.toPath()));
        String etag = s3.headObject(HeadObjectRequest.builder().bucket(bucket).key(key).build()).eTag();
        log.info("[S3MediaStore] uploaded s3://{}/{}", bucket, key);
        // 로컬 임시파일은 호출측에서 정리
        return new SaveResult(key, size, etag);
    }

    public String presignedGet(String interviewId, int seq, Duration ttl) {
        String key = "%s/%s/%d.mp4".formatted(prefixTmp, interviewId, seq);
        var req = GetObjectRequest.builder().bucket(bucket).key(key).build();
        var presign = GetObjectPresignRequest.builder()
                .signatureDuration(ttl)
                .getObjectRequest(req)
                .build();
        return presigner.presignGetObject(presign).url().toString();
    }

    @Override
    public StreamingResponseBody stream(String interviewId, int seq) {
        String key = "%s/%s/%d.mp4".formatted(prefixTmp, interviewId, seq);
        // 필요 시 media/ 경로도 조회하도록 분기 가능
        return out -> {
            try (var in = s3.getObject(GetObjectRequest.builder()
                    .bucket(bucket).key(key).build())) {
                in.transferTo(out);
            }
        };
    }

    @Override
    public void ackAndDelete(String interviewId, int seq) {
        String key = "%s/%s/%d.mp4".formatted(prefixTmp, interviewId, seq);
        try {
            s3.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
            log.info("[S3MediaStore] deleted s3://{}/{}", bucket, key);
        } catch (S3Exception e) {
            log.warn("[S3MediaStore] delete failed {}: {}", key, e.awsErrorDetails().errorMessage());
        }
    }
}
