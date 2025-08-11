package com.backend.recruitAi.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final StringRedisTemplate redisTemplate;
    private static final long REFRESH_EXPIRATION = 60 * 60 * 24 * 7; // 7일

    private String key(String rtid) { return "rt:" + rtid; }

    public void saveRefreshToken(String rtid, String refreshToken) {
        redisTemplate.opsForValue().set(key(rtid), refreshToken, REFRESH_EXPIRATION, TimeUnit.SECONDS);
    }

    public String getRefreshToken(String rtid) {
        return redisTemplate.opsForValue().get(key(rtid));
    }

    public void deleteRefreshToken(String rtid) {
        redisTemplate.delete(key(rtid));
    }
}
