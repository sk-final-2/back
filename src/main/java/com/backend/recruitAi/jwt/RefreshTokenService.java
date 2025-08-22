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

    private static final String OTT_PREFIX = "ott:";
    private static final long OTT_TTL_SECONDS = 120; // 2분 유효

    /** 모바일 성공 콜백에서 {accessToken, rtid, profile} JSON을 1회용 키에 저장 */
    public void saveOtt(String ott, String payloadJson) {
        redisTemplate.opsForValue().set(OTT_PREFIX + ott, payloadJson, OTT_TTL_SECONDS, TimeUnit.SECONDS);
    }

    /** 앱에서 교환 시 한 번만 꺼내고 즉시 삭제 */
    public String consumeOtt(String ott) {
        String key = OTT_PREFIX + ott;
        String val = redisTemplate.opsForValue().get(key);
        if (val != null) redisTemplate.delete(key);
        return val;
    }
}
