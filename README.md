## 각자도생 대신 같이하는 갓생 챌린지 애플리케이션, 같생(GODSAENG)

서로 응원하며 모두의 노력으로 달성하는 챌린지 앱 없을까? 고민했다면 같생!  

<p align="center">
  <img width="19%" height="400" alt="image" src="https://github.com/mocacong-hackathonKU/godsaeng/assets/69844138/7e70954b-1419-4878-96e0-c65abc518830">
  <img width="19%" height="400" alt="image" src="https://github.com/mocacong-hackathonKU/godsaeng/assets/69844138/d408b3c3-32e2-4498-9963-d75af1ae5714">
  <img width="19%" height="400 alt="image" src="https://github.com/mocacong-hackathonKU/godsaeng/assets/69844138/ddfbc744-3db6-4fd1-b02c-33a1598f6e98">
  <img width="19%" height="400" alt="image" src="https://github.com/mocacong-hackathonKU/godsaeng/assets/69844138/d73b8c0c-2ec0-4b1e-a3fe-f7f669344498">
  <img width="19%" height="400"" alt="image" src="https://github.com/mocacong-hackathonKU/godsaeng/assets/69844138/84d5675a-16b0-4bf3-b1fe-fb27261af0c9">
</p>


- - - 

### MVP 기능 목차
<p align="center">
  <img width="969" alt="스크린샷 2023-08-17 오전 4 54 32" src="https://github.com/mocacong-hackathonKU/godsaeng/assets/69844138/39d62e6f-14b1-40fc-b84e-66ce2aa3a575">
</p>

---

### MVP 주요 기능 소개
- **로그인**
  - Apple OAuth 로그인 기능 제공
  - Kakao OAuth 로그인 기능 제공
- **캘린더**
  - 자신이 참여한 같생 챌린지에 대해 월별 같생 조회
  - 자신이 참여한 같생 챌린지에 대해 일별 같생 조회
  - 자신이 참여한 같생 달성 상태 조회
- **같생**
  - 자신이 참여하고 싶은 다른 회원이 만든 같생에 참여
  - 직접 자신이 같생 챌린지 모임을 생성
  - 같생 상세 정보 조회
  - 자신이 참여한 같생에 대해 해당하는 요일에 인증 사진과 인증 내용을 포함하여 인증 글 작성 
- **마이페이지**
  - 프로필 조회
  - 프로필 이미지 수정
  - 로그아웃 및 회원 탈퇴 
- - - 

### 사용 기술스택
**BackEnd**
- `Language`: Java 11, JUnit 5
- `Framework`: Spring Boot 2.7.9
- `Database`: H2, Amazon RDS for MySQL, Amazon Elasticache for Redis
- `ORM`: JPA (Spring Data JPA)
- `Deploy`: Github Actions, Docker CI/CD
- `Logging`: Logback, AWS Cloudwatch, AWS Lambda, Slack API
- `API Docs`: SpringDoc Swagger 3
<br>

**iOS**
- `Language`: Swift
- `Architecture Design`: MVVM
- `View`: SwiftUI framework
- `서버 통신`: Combine + URL session
- `사진 라이브러리`: PhotosUI
<br>

**Android**
- `Language`: Kotlin
- `Architecture Design`: MVVM

- - - 

### ERD
<p align="center">
  <img width="560" alt="image" src="https://github.com/mocacong-hackathonKU/godsaeng/assets/69844138/779345a2-01e1-4626-8688-f069d3058c45">

</p>

- - - 

### 서비스 아키텍처
<p align="center">
<img width="665" alt="image" src="https://github.com/mocacong-hackathonKU/godsaeng/assets/69844138/c1129101-b9da-4f7f-a7af-f80d6e9c5035">

<img width="665" alt="스크린샷 2023-08-17 오전 2 41 51" src="https://github.com/mocacong-hackathonKU/godsaeng/assets/69844138/a48c4b34-7835-4ec4-957d-b0fd6a0ce282">

</p>

