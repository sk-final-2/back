package com.backend.recruitAi.member.controller;

import com.backend.recruitAi.global.exception.BusinessException;
import com.backend.recruitAi.global.exception.ErrorCode;
import com.backend.recruitAi.global.response.ResponseDto;
import com.backend.recruitAi.jwt.JwtTokenProvider;
import com.backend.recruitAi.jwt.RefreshTokenService;
import com.backend.recruitAi.member.dto.LoginRequest;
import com.backend.recruitAi.member.dto.SignupRequest;
import com.backend.recruitAi.member.entity.Member;
import com.backend.recruitAi.member.entity.Provider;
import com.backend.recruitAi.member.entity.Role;
import com.backend.recruitAi.member.repository.MemberRepository;
import com.backend.recruitAi.member.service.CustomUserDetails;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired private RefreshTokenService refreshTokenService;

    @Autowired private MemberRepository memberRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    public ResponseDto<?> signup(@RequestBody SignupRequest request) {
        if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException(ErrorCode.EMAIL_DUPLICATED);
        }

        Member member = new Member();
        member.setEmail(request.getEmail());
        member.setPassword(passwordEncoder.encode(request.getPassword()));
        member.setName(request.getName());
        member.setProvider(Provider.LOCAL);
        member.setRole(Role.ROLE_USER);
        memberRepository.save(member);

        return ResponseDto.success("회원가입 성공");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        String accessToken = jwtTokenProvider.createAccessToken(member.getEmail(), member.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getEmail(), member.getRole());

        refreshTokenService.saveRefreshToken(member.getEmail(), refreshToken);

        Cookie accessCookie = new Cookie("accessToken", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(60 * 30); // 30분
        response.addCookie(accessCookie);

        return ResponseEntity.ok("로그인 성공");
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissueToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new BusinessException(ErrorCode.COOKIE_NOT_FOUND);
        }

        String accessToken = null;
        for (Cookie cookie : cookies) {
            if ("accessToken".equals(cookie.getName())) {
                accessToken = cookie.getValue();
                break;
            }
        }

        if (accessToken == null) {
            throw new BusinessException(ErrorCode.ACCESS_TOKEN_NOT_FOUND);
        }

        String email = jwtTokenProvider.getEmail(accessToken);

        String storedRefreshToken = refreshTokenService.getRefreshToken(email);

        if (storedRefreshToken == null || !jwtTokenProvider.validateToken(storedRefreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Role role = jwtTokenProvider.getRole(storedRefreshToken);
        String newAccessToken = jwtTokenProvider.createAccessToken(email, role);

        Cookie newAccessCookie = new Cookie("accessToken", newAccessToken);
        newAccessCookie.setHttpOnly(true);
        newAccessCookie.setSecure(true);
        newAccessCookie.setPath("/");
        newAccessCookie.setMaxAge(60 * 30); // 30분
        response.addCookie(newAccessCookie);

        return ResponseEntity.ok("Access Token 재발급 완료");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal CustomUserDetails userDetails) {
        refreshTokenService.deleteRefreshToken(userDetails.getUsername());
        return ResponseEntity.ok("로그아웃 완료");
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember(); // 또는 userDetails.getUsername(), getAuthorities() 등
        return ResponseEntity.ok(Map.of(
                "email", member.getEmail(),
                "name", member.getName(),
                "role", member.getRole()
        ));
    }
}
