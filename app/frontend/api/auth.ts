import { LoginRequest, LoginResponse } from '@/types/api/auth';
import { tokenStorage, fetcher, fetcherWithAuth } from '@/lib/fetcher';
import { handleApiResponse } from '@/lib/error';

export async function login(data: LoginRequest): Promise<LoginResponse> {
  const response = await fetcher('/api/auth/login', {
    method: 'POST',
    body: data,
  });

  await handleApiResponse(response, 'Login failed');

  const result: LoginResponse = await response.json();
  tokenStorage.setAccessToken(result.accessToken, result.accessExpSec);

  return result;
}

export async function logout(): Promise<void> {
  try {
    await fetcherWithAuth('/api/auth/logout', {
      method: 'POST',
    });
  } finally {
    tokenStorage.clearTokens();
  }
}
