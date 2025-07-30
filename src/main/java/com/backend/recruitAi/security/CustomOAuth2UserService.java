package com.backend.recruitAi.security;

import com.backend.recruitAi.member.entity.Member;
import com.backend.recruitAi.member.repository.MemberRepository;
import com.backend.recruitAi.security.exception.OAuth2RegistrationRequiredException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(request);

        String registrationId = request.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = null;
        String name = null;

        if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            email = (String) kakaoAccount.get("email");
            name = (String) profile.get("nickname");
        } else if ("google".equals(registrationId)) {
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
            System.out.println(email+"googogogoo");
            System.out.println(name+"gogogogogo");
        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다: " + registrationId);
        }

        // email, name만 있는 새로운 attribute 생성
        Map<String, Object> flattenedAttributes = new HashMap<>();
        flattenedAttributes.put("email", email);
        flattenedAttributes.put("name", name);
        flattenedAttributes.put("registrationId", registrationId); // optional: 프론트에서 구분 필요 시

        return memberRepository.findByEmail(email)
                .map(member -> new DefaultOAuth2User(
                        List.of(new SimpleGrantedAuthority(member.getRole().name())),
                        flattenedAttributes,
                        "email"
                ))
                .orElseThrow(() -> new OAuth2RegistrationRequiredException("소셜 회원가입 필요", flattenedAttributes));
    }
}
