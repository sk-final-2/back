package com.backend.recruitAi.runner;

import com.backend.recruitAi.member.entity.*;
import com.backend.recruitAi.member.repository.MemberRepository;
import com.backend.recruitAi.result.repository.InterviewRepository;
import com.backend.recruitAi.result.entity.InterviewResult;
import com.backend.recruitAi.interview.entity.Interview;
import com.backend.recruitAi.interview.entity.InterviewType;
import com.backend.recruitAi.interview.entity.Level;
import com.backend.recruitAi.interview.entity.Language;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataInitRunner implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final InterviewRepository interviewRepository;

    @Override
    public void run(String... args) throws Exception {
        if (memberRepository.count() == 0) {
            // ✅ 기본 멤버 3명 + 카카오 유저 생성
            memberRepository.saveAll(Arrays.asList(
                    Member.builder()
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
                            .build(),
                    Member.builder()
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
                            .build(),
                    Member.builder()
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
                            .build()
            ));

            Member kakaoMember = memberRepository.save(Member.builder()
                    .email("ajtwlstpgns@naver.com")
                    .name("박세훈")
                    .password(passwordEncoder.encode("kakao-user"))
                    .postcode("08501")
                    .address1("서울특별시 관악구 신림로")
                    .address2("303호")
                    .gender(GenderType.MALE)
                    .birth(LocalDate.of(2000, 8, 11))
                    .provider(Provider.KAKAO)
                    .role(Role.ROLE_USER)
                    .build());

            System.out.println("✅ 샘플 멤버 4명(카카오 포함) 초기화 완료");

            // ✅ 첫 번째 인터뷰 저장
            Interview interview1 = Interview.builder()
                    .member(kakaoMember)
                    .uuid(UUID.randomUUID().toString())
                    .createdAt(LocalDateTime.now().minusHours(24))
                    .job("백엔드 개발자")
                    .career("신입")
                    .type(InterviewType.PERSONALITY)
                    .level(Level.중)
                    .language(Language.KOREAN)
                    .build();

            InterviewResult answer1_1 = InterviewResult.builder()
                    .seq(1)
                    .question("자기소개를 해주세요.")
                    .answer("안녕하세요. 저는 박세훈입니다. 백엔드 개발에 열정이 있습니다.")
                    .good("명확하고 간결한 자기소개였습니다.")
                    .bad("기술 역량에 대한 구체적인 설명이 부족했습니다.")
                    .score(85)
                    .emotion_score(90)
                    .tracking_score(95)
                    .interview(interview1)
                    .build();

            InterviewResult answer1_2 = InterviewResult.builder()
                    .seq(2)
                    .question("RESTful API 설계 원칙에 대해 설명해주세요.")
                    .answer("REST는 Representational State Transfer의 약자로...")
                    .good("RESTful API에 대한 기본적인 이해도가 높습니다.")
                    .bad("각 원칙에 대한 구체적인 예시가 부족했습니다.")
                    .score(80)
                    .emotion_score(85)
                    .tracking_score(80)
                    .interview(interview1)
                    .build();

            interview1.setAnswerAnalyses(Arrays.asList(answer1_1, answer1_2));
            interviewRepository.save(interview1);
            System.out.println("✅ 첫 번째 Interview 저장 완료: ID = " + interview1.getId());

            // ✅ 두 번째 인터뷰 저장
            Interview interview2 = Interview.builder()
                    .member(kakaoMember)
                    .uuid(UUID.randomUUID().toString())
                    .createdAt(LocalDateTime.now())
                    .job("백엔드 개발자")
                    .career("경력 1년차")
                    .type(InterviewType.MIXED)
                    .level(Level.상)
                    .language(Language.KOREAN)
                    .build();

            InterviewResult answer2_1 = InterviewResult.builder()
                    .seq(1)
                    .question("가장 좋아하는 프로그래밍 언어는 무엇인가요?")
                    .answer("저는 Java를 가장 좋아합니다.")
                    .good("Java에 대한 명확한 선호 이유를 제시했습니다.")
                    .bad("다른 언어와의 비교가 부족했습니다.")
                    .score(78)
                    .emotion_score(75)
                    .tracking_score(90)
                    .interview(interview2)
                    .build();

            InterviewResult answer2_2 = InterviewResult.builder()
                    .seq(2)
                    .question("팀원과의 갈등을 어떻게 해결하나요?")
                    .answer("먼저 상대방의 의견을 경청하고...")
                    .good("소통과 협업 능력을 보여주는 좋은 답변입니다.")
                    .bad("구체적인 갈등 해결 사례가 제시되지 않았습니다.")
                    .score(82)
                    .emotion_score(88)
                    .tracking_score(85)
                    .interview(interview2)
                    .build();

            InterviewResult answer2_3 = InterviewResult.builder()
                    .seq(3)
                    .question("마지막으로 하고 싶은 말은?")
                    .answer("이 자리에 합류하여 귀사의 성장에 기여하고 싶습니다.")
                    .good("열정적인 태도를 보여주며 잘 마무리했습니다.")
                    .bad("없음")
                    .score(90)
                    .emotion_score(92)
                    .tracking_score(98)
                    .interview(interview2)
                    .build();

            interview2.setAnswerAnalyses(Arrays.asList(answer2_1, answer2_2, answer2_3));
            interviewRepository.save(interview2);
            System.out.println("✅ 두 번째 Interview 저장 완료: ID = " + interview2.getId());

            System.out.println("--- 샘플 인터뷰 데이터 초기화 완료 ---");
        }
    }
}
