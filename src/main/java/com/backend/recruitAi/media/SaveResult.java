package com.backend.recruitAi.media;

public record SaveResult(String location, long size, String etag) {
    // location: dev=파일 절대경로, prod=S3 key (ex: tmp/{interviewId}/{seq}.mp4)
}