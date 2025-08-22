package com.backend.recruitAi.security;

import com.backend.recruitAi.security.exception.OAuth2RegistrationRequiredException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.backend.recruitAi.member.controller.OAuthBridgeController.ATTR_REDIRECT;
import static com.backend.recruitAi.member.controller.OAuthBridgeController.ATTR_TARGET;


@Component
public class OAuth2FailureHandler implements AuthenticationFailureHandler {
    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) {
        try {
            // --- 세션에서 모바일 플래그/딥링크 복원 ---
            HttpSession session = request.getSession(false);
            String target = session != null ? (String) session.getAttribute(ATTR_TARGET) : null;
            String redirectUri = session != null ? (String) session.getAttribute(ATTR_REDIRECT) : null;
            if (session != null) {
                session.removeAttribute(ATTR_TARGET);
                session.removeAttribute(ATTR_REDIRECT);
            }

            if (exception instanceof OAuth2RegistrationRequiredException regEx) {
                Map<String, Object> attr = regEx.getAttributes();
                String email    = (String) attr.get("email");
                String name     = (String) attr.get("name");
                String provider = (String) attr.get("registrationId");

                // ★ 모바일: 가입 필요 신호를 딥링크로
                if ("mobile".equalsIgnoreCase(target) && redirectUri != null) {
                    String qs = String.format("needSignup=1&email=%s&name=%s&provider=%s",
                            URLEncoder.encode(email, StandardCharsets.UTF_8),
                            URLEncoder.encode(name,  StandardCharsets.UTF_8),
                            URLEncoder.encode(provider, StandardCharsets.UTF_8));
                    response.sendRedirect(redirectUri + "?" + qs);
                    return;
                }

                // ★ 웹: 기존처럼 프론트의 소셜 가입 페이지로
                String path = "kakao".equalsIgnoreCase(provider) ? "kakao-signup"
                        : "google".equalsIgnoreCase(provider) ? "google-signup" : "social-signup";
                String uri = String.format("%s/%s?email=%s&name=%s&provider=%s",
                        frontendUrl, path,
                        URLEncoder.encode(email, StandardCharsets.UTF_8),
                        URLEncoder.encode(name,  StandardCharsets.UTF_8),
                        URLEncoder.encode(provider, StandardCharsets.UTF_8));
                response.sendRedirect(uri);
                return;
            }

            // 기타 오류
            if ("mobile".equalsIgnoreCase(target) && redirectUri != null) {
                response.sendRedirect(redirectUri + "?error=unknown");
            } else {
                response.sendRedirect("/login?error=true");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
