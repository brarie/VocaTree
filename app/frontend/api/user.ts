import {
  UserInfo,
  SignUpRequest,
  SignUpResponse,
  VerifyRequest,
  ResendVerificationRequest,
} from '@/types/api/user';
import { fetcherWithAuth, fetcher } from '@/lib/fetcher';
import { handleApiResponse } from '@/lib/error';

export async function getCurrentUser(): Promise<UserInfo> {
  const response = await fetcherWithAuth('/api/users/me');
  await handleApiResponse(response, 'Failed to fetch user info');

  const data: UserInfo = await response.json();
  return data;
}

export async function signUp(data: SignUpRequest): Promise<SignUpResponse> {
  const response = await fetcher('/api/users/sign-up', {
    method: 'POST',
    body: data,
  });

  await handleApiResponse(response, 'Sign up failed');

  const result: SignUpResponse = await response.json();
  return result;
}

export async function verify(data: VerifyRequest): Promise<void> {
  const response = await fetcher('/api/users/verify', {
    method: 'POST',
    body: data,
  });

  await handleApiResponse(response, 'Verification failed');
}

export async function resendVerification(
  data: ResendVerificationRequest,
): Promise<void> {
  const response = await fetcher('/api/users/resend-verification', {
    method: 'PATCH',
    body: data,
  });

  await handleApiResponse(response, 'Resend verification failed');
}
