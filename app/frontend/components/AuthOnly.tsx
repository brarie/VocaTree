'use client';

import { useAuth } from '@/hooks/useAuth';

interface AuthOnlyProps {
  children: React.ReactNode;
  fallback?: React.ReactNode;
}

/**
 * 로그인한 사용자에게만 children을 보여주는 컴포넌트
 * 로그인하지 않은 경우 fallback을 보여주거나 아무것도 렌더링하지 않음
 */
export function AuthOnly({ children, fallback = null }: AuthOnlyProps) {
  const { isAuthenticated, isLoading } = useAuth();

  // 로딩 중에는 fallback 표시
  if (isLoading) {
    return <>{fallback}</>;
  }

  // 인증되지 않으면 fallback 또는 null
  if (!isAuthenticated) {
    return <>{fallback}</>;
  }

  // 인증됨 - children 렌더링
  return <>{children}</>;
}
