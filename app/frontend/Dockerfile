# Dependencies stage
FROM node:20-alpine AS deps
WORKDIR /app

RUN corepack enable pnpm

COPY package.json pnpm-lock.yaml ./

RUN pnpm install --frozen-lockfile

# Build stage
FROM node:20-alpine AS build
WORKDIR /app

RUN corepack enable pnpm

COPY --from=deps /app/node_modules ./node_modules
COPY . .

# Build-time environment variable
# ARG의 경우 docker-compose에 정의된 arg가 우선됨.
# 빈 문자열: 코드에서 /api를 명시하므로 BASE_URL은 비워둠
ARG NEXT_PUBLIC_API_BASE_URL=
ENV NEXT_PUBLIC_API_BASE_URL=$NEXT_PUBLIC_API_BASE_URL

RUN pnpm build

# Runtime stage
FROM node:20-alpine AS runtime
WORKDIR /app

ENV NODE_ENV=production

RUN corepack enable pnpm

COPY --from=build /app/.next/standalone ./
COPY --from=build /app/.next/static ./.next/static
COPY --from=build /app/public ./public

EXPOSE 3000

CMD ["node", "server.js"]
