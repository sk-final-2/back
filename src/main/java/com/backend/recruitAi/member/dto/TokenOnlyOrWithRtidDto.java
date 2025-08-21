package com.backend.recruitAi.member.dto;

public class TokenOnlyOrWithRtidDto {
    private final String accessToken;
    private final String rtid;
    public TokenOnlyOrWithRtidDto(String accessToken, String rtid) {
        this.accessToken = accessToken; this.rtid = rtid;
    }
    public String getAccessToken() { return accessToken; }
    public String getRtid() { return rtid; }
}
