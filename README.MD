# Re:AI (Rehearse with AI & Reinforce with AI)
![로고 이미지](images/main.png)
***

## 1. 프로젝트 소개

 **Re:AI**는 AI 기술을 활용하여 사용자의 면접 능력을 향상시키는 개인 맞춤형 면접 연습 플랫폼입니다.

 사용자는 실제와 유사한 면접 환경에서 AI 아바타와 함께 인터뷰를 진행하고, 종료 후에는 심층적인 분석과 피드백을 통해 자신의 강점과 약점을 파악할 수 있습니다.

 이 '**back**' Repository에서는 백엔드 파트에 대해 설명하겠습니다.

## ✨ 주요 기능

- **🤖 AI 아바타와 함께하는 실전 면접 시뮬레이션**: 3D AI 아바타와 음성으로 대화하며 실제 면접과 같은 긴장감 속에서 연습할 수 있습니다.
- **🎯 다양한 면접 유형 지원**: 직무별(기술, 인성) 및 종합 면접 등 다양한 시나리오를 선택하여 집중적으로 대비할 수 있습니다.
- **📊 심층 분석 및 피드백**: 면접 종료 후, 답변 내용, 사용 어휘, 음성 톤, 시선 처리 등을 종합적으로 분석하여 상세한 리포트를 제공합니다.
- **📱 크로스 플랫폼 지원**: 웹(PC) 환경뿐만 아니라 모바일 앱(Android, iOS)을 통해서도 언제 어디서든 면접을 연습할 수 있습니다.

***

## 2. 개발 기간
* 25.07.28~25.09.11

    ### 👥 백엔드 팀 구성
  * 고현우 (●'◡'●) [GitHub Profile](https://github.com/rhgusdn0708)
  * 박세훈 (❁´◡`❁) [GitHub Profile](https://github.com/TP9NS)
  * 최지은 (≧∇≦)ﾉ [GitHub Profile](https://github.com/nueij0429)

  ### 🛠️ 기술 스택
  #### ⚙️ Backend Core
    ![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white)
    ![Spring](https://img.shields.io/badge/Spring-6DB33F?style=flat-square&logo=spring&logoColor=white)
    ![Spring Web (MVC)](https://img.shields.io/badge/Spring_Web_(MVC)-6DB33F?style=flat-square&logo=spring&logoColor=white)
    ![Spring WebFlux](https://img.shields.io/badge/Spring_WebFlux-6DB33F?style=flat-square&logo=spring&logoColor=white)
    ![Spring Web Services](https://img.shields.io/badge/Spring_Web_Services-6DB33F?style=flat-square&logo=spring&logoColor=white)
    ![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=flat-square&logo=spring&logoColor=white)
    ![Bean Validation](https://img.shields.io/badge/Bean_Validation-6DB33F?style=flat-square&logo=spring&logoColor=white)
    ![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?style=flat-square&logo=thymeleaf&logoColor=white)
    ![Actuator](https://img.shields.io/badge/Spring_Actuator-6DB33F?style=flat-square&logo=spring&logoColor=white)

  #### 🔐 Security & Auth
    ![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=flat-square&logo=spring&logoColor=white)
    ![OAuth2 Authorization Server](https://img.shields.io/badge/OAuth2_Authorization_Server-6DB33F?style=flat-square&logo=spring&logoColor=white)
    ![OAuth2 Client](https://img.shields.io/badge/OAuth2_Client-6DB33F?style=flat-square&logo=spring&logoColor=white)
    ![OAuth2 Resource Server](https://img.shields.io/badge/OAuth2_Resource_Server-6DB33F?style=flat-square&logo=spring&logoColor=white)
    ![JJWT](https://img.shields.io/badge/JSON_Web_Tokens_(JJWT)-000000?style=flat-square&logo=jsonwebtokens&logoColor=white)

  #### 🗄️ Data & Storage
    ![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=flat-square&logo=mariadb&logoColor=white)
    ![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat-square&logo=redis&logoColor=white)
    ![Spring Session (Redis)](https://img.shields.io/badge/Spring_Session_(Redis)-6DB33F?style=flat-square&logo=spring&logoColor=white)
    ![Spring Cache](https://img.shields.io/badge/Spring_Cache-6DB33F?style=flat-square&logo=spring&logoColor=white)

  #### 🔌 API & Docs
    ![OpenAPI (springdoc)](https://img.shields.io/badge/OpenAPI_(springdoc)-6BA539?style=flat-square&logo=openapiinitiative&logoColor=white)

  #### ⚡ Realtime & Messaging
    ![WebSocket](https://img.shields.io/badge/WebSocket-3C3C3C?style=flat-square)
    ![SockJS](https://img.shields.io/badge/SockJS-3C3C3C?style=flat-square)
    ![STOMP](https://img.shields.io/badge/STOMP-3C3C3C?style=flat-square)
    ![WebJars](https://img.shields.io/badge/WebJars-3C3C3C?style=flat-square)

  #### ✉️ Mail
    ![Spring Mail](https://img.shields.io/badge/Spring_Mail-6DB33F?style=flat-square&logo=spring&logoColor=white)

  #### 🧪 Test & Utilities
    ![JUnit 5](https://img.shields.io/badge/JUnit_5-25A162?style=flat-square&logo=junit5&logoColor=white)
    ![Lombok](https://img.shields.io/badge/Lombok-555555?style=flat-square)

  #### ☁️ Infra & Deployment
    ![AWS S3 SDK v2](https://img.shields.io/badge/AWS_S3_SDK_v2-232F3E?style=flat-square&logo=amazonaws&logoColor=white)

***

## 3. 프로젝트 구조

```text
├─.github/                            — GitHub 설정 디렉터리
│  └─workflows/                       — CI/CD 워크플로우
│      └─deploy-back.yml              — 백엔드 배포 GitHub Actions
│
├─load-tests/                         — k6 부하/성능 테스트 스크립트
│  ├─interview-by-id.js               — 인터뷰 단건 조회 API 부하 테스트
│  ├─interview-results.js             — 인터뷰 결과 목록/상세 부하 테스트
│  └─smoke.js                         — 스모크 테스트(헬스 체크)
│
├─src/
│  ├─main/
│  │  ├─java/
│  │  │  └─com/backend/recruitAi/
│  │  │      ├─admin/controller/     — 관리자 페이지/로그인 뷰 컨트롤러
│  │  │      ├─config/               — Cache/Redis/Swagger/WebClient/WebSocket/S3 등 설정
│  │  │      ├─email/{...}/          — 이메일 인증·발송 모듈(컨트롤러/서비스/엔티티 등)
│  │  │      ├─global/{...}/         — 전역 예외 처리, 공통 응답 DTO
│  │  │      ├─interview/{...}/      — 면접 흐름, 소켓, 통계, 임시미디어, Redis pub/sub
│  │  │      ├─jwt/                  — JWT 토큰/필터/예외 핸들러
│  │  │      ├─media/{config}/       — 로컬/S3 미디어 저장소 및 S3 설정
│  │  │      ├─member/{...}/         — 회원/인증/관리자 API, DTO, 리포지토리, 서비스
│  │  │      ├─mypage/{...}/         — 마이페이지 API/서비스
│  │  │      ├─result/{...}/         — 결과 저장·조회, 직무별 평균 캐시/집계
│  │  │      ├─runner/               — 초기 데이터 로딩 러너
│  │  │      └─security/{exception}/ — Spring Security/OAuth2 설정 및 예외
│  │  │
│  │  └─resources/
│  │      ├─application.properties    — 공통 설정(기본 프로필)
│  │      ├─application-dev.properties— 개발용 설정
│  │      ├─application-prod.properties— 운영용 설정
│  │      ├─templates/                — Thymeleaf 템플릿
│  │      │  ├─login.html             — 로그인 페이지
│  │      │  └─admin/
│  │      │      └─dashboard.html     — 관리자 대시보드
│  │      └─static/                   — 정적 리소스
│  │          ├─css/style.css         — 스타일시트
│  │          └─js/{dashboard.js,main.js,member.js,stats.js} — 프런트 스크립트
│  │         
│  └─test/
│      └─java/com/backend/recruitAi/
│          └─RecruitAiApplicationTests.java — 기본 스프링 부트 테스트
│
└─temp-media/
   └─<uploaded-temp-files>            — 업로드 영상/임시 미디어 저장소(개발용)
```