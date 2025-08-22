package com.backend.recruitAi.security;

import com.backend.recruitAi.jwt.JwtTokenProvider;
import com.backend.recruitAi.jwt.RefreshTokenService;
import com.backend.recruitAi.member.entity.Member;
import com.backend.recruitAi.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static com.backend.recruitAi.member.controller.OAuthBridgeController.ATTR_REDIRECT;
import static com.backend.recruitAi.member.controller.OAuthBridgeController.ATTR_TARGET;


@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Autowired private JwtTokenProvider jwtTokenProvider;
    @Autowired private MemberRepository memberRepository;
    @Autowired private RefreshTokenService refreshTokenService;
    @Autowired private ObjectMapper objectMapper;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    private static final SecureRandom RNG = new SecureRandom();
    private String newRtid() { byte[] b=new byte[32]; RNG.nextBytes(b); return Base64.getUrlEncoder().withoutPadding().encodeToString(b); }
    private String newOtt()  { byte[] b=new byte[32]; RNG.nextBytes(b); return Base64.getUrlEncoder().withoutPadding().encodeToString(b); }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // --- 세션에서 모바일 플래그/딥링크 복원 ---
        HttpSession session = request.getSession(false);
        String target = session != null ? (String) session.getAttribute(ATTR_TARGET) : null;
        String redirectUri = session != null ? (String) session.getAttribute(ATTR_REDIRECT) : null;
        if (session != null) {
            session.removeAttribute(ATTR_TARGET);
            session.removeAttribute(ATTR_REDIRECT);
        }

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        Member member = memberRepository.findByEmail(email).orElseThrow();

        String accessToken  = jwtTokenProvider.createAccessToken(email, member.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(email, member.getRole());
        String rtid = newRtid();
        refreshTokenService.saveRefreshToken(rtid, refreshToken);

        // ★ 모바일: OTT 발급 후 딥링크로 리다이렉트
        if ("mobile".equalsIgnoreCase(target) && redirectUri != null) {
            String ott = newOtt();

            Map<String, Object> payload = new HashMap<>();
            payload.put("accessToken", accessToken);
            payload.put("rtid", rtid);
            payload.put("profile", Map.of(
                    "email", member.getEmail(),
                    "name", member.getName(),
                    "role", member.getRole()
            ));
            String json = objectMapper.writeValueAsString(payload);
            refreshTokenService.saveOtt(ott, json);

            response.sendRedirect(redirectUri + "?ott=" + URLEncoder.encode(ott, StandardCharsets.UTF_8));
            return;
        }

        Cookie accessCookie = new Cookie("accessToken", accessToken);
        accessCookie.setHttpOnly(true); accessCookie.setSecure(false);
        accessCookie.setPath("/"); accessCookie.setMaxAge(60 * 30);
        response.addCookie(accessCookie);

        Cookie rtidCookie  = new Cookie("rtid", rtid);
        rtidCookie.setHttpOnly(true); rtidCookie.setSecure(false);
        rtidCookie.setPath("/"); rtidCookie.setMaxAge(60 * 60 * 24 * 7);
        response.addCookie(rtidCookie);

        String uri = String.format("%s/oauth/success?email=%s&provider=%s&name=%s",
                frontendUrl,
                URLEncoder.encode(member.getEmail(), StandardCharsets.UTF_8),
                URLEncoder.encode(member.getProvider().toString(), StandardCharsets.UTF_8),
                URLEncoder.encode(member.getName(), StandardCharsets.UTF_8));
        response.sendRedirect(uri);
    }
}
