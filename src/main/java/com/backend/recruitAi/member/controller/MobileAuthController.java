package com.backend.recruitAi.member.controller;

import com.backend.recruitAi.global.exception.BusinessException;
import com.backend.recruitAi.global.exception.ErrorCode;
import com.backend.recruitAi.global.response.ResponseDto;
import com.backend.recruitAi.jwt.JwtTokenProvider;
import com.backend.recruitAi.jwt.RefreshTokenService;
import com.backend.recruitAi.member.dto.KakaoSignupRequest;
import com.backend.recruitAi.member.dto.GoogleSignupRequest;
import com.backend.recruitAi.member.dto.LoginResponseDto;
import com.backend.recruitAi.member.dto.LoginTokensDto;
import com.backend.recruitAi.member.entity.Member;
import com.backend.recruitAi.member.entity.Provider;
import com.backend.recruitAi.member.entity.Role;
import com.backend.recruitAi.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/mobile")
public class MobileAuthController {
    @Autowired private RefreshTokenService refreshTokenService;
    @Autowired private ObjectMapper objectMapper;

    // ▼ 추가 의존성
    @Autowired private MemberRepository memberRepository;
    @Autowired private JwtTokenProvider jwtTokenProvider;
    @Autowired private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    // ====== 기존: OTT 교환 ======
    @GetMapping("/ott-exchange")
    public ResponseDto<?> exchange(@RequestParam String ott) {
        String json = refreshTokenService.consumeOtt(ott); // 1회용
        if (json == null) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        try {
            Map<String, Object> payload = objectMapper.readValue(json, Map.class);
            return ResponseDto.success(payload);
        } catch (Exception e) {
            // 적절한 에러코드로 매핑(예시로 기존 코드 유지)
            throw new BusinessException(ErrorCode.EMAIL_CODE_EXPIRED);
        }
    }

    // ====== 모바일 전용: 소셜 회원가입 + 즉시 토큰 발급 ======

    @PostMapping("/kakao-signup")
    public ResponseDto<LoginTokensDto> kakaoMobileSignup(@Valid @RequestBody KakaoSignupRequest req) {
        if (memberRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new BusinessException(ErrorCode.EMAIL_DUPLICATED);
        }

        Member m = Member.builder()
                .email(req.getEmail())
                .name(req.getName())
                .provider(Provider.KAKAO)
                .role(Role.ROLE_USER)
                .postcode(req.getZipcode())
                .address1(req.getAddress1())
                .address2(req.getAddress2())
                .gender(req.getGender())
                .birth(req.getBirth())
                // 소셜 계정은 비밀번호 사용 안 하므로 임의값 저장(BCrypt)
                .password(passwordEncoder.encode(randomSecret()))
                .build();

        memberRepository.save(m);
        return ResponseDto.success(issueTokensFor(m));
    }

    @PostMapping("/google-signup")
    public ResponseDto<LoginTokensDto> googleMobileSignup(@Valid @RequestBody GoogleSignupRequest req) {
        if (memberRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new BusinessException(ErrorCode.EMAIL_DUPLICATED);
        }

        Member m = Member.builder()
                .email(req.getEmail())
                .name(req.getName())
                .provider(Provider.GOOGLE)
                .role(Role.ROLE_USER)
                .postcode(req.getZipcode())
                .address1(req.getAddress1())
                .address2(req.getAddress2())
                .gender(req.getGender())
                .birth(req.getBirth())
                .password(passwordEncoder.encode(randomSecret()))
                .build();

        memberRepository.save(m);
        return ResponseDto.success(issueTokensFor(m));
    }

    // ====== 토큰 발급 공통 유틸 ======
    private static final SecureRandom RNG = new SecureRandom();

    private String randomSecret() {
        byte[] buf = new byte[32];
        RNG.nextBytes(buf);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
    }

    private LoginTokensDto issueTokensFor(Member member) {
        String access = jwtTokenProvider.createAccessToken(member.getEmail(), member.getRole());
        String refresh = jwtTokenProvider.createRefreshToken(member.getEmail(), member.getRole());
        String rtid = randomSecret();

        refreshTokenService.saveRefreshToken(rtid, refresh);

        return new LoginTokensDto(
                access,
                rtid, // 앱은 rtid를 저장해두고 /mobile/reissue 때 사용
                new LoginResponseDto(member)
        );
    }
}
