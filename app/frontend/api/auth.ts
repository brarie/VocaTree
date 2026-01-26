import { LoginRequest, LoginResponse } from '@/types/api/auth';
import { tokenStorage, fetcher, fetcherWithAuth } from '@/lib/fetcher';

export async function login(data: LoginRequest): Promise<LoginResponse> {
  const response = await fetcher('/api/auth/login', {
    method: 'POST',
    body: data,
  });

  if (!response.ok) {
    const error = await response.json().catch(() => ({ message: 'Login failed' }));
    throw new Error(error.message || 'Login failed');
  }

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
