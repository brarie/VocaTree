'use client';

import { useAuth } from '@/contexts/AuthContext';
import { UserRole } from '@/types/api/user';

interface AuthOnlyProps {
  children: React.ReactNode;
  fallback?: React.ReactNode;
  roles?: UserRole[];
}

export function AuthOnly({ children, fallback = null, roles }: AuthOnlyProps) {
  const { user, isLoading } = useAuth();

  if (isLoading) {
    return <>{fallback}</>;
  }

  if (!user) {
    return <>{fallback}</>;
  }

  if (roles && roles.length > 0 && !roles.includes(user.role as UserRole)) {
    return <>{fallback}</>;
  }

  return <>{children}</>;
}
