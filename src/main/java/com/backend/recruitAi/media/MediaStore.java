package com.backend.recruitAi.media;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.IOException;

public interface MediaStore {
    SaveResult save(File src, String interviewId, int seq) throws IOException;
    StreamingResponseBody stream(String interviewId, int seq) throws IOException; // 비디오 스트리밍용
    void ackAndDelete(String interviewId, int seq);
}