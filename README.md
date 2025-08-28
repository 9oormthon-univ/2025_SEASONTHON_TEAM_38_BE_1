## 커밋 컨벤션

```
#이슈번호 타입: 메시지
```

### 커밋 타입

- feat → 새로운 기능 추가  
- fix → 버그 수정  
- refactor → 코드 리팩토링  
- docs → 문서 수정  
- style → 코드 스타일 수정 (포맷, 세미콜론 등)  
- test → 테스트 코드 작성/수정  
- chore → 빌드, 설정, 패키지 관리 등 잡다한 수정  
- perf → 성능 개선  
- ci → CI/CD 관련 설정  
- revert → 이전 커밋 되돌리기  


### 예시
```
#1 feat: 꿈 목록 조회 API 추가  
#2 fix: 로그인 로직 버그 수정
```

## 패키지 구조 컨벤션 (DDD 기반)
```
com.example.project
│
├── domain
│   ├── [도메인명]
│   │   ├── service
│   │   ├── entity
│   │   ├── repository
│   │   ├── controller
│   │   └── dto
├── global
│   ├── config
│   ├── security
│   └── util

- domain: 도메인별 패키지

- service: 비즈니스 로직

- entity: JPA Entity 등 도메인 모델

- repository: 데이터 액세스

- controller: API 컨트롤러
  
- dto: 요청/응답 DTO

- global: 공통 설정, 보안, 유틸리티 등  
```

## 브랜치 컨벤션

- 브랜치는 **이슈 번호**와 연결하여 생성  
- 형식: `feat/#이슈번호`, `fix/#이슈번호`, `refactor/#이슈번호` 등  

### 예시
- feat/#1: 꿈 목록 조회 API 구현  
- fix/#2: 로그인 에러 수정  
- refactor/#3: JWT 인증 로직 개선  
