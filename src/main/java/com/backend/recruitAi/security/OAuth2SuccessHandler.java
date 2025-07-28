package com.backend.recruitAi.security;

import com.backend.recruitAi.jwt.JwtTokenProvider;
import com.backend.recruitAi.member.entity.Member;
import com.backend.recruitAi.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired private MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        Member member = memberRepository.findByEmail(email).orElseThrow();

        String accessToken = jwtTokenProvider.createAccessToken(email, member.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(email, member.getRole());

        // React 프론트엔드 연동 (리다이렉트 URL에 토큰 포함)
        String redirectUri = UriComponentsBuilder.fromUriString("http://localhost:3000/oauth2/redirect")
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();

        response.sendRedirect(redirectUri);
    }
}
