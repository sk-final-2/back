package com.backend.recruitAi.member.controller;

import com.backend.recruitAi.global.exception.BusinessException;
import com.backend.recruitAi.global.exception.ErrorCode;
import com.backend.recruitAi.global.response.ResponseDto;
import com.backend.recruitAi.jwt.JwtTokenProvider;
import com.backend.recruitAi.jwt.RefreshTokenService;
import com.backend.recruitAi.member.dto.*;

import com.backend.recruitAi.member.entity.Member;
import com.backend.recruitAi.member.entity.Provider;
import com.backend.recruitAi.member.entity.Role;
import com.backend.recruitAi.member.repository.MemberRepository;
import com.backend.recruitAi.member.service.CustomUserDetails;
import com.backend.recruitAi.member.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired private RefreshTokenService refreshTokenService;

    @Autowired private MemberRepository memberRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtTokenProvider jwtTokenProvider;
    @Autowired private MemberService memberService;

    private static final SecureRandom RNG = new SecureRandom();

    private String newRtid() {
        byte[] buf = new byte[32];
        RNG.nextBytes(buf);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
    }

    @PostMapping("/signup")
    public ResponseDto<?> signup(@Valid @RequestBody SignupRequest request) {
        if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException(ErrorCode.EMAIL_DUPLICATED);
        }

        Member member = Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .provider(Provider.LOCAL)
                .role(Role.ROLE_USER)
                .postcode(request.getZipcode())
                .address1(request.getAddress1())
                .address2(request.getAddress2())
                .gender(request.getGender())
                .birth(request.getBirth())
                .build();

        memberRepository.save(member);
        return ResponseDto.success("회원가입 성공");
    }

    @PostMapping("/login")
    public ResponseDto<LoginResponseDto> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        String accessToken = jwtTokenProvider.createAccessToken(member.getEmail(), member.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getEmail(), member.getRole());

        String rtid = newRtid();

        refreshTokenService.saveRefreshToken(rtid, refreshToken);

        Cookie accessCookie = new Cookie("accessToken", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(false);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(60 * 1); // 30분
        response.addCookie(accessCookie);

        Cookie rtidCookie  = new Cookie("rtid", rtid);
        rtidCookie .setHttpOnly(true);
        rtidCookie .setSecure(false);
        rtidCookie .setPath("/");
        rtidCookie .setMaxAge(60 * 60 * 24 * 7);
        response.addCookie(rtidCookie );


        LoginResponseDto loginResponseDto = new LoginResponseDto(member);

        return ResponseDto.success(loginResponseDto);
    }

    @PostMapping("/reissue")
    public ResponseDto<?> reissueToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new BusinessException(ErrorCode.COOKIE_NOT_FOUND);
        }

        String rtid = null;
        for (Cookie cookie : cookies) {
            if ("rtid".equals(cookie.getName())) {
                rtid = cookie.getValue();
                break;
            }
        }

        if (rtid == null) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        String storedRefreshToken = refreshTokenService.getRefreshToken(rtid);
        if (storedRefreshToken == null || !jwtTokenProvider.validateToken(storedRefreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        String email = jwtTokenProvider.getEmail(storedRefreshToken);
        Role role = jwtTokenProvider.getRole(storedRefreshToken);

        String newAccessToken = jwtTokenProvider.createAccessToken(email, role);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(email, role);
        String newRtid = newRtid();

        refreshTokenService.saveRefreshToken(newRtid, newRefreshToken);
        refreshTokenService.deleteRefreshToken(rtid);


        Cookie newAccessCookie = new Cookie("accessToken", newAccessToken);
        newAccessCookie.setHttpOnly(true);
        newAccessCookie.setSecure(false);
        newAccessCookie.setPath("/");
        newAccessCookie.setMaxAge(60 * 10); // 10분
        response.addCookie(newAccessCookie);

        Cookie newRtidCookie = new Cookie("rtid", newRtid);
        newRtidCookie.setHttpOnly(true);
        newRtidCookie.setSecure(false);
        newRtidCookie.setPath("/");
        newRtidCookie.setMaxAge(60 * 60 * 24 * 7); // 7일
        response.addCookie(newRtidCookie);

        return ResponseDto.success("Access Token 재발급 완료");
    }

    @PostMapping("/logout")
    public ResponseDto<?> logout(HttpServletRequest request, HttpServletResponse response) {
        // 1) rtid 쿠키 찾기
        String rtid = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("rtid".equals(c.getName())) {
                    rtid = c.getValue();
                    break;
                }
            }
        }

        // 2) Redis에서 해당 rtid 세션 삭제(현재 디바이스만 로그아웃)
        if (rtid != null && !rtid.isEmpty()) {
            refreshTokenService.deleteRefreshToken(rtid);
        }

        // 3) 쿠키 만료(삭제) - rtid
        Cookie killRtid = new Cookie("rtid", "");
        killRtid.setHttpOnly(true);
        killRtid.setSecure(false);
        killRtid.setPath("/");
        killRtid.setMaxAge(0);
        response.addCookie(killRtid);

        // 4) 쿠키 만료(삭제) - accessToken
        Cookie killAccess = new Cookie("accessToken", "");
        killAccess.setHttpOnly(true);
        killAccess.setSecure(false);
        killAccess.setPath("/");
        killAccess.setMaxAge(0);
        response.addCookie(killAccess);

        return ResponseDto.success("로그아웃 완료");
    }
    
    @GetMapping("/me")
    public ResponseDto<?> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember(); // 또는 userDetails.getUsername(), getAuthorities() 등
        return ResponseDto.success(Map.of(
                "email", member.getEmail(),
                "name", member.getName(),
                "role", member.getRole()
        ));
    }

    @PostMapping("/kakao-signup")
    public ResponseDto<String> kakaoSignup(@Valid @RequestBody KakaoSignupRequest request) {
        try {
            memberService.kakaoSignup(request);
            return ResponseDto.success("카카오 회원가입 성공");
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.EMAIL_VERIFICATION_NOT_FOUND);
        }
    }
    @PostMapping("/google-signup")
    public ResponseDto<String> googleSignup(@Valid @RequestBody GoogleSignupRequest request) {
        try {
            memberService.googleSignup(request);
            return ResponseDto.success("google 회원가입 성공");
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.EMAIL_VERIFICATION_NOT_FOUND);
        }
    }

}
