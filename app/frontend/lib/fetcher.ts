const ACCESS_TOKEN_KEY = 'access_token';
const TOKEN_EXPIRY_KEY = 'token_expiry';

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ?? 'http://localhost:8080';

export const tokenStorage = {
  getAccessToken: (): string | null => {
    if (typeof window === 'undefined') return null;
    return localStorage.getItem(ACCESS_TOKEN_KEY);
  },

  setAccessToken: (token: string, expirySec: number): void => {
    if (typeof window === 'undefined') return;
    localStorage.setItem(ACCESS_TOKEN_KEY, token);
    const expiryTime = Date.now() + expirySec * 1000;
    localStorage.setItem(TOKEN_EXPIRY_KEY, expiryTime.toString());
  },

  clearTokens: (): void => {
    if (typeof window === 'undefined') return;
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(TOKEN_EXPIRY_KEY);
  },

  isTokenExpired: (): boolean => {
    if (typeof window === 'undefined') return true;
    const expiry = localStorage.getItem(TOKEN_EXPIRY_KEY);
    if (!expiry) return true;
    return Date.now() >= parseInt(expiry);
  },
};

let isRefreshing = false;
let refreshPromise: Promise<boolean> | null = null;

async function refreshAccessToken(): Promise<boolean> {
  try {
    const response = await fetch(`${API_BASE_URL}/api/auth/refresh`, {
      method: 'POST',
      credentials: 'include',
    });

    if (!response.ok) {
      tokenStorage.clearTokens();
      return false;
    }

    const data = await response.json();
    tokenStorage.setAccessToken(data.accessToken, data.accessExpSec);
    return true;
  } catch (error) {
    tokenStorage.clearTokens();
    return false;
  }
}

interface FetcherOptions extends Omit<RequestInit, 'body'> {
  body?: any;
}

function prepareRequest(options: FetcherOptions): RequestInit {
  const headers = new Headers(options.headers);

  let processedBody = options.body;

  if (options.body && typeof options.body === 'object') {
    if (!headers.has('Content-Type')) {
      headers.set('Content-Type', 'application/json');
    }
    processedBody = JSON.stringify(options.body);
  }

  return {
    ...options,
    headers,
    body: processedBody,
    credentials: 'include',
  };
}

export async function fetcherWithAuth(
  url: string,
  options: FetcherOptions = {},
): Promise<Response> {
  const fullUrl = url.startsWith('http') ? url : `${API_BASE_URL}${url}`;

  let token = tokenStorage.getAccessToken();

  if (!token || tokenStorage.isTokenExpired()) {
    if (isRefreshing) {
      await refreshPromise;
      token = tokenStorage.getAccessToken();
    } else {
      isRefreshing = true;
      refreshPromise = refreshAccessToken();
      const success = await refreshPromise;
      isRefreshing = false;
      refreshPromise = null;

      if (!success) {
        throw new Error('Authentication required');
      }
      token = tokenStorage.getAccessToken();
    }
  }

  const requestInit = prepareRequest(options);
  const headers = new Headers(requestInit.headers);

  if (token) {
    headers.set('Authorization', `Bearer ${token}`);
  }

  return fetch(fullUrl, {
    ...requestInit,
    headers,
  });
}

export async function fetcher(
  url: string,
  options: FetcherOptions = {},
): Promise<Response> {
  const fullUrl = url.startsWith('http') ? url : `${API_BASE_URL}${url}`;
  const requestInit = prepareRequest(options);

  return fetch(fullUrl, requestInit);
}

export { API_BASE_URL };
