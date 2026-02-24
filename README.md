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
- 데이터베이스: `vocatree`
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

### Prod 환경

**특징**

- MySQL database (영속성 볼륨)
- Redis (영속성 볼륨)
- Nginx + HTTPS (Let's Encrypt)

**사전 준비**

1. 서버에 Docker 설치

```bash
sudo apt update && sudo apt install -y docker.io docker-compose-plugin git
sudo usermod -aG docker $USER
```

docker-compose-plugin 안깔리면 인터넷 참고해서 환경 변경 필요

2. 프로젝트 클론

```bash
git clone https://github.com/brarie/VocaTree.git
cd VocaTree
```

3. 환경변수 설정

```bash
# .env.prod 파일 생성 (.env.prod.example 참고)
cp .env.prod.example .env.prod
nano .env.prod

# .env.mail 파일 생성
nano .env.mail
```

4. init.sql 준비 (dev에서 스키마 export)

```bash
docker exec vocatree-mysql-dev bash -c "mysqldump -u root -pdev_root_password --no-data vocatree > /tmp/init.sql"
docker cp vocatree-mysql-dev:/tmp/init.sql docker/mysql/init.sql
```

**첫 배포 (SSL 발급 전)**

```bash
# 1. nginx를 HTTP 전용으로 실행
# docker-compose.prod.yml의 nginx volumes를 nginx-prod-init.conf로 임시 변경
docker compose -f docker-compose.prod.yml up -d

# 2. SSL 인증서 발급 (가비아에서 도메인 A레코드 설정 완료 후)
docker run -it --rm \
  -v $(pwd)/docker/nginx/ssl:/etc/letsencrypt \
  -v $(pwd)/docker/nginx/certbot:/var/www/certbot \
  certbot/certbot certonly --webroot \
  -w /var/www/certbot \
  -d vocatree.com -d www.vocatree.com \
  --email prarie77@gmail.com --agree-tos --no-eff-email

# 3. nginx를 HTTPS 설정으로 전환 (nginx-prod.conf로 복구)
docker compose -f docker-compose.prod.yml restart nginx
```

**접속**

- https://vocatree.com

---

## 주요 명령어
