package com.backend.recruitAi.security;

import com.backend.recruitAi.security.exception.OAuth2RegistrationRequiredException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class OAuth2FailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) {
        try {
            if (exception instanceof OAuth2RegistrationRequiredException regEx) {

                System.out.println("OAuth2 로그인 실패: " + exception.getClass().getSimpleName());
                exception.printStackTrace();

                Map<String, Object> attributes = regEx.getAttributes();

                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

                String email = (String) kakaoAccount.get("email");
                String nickname = (String) profile.get("nickname");

                String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);
                String encodedNickname = URLEncoder.encode(nickname, StandardCharsets.UTF_8);

                String redirectUri = "http://localhost:3000/kakao-signup?email=" + encodedEmail + "&nickname=" + encodedNickname;
                response.sendRedirect(redirectUri);
            } else {
                response.sendRedirect("/login?error=true");
            }
        } catch (IOException e) {
            // 예외 발생 시 로깅 및 fallback 처리
            e.printStackTrace();
            try {
                response.sendRedirect("/login?error=exception");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
