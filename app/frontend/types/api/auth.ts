import { z } from 'zod';

export const LoginRequestSchema = z.object({
  username: z.string(),
  rawPassword: z.string(),
});

export type LoginRequest = z.infer<typeof LoginRequestSchema>;

export interface LoginResponse {
  accessToken: string;
  accessExpSec: number;
}
