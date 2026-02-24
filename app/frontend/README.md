# VocaTree Frontend

Next.js 16 기반 영어 학습 플랫폼 프론트엔드

## 환경 설정

### 1. 환경변수 설정

프로젝트 루트에 `.env.local` 파일 생성:

```bash
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080
```

### 2. 의존성 설치

```bash
pnpm install
```

### 3. 개발 서버 실행

```bash
pnpm dev
```

브라우저에서 [http://localhost:3000](http://localhost:3000) 접속

## 주요 개념

### Context API를 통한 전역 인증 상태 관리

- **위치**: `contexts/AuthContext.tsx`
- **사용**: `import { useAuth } from '@/contexts/AuthContext'`
- 모든 컴포넌트가 동일한 인증 상태 공유
- 로그아웃 시 전체 앱이 즉시 업데이트

### 자동 로그인

- 앱 로드 시 refresh token(쿠키)이 있으면 자동으로 새 access token 발급
- 사용자가 브라우저를 닫았다가 다시 열어도 로그인 유지

### SSR & Hydration

- Next.js는 서버에서 먼저 HTML 생성 후 클라이언트에서 hydration
- `typeof window === 'undefined'`: 서버(Node.js) 환경 체크
- `mounted` 상태: Hydration 에러 방지용 (서버/클라이언트 일치)

### API Fetcher

- **fetcherWithAuth**: 인증 필요 API (자동 Bearer token, 자동 refresh)
- **fetcher**: 공개 API (로그인, 회원가입 등)

## 프로젝트 구조

```
frontend/
├── .env.local              # 환경변수 (git에 포함 안 됨)
├── src/app/                # Next.js App Router
├── api/                    # API 함수 (도메인별)
├── components/             # 공통 컴포넌트
├── contexts/               # React Context (AuthContext)
├── hooks/                  # Custom Hooks
├── lib/                    # 유틸리티 (fetcher, utils)
└── types/                  # TypeScript 타입 정의
```

## 자세한 문서

- **프론트엔드 전체 문서**: `CLAUDE.md`
- **백엔드 문서**: `../backend/claude.md`

## 기술 스택

- Next.js 16 (App Router)
- TypeScript 5
- Tailwind CSS 4
- shadcn/ui
- Framer Motion
- React Hook Form + Zod
