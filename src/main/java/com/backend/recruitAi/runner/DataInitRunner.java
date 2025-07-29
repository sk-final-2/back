package com.backend.recruitAi.runner;

import com.backend.recruitAi.member.entity.*;
import com.backend.recruitAi.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor

public class DataInitRunner implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (memberRepository.count() == 0) {
            memberRepository.save(Member.builder()
                    .email("a@a.com")
                    .name("유저1")
                    .password(passwordEncoder.encode("123"))
                    .postcode("12345")
                    .address1("서울특별시 중구 세종대로")
                    .address2("101호")
                    .gender(GenderType.MALE)
                    .birth(LocalDate.of(2000, 1, 1))
                    .provider(Provider.LOCAL)
                    .role(Role.ROLE_USER)
                    .build());

            memberRepository.save(Member.builder()
                    .email("b@b.com")
                    .name("유저2")
                    .password(passwordEncoder.encode("123"))
                    .postcode("54321")
                    .address1("부산광역시 해운대구")
                    .address2("202호")
                    .gender(GenderType.FEMALE)
                    .birth(LocalDate.of(1999, 5, 5))
                    .provider(Provider.LOCAL)
                    .role(Role.ROLE_USER)
                    .build());

            memberRepository.save(Member.builder()
                    .email("admin@admin.com")
                    .name("관리자")
                    .password(passwordEncoder.encode("admin"))
                    .postcode("99999")
                    .address1("경기도 수원시 장안구")
                    .address2("관리동 1층")
                    .gender(GenderType.OTHER)
                    .birth(LocalDate.of(1990, 8, 15))
                    .provider(Provider.LOCAL)
                    .role(Role.ROLE_ADMIN)
                    .build());

            // ✅ 카카오 사용자
            memberRepository.save(Member.builder()
                    .email("ajtwlstpgns@naver.com") // 사용자의 이메일
                    .name("박세훈") // 사용자의 이름
                    .password(passwordEncoder.encode("kakao-user")) // 의미 없는 패스워드
                    .postcode("08501")
                    .address1("서울특별시 관악구 신림로")
                    .address2("303호")
                    .gender(GenderType.MALE)
                    .birth(LocalDate.of(2000, 8, 11))
                    .provider(Provider.KAKAO)
                    .role(Role.ROLE_USER)
                    .build());

            System.out.println("✅ 샘플 멤버 4명(카카오 포함) 초기화 완료");
        }
    }
}
