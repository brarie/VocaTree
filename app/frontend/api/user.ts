import {
  UserInfo,
  SignUpRequest,
  SignUpResponse,
  VerifyRequest,
  ResendVerificationRequest,
} from '@/types/api/user';
import { fetcherWithAuth, fetcher } from '@/lib/fetcher';

export async function getCurrentUser(): Promise<UserInfo> {
  const response = await fetcherWithAuth('/api/users/me');

  if (!response.ok) {
    const error = await response.json().catch(() => ({ message: 'Failed to fetch user info' }));
    throw new Error(error.message || 'Failed to fetch user info');
  }

  const data: UserInfo = await response.json();
  return data;
}

export async function signUp(data: SignUpRequest): Promise<SignUpResponse> {
  const response = await fetcher('/api/users/sign-up', {
    method: 'POST',
    body: data,
  });

  if (!response.ok) {
    const error = await response.json().catch(() => ({ message: 'Sign up failed' }));
    throw new Error(error.message || 'Sign up failed');
  }

  const result: SignUpResponse = await response.json();
  return result;
}

export async function verify(data: VerifyRequest): Promise<void> {
  const response = await fetcher('/api/users/verify', {
    method: 'POST',
    body: data,
  });

  if (!response.ok) {
    const error = await response.json().catch(() => ({ message: 'Verification failed' }));
    throw new Error(error.message || 'Verification failed');
  }
}

export async function resendVerification(data: ResendVerificationRequest): Promise<void> {
  const response = await fetcher('/api/users/resend-verification', {
    method: 'PATCH',
    body: data,
  });

  if (!response.ok) {
    const error = await response.json().catch(() => ({ message: 'Resend verification failed' }));
    throw new Error(error.message || 'Resend verification failed');
  }
}
