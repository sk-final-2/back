package com.backend.recruitAi.runner;

import com.backend.recruitAi.member.entity.*;
import com.backend.recruitAi.member.repository.MemberRepository;
import com.backend.recruitAi.result.service.InterviewResultService; //추가
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import com.backend.recruitAi.result.entity.AnswerAnalysis; //추가
import com.backend.recruitAi.result.entity.InterviewResult; //추가
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime; //추가
import java.util.Arrays; //추가

@Component
@RequiredArgsConstructor

public class DataInitRunner implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final InterviewResultService interviewResultService; //추가

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
            Member kakaoMember = memberRepository.save(Member.builder() //면접결과분석저장용테스트코드
            //memberRepository.save(Member.builder() 기존코드
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

            System.out.println("--- 샘플 인터뷰 데이터 초기화 시작 ---");

            // 첫 번째 인터뷰 결과 데이터
            AnswerAnalysis answer1_1 = AnswerAnalysis.builder()
                    .seq(1)
                    .question("자기소개를 해주세요.")
                    .answer("안녕하세요. 저는 박세훈입니다. 백엔드 개발에 열정이 있습니다.")
                    .good("명확하고 간결한 자기소개였습니다.")
                    .bad("기술 역량에 대한 구체적인 설명이 부족했습니다.")
                    .emotion(90)
                    .tracking(95)
                    .build();

            AnswerAnalysis answer1_2 = AnswerAnalysis.builder()
                    .seq(2)
                    .question("RESTful API 설계 원칙에 대해 설명해주세요.")
                    .answer("REST는 Representational State Transfer의 약자로, 자원을 이름으로 구분하여 해당 자원의 상태를 주고받는 모든 것을 의미합니다. 무상태성, 클라이언트-서버 분리, 계층화 등의 원칙이 있습니다.")
                    .good("RESTful API에 대한 기본적인 이해도가 높습니다.")
                    .bad("각 원칙에 대한 구체적인 예시가 부족했습니다.")
                    .emotion(85)
                    .tracking(80)
                    .build();

            // InterviewResult 엔티티 생성 시 AnswerAnalysis 리스트 추가
            InterviewResult interviewResult1 = InterviewResult.builder()
                    .member(kakaoMember) // kakaoMember 사용
                    .createdAt(LocalDateTime.now().minusHours(24)) // 24시간 전으로 설정
                    .answerAnalyses(Arrays.asList(answer1_1, answer1_2))
                    .build();
            // InterviewResult 저장 (cascade 설정에 의해 AnswerAnalysis도 함께 저장)
            interviewResultService.saveInterviewResult(interviewResult1); // InterviewResult 엔티티 직접 전달
            System.out.println("✅ 첫 번째 InterviewResult 저장 완료: ID = " + interviewResult1.getId());

            // 두 번째 인터뷰 결과 데이터
            AnswerAnalysis answer2_1 = AnswerAnalysis.builder()
                    .seq(1)
                    .question("가장 좋아하는 프로그래밍 언어는 무엇인가요?")
                    .answer("저는 Java를 가장 좋아합니다. 안정적이고 객체지향적인 특징 때문입니다.")
                    .good("Java에 대한 명확한 선호 이유를 제시했습니다.")
                    .bad("다른 언어와의 비교가 부족했습니다.")
                    .emotion(75)
                    .tracking(90)
                    .build();

            AnswerAnalysis answer2_2 = AnswerAnalysis.builder()
                    .seq(2)
                    .question("팀원과의 갈등을 어떻게 해결하나요?")
                    .answer("먼저 상대방의 의견을 경청하고, 서로의 목표를 상기시켜 합의점을 찾으려 노력합니다.")
                    .good("소통과 협업 능력을 보여주는 좋은 답변입니다.")
                    .bad("구체적인 갈등 해결 사례가 제시되지 않았습니다.")
                    .emotion(88)
                    .tracking(85)
                    .build();

            AnswerAnalysis answer2_3 = AnswerAnalysis.builder()
                    .seq(3)
                    .question("마지막으로 하고 싶은 말은?")
                    .answer("이 자리에 합류하여 귀사의 성장에 기여하고 싶습니다. 감사합니다.")
                    .good("열정적인 태도를 보여주며 잘 마무리했습니다.")
                    .bad("없음")
                    .emotion(92)
                    .tracking(98)
                    .build();

            InterviewResult interviewResult2 = InterviewResult.builder()
                    .member(kakaoMember) // kakaoMember 사용
                    .createdAt(LocalDateTime.now()) // 현재 시간으로 설정
                    .answerAnalyses(Arrays.asList(answer2_1, answer2_2, answer2_3))
                    .build();

            interviewResultService.saveInterviewResult(interviewResult2); // InterviewResult 엔티티 직접 전달
            System.out.println("✅ 두 번째 InterviewResult 저장 완료: ID = " + interviewResult2.getId());

            System.out.println("--- 샘플 인터뷰 데이터 초기화 완료 ---");
        }
    }
}
