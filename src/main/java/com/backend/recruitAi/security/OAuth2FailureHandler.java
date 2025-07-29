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

                Map<String, Object> attributes = regEx.getAttributes();

                String email = (String) attributes.get("email");
                String name = (String) attributes.get("name");
                String registrationId = (String) attributes.get("registrationId");

                String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);
                String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8);
                String encodedProvider = URLEncoder.encode(registrationId, StandardCharsets.UTF_8);

                String redirectUri = String.format(
                        "http://localhost:3000/kakao-signup?email=%s&name=%s&provider=%s",
                        encodedEmail, encodedName, encodedProvider
                );

                response.sendRedirect(redirectUri);
            } else {
                response.sendRedirect("/login?error=true");
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                response.sendRedirect("/login?error=exception");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
