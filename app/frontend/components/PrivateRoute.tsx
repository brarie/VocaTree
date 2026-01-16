'use client';

import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';

interface PrivateRouteProps {
  children: React.ReactNode;
  fallback?: React.ReactNode;
}

export function PrivateRoute({ children, fallback }: PrivateRouteProps) {
  const router = useRouter();
  const [isAuthenticated, setIsAuthenticated] = useState<boolean | null>(null);

  useEffect(() => {
    // TODO: 실제 인증 로직으로 교체
    // 예: API 호출, 토큰 확인 등
    const checkAuth = async () => {
      try {
        // 임시: localStorage나 쿠키에서 토큰 확인
        const token = localStorage.getItem('auth_token');
        setIsAuthenticated(!!token);

        if (!token) {
          router.push('/login');
        }
      } catch (error) {
        setIsAuthenticated(false);
        router.push('/login');
      }
    };

    checkAuth();
  }, [router]);

  // 로딩 중
  if (isAuthenticated === null) {
    return fallback || <div className="flex items-center justify-center min-h-screen">Loading...</div>;
  }

  // 인증되지 않음
  if (!isAuthenticated) {
    return null;
  }

  // 인증됨
  return <>{children}</>;
}
