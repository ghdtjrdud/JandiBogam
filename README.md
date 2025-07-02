<img src="https://github.com/user-attachments/assets/921640d0-b819-4b93-a1f3-973d2f39c897" width="250"> 

# 다이닝봄 (Dining Bom)
고령자의 식사와 복약을 안전하게 관리하고, 가족과 함께 건강 상태를 체크할 수 있는 웹 서비스


<br>

## 1. 프로젝트 개요

| 항목 | 내용 |
|------|------|
| 프로젝트명 | 다이닝봄 |
| 개발기간 | 2025.05.01 ~ 2025.05.27 (4주) |
| 개발인원 | 2명 |
| 대상 | 고령자 및 가족(보호자) |
| 플랫폼 | 웹(Web) 기반 서비스 |
| 주요 기능 | 식단 기록, 복약 기록, 가족 연동, 주간 리포트, 맞춤형 식단 추천 |


| 이름 | 역할 | GitHub | 주요 담당 업무 |
|------|------|---------|----------------|
| 홍석영 | BE & FE | [GitHub 프로필](https://github.com/ghdtjrdud) | 로그인(JWT) 기능 구현, 사용자 테마 설정 UI 구현, 그룹 코드 생성 및 관리 시스템 |
| 유지은 | BE & FE | [GitHub 프로필](https://github.com/jieun4587) | 식약청 DB 활용한 식단 분석, 이미지 파일 입출력 구현, AI를 활용한 식단 피드백 기능 구현  |


<br><br>
## 2. 기획 의도

**다이닝봄**은 초고령사회로의 급속한 진입과 만성질환자 비율 증가에 대응하기 위해 기획된 건강관리 서비스입니다.  

고령자 스스로 식단과 복약을 쉽게 기록할 수 있도록 하고, 보호자는 원격으로 건강 상태를 확인하여 효율적으로 돌봄을 수행할 수 있습니다. 

또한 질환 정보를 기반으로 한 **맞춤형 식단 추천**과 **주간 리포트 기능**을 통해 만성질환을 예방하고 건강한 생활 습관 형성을 유도합니다.

**사용자 친화적인 UI/UX와 큰 글씨**, **직관적인 화면 설계**를 통해 디지털 환경에 익숙하지 않은 고령자도 부담 없이 활용할 수 있도록 했습니다.  
**다이닝봄**은 고령자의 자율성과 가족의 연계성을 동시에 강화하며, 일상 속 지속 가능한 건강 관리를 실현하고자 합니다.


<br><br>
## 3. ERD / API / 기획 문서

| 문서 | 링크 |
|------|------|
| ERD 다이어그램 | [📌 ERD 보기](https://www.erdcloud.com/d/your_link) |
| API 명세서 | [📌 API 보기](https://documenter.getpostman.com/view/your_link) |
| 기능 명세서 | [📌 기능 보기](https://docs.google.com/spreadsheets/d/your_link) |
| 프로젝트 Notion | [📌 Notion 링크](https://www.notion.so/ssafy-jinhyeok/1ee7f669b13380b6aa6dc3c2a50bc56e?pvs=4) |


<br><br>
## 4. 기술 스택

| 구분 | 사용 기술 |
|------|-----------|
| **백엔드** | Java 21, Spring Boot 3 |
| **DB** | MySQL 8, MyBatis |
| **프론트엔드** | Vue.js, Bootstrap |
| **기타** | JWT 인증, MultipartFile 이미지 업로드 |


<br><br>
## 5. 주요 기능

| 구분 | 기능 | 설명 |
|------|------|------|
| 사용자 관리 | 회원가입/로그인, 프로필 질환 관리 |
| 식단 관리 | 기록, 조회, 공유 |
| 복약 관리 | 기록, 알림, 복약 준수율 확인 |
| 가족 연결 | 그룹 생성/참여/관리 |
| 건강 관리 | 대시보드, 주간 리포트, 건강 팁 |
| 식단 추천 | 질환 기반 추천, 주간 분석 기반 제안 |


<br><br>
## 6. 핵심 기능 소개

### 1) 메인화면 및 식단/복약 관리
<!-- 여기에 그림 추가: width="750px" -->
![image](https://github.com/user-attachments/assets/610a4186-1981-48c1-bdca-772841ba975f)


### 2) AI 맞춤형 통합 건강 관리 서비스
![image](https://github.com/user-attachments/assets/1fdf4ad5-a3e1-44c1-b50c-019b50edf07d)


### 3) 가족(그룹) 연동 시스템
![image](https://github.com/user-attachments/assets/990c4f2d-c716-4515-9418-d23f42c7741c)





