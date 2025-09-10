// S3Config.java  (prod)
package com.backend.recruitAi.media.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@Profile("prod")
public class S3Config {
    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(System.getenv().getOrDefault("AWS_REGION", "ap-northeast-2")))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .region(Region.of(System.getenv().getOrDefault("AWS_REGION","ap-northeast-2")))
                .build();
    }
}
