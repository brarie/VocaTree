import { z } from 'zod';

export const UserRole = {
  USER: 'USER',
  ADMIN: 'ADMIN',
} as const;

export type UserRole = (typeof UserRole)[keyof typeof UserRole];

export interface UserInfo {
  id: string;
  username: string;
  email: string;
  nickname: string;
  role: UserRole;
}

// Sign Up Request (Zod schema)
export const SignUpRequestSchema = z.object({
  username: z
    .string()
    .regex(
      /^[A-Za-z0-9]{4,10}$/,
      '아이디는 영문, 숫자를 조합하여 4~10자로 설정해주세요.',
    ),
  email: z
    .string()
    .regex(
      /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/,
      '이메일 형식이 올바르지 않습니다.',
    ),
  nickname: z
    .string()
    .regex(
      /^[A-Za-z0-9가-힣]{2,8}$/,
      '닉네임은 영문, 숫자, 한글을 조합하여 2~8자로 설정해주세요.',
    ),
  rawPassword: z
    .string()
    .regex(
      /^[A-Za-z\d!@#$%^&*()_+\-=\[\]{};':"\\|,.<>/?]{8,20}$/,
      '비밀번호는 영문, 숫자, 특수문자를 조합하여 8~20자로 설정해주세요.',
    ),
});

export type SignUpRequest = z.infer<typeof SignUpRequestSchema>;

export interface SignUpResponse {
  tempKey: string;
}

export const VerifyRequestSchema = z.object({
  code: z.string(),
});

export type VerifyRequest = z.infer<typeof VerifyRequestSchema>;

export const ResendVerificationRequestSchema = z.object({
  tempKey: z.string(),
});

export type ResendVerificationRequest = z.infer<
  typeof ResendVerificationRequestSchema
>;
