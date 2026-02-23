# VocaTree

영어 단어 학습 웹 애플리케이션

---

## 기술 스택

**Backend**

- Kotlin 2.1, Spring Boot 3.5, Java 21
- ORM: Exposed (JetBrains)
- Database: H2 (local), MySQL 8 (dev)
- Cache: Redis 7
- Authentication: JWT (Spring Security)

**Frontend**

- Next.js 16 (App Router), TypeScript 5
- Styling: Tailwind CSS 4
- UI: shadcn/ui (Radix UI)
- State: TanStack Query (React Query v5)
- Package Manager: pnpm

**Infrastructure**

- Docker, Docker Compose
- Nginx (reverse proxy)

---

## 환경 구성

### Local 환경

**특징**

- H2 in-memory database
- Backend/Frontend 직접 포트 노출
- 초기 데이터 자동 생성

**실행**

```bash
# 환경변수 설정 (.env.mail 파일 생성)
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_gmail_app_password

# 실행
docker-compose -f docker-compose.local.yml up --build

# 백그라운드 실행
docker-compose -f docker-compose.local.yml up -d
```

**접속**

- Frontend: http://localhost:3000
- Backend: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui/index.html
- H2 Console: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: (비워두기)

**초기 계정**

- 관리자: `admin` / `12121212`
- 일반: `user` / `12121212`

---

### Dev 환경

**특징**

- MySQL database (영속성 볼륨)
- Nginx reverse proxy
- 실제 운영 환경과 유사한 구성

**실행**

```bash
# .env.mail 파일 있어야 함 (Local과 동일)

# 실행
docker-compose -f docker-compose.dev.yml up --build

# 백그라운드 실행
docker-compose -f docker-compose.dev.yml up -d
```

**접속**

- Frontend/Backend: http://localhost
  - Frontend: `/`
  - Backend API: `/api/**`

**데이터베이스**

- MySQL root 비밀번호: `dev_root_password` (하드코딩)
- 데이터베이스: `monovoca`
- 스키마 자동 생성: enabled

---

## 주요 명령어

```bash
# 시작
docker-compose -f docker-compose.{환경}.yml up

# 재빌드 & 시작
docker-compose -f docker-compose.{환경}.yml up --build

# 중지
docker-compose -f docker-compose.{환경}.yml down

# 볼륨까지 삭제 (데이터 초기화)
docker-compose -f docker-compose.{환경}.yml down -v

# 로그 확인
docker-compose -f docker-compose.{환경}.yml logs -f

# 특정 서비스 재시작
docker-compose -f docker-compose.{환경}.yml restart backend
```

---

## 프로필 설명

| 프로필 | Database       | DDL 생성 | 초기데이터 | 용도      |
| ------ | -------------- | -------- | ---------- | --------- |
| local  | H2 (in-memory) | O        | O          | 로컬 개발 |
| dev    | MySQL          | O        | X          | 개발 서버 |

---

**문의**: monovoca0@gmail.com
