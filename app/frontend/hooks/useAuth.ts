'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';

export function useAuth() {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const router = useRouter();

  useEffect(() => {
    // TODO: 실제 인증 로직으로 교체
    const checkAuth = () => {
      try {
        const token = localStorage.getItem('auth_token');
        setIsAuthenticated(!!token);
      } catch (error) {
        setIsAuthenticated(false);
      } finally {
        setIsLoading(false);
      }
    };

    checkAuth();
  }, []);

  const logout = () => {
    try {
      // TODO: 실제 로그아웃 로직으로 교체 (API 호출 등)
      localStorage.removeItem('auth_token');
      setIsAuthenticated(false);
      router.push('/');
    } catch (error) {
      console.error('로그아웃 실패:', error);
    }
  };

  return { isAuthenticated, isLoading, logout };
}
