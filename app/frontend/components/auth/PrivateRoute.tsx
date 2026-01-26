'use client';

import { useEffect } from 'react';
import { useRouter, usePathname } from 'next/navigation';
import { useAuth } from '@/contexts/AuthContext';
import { UserRole } from '@/types/api/user';

interface PrivateRouteProps {
  children: React.ReactNode;
  roles?: UserRole[];
}

export function PrivateRoute({ children, roles }: PrivateRouteProps) {
  const { user, isLoading } = useAuth();
  const router = useRouter();
  const pathname = usePathname();

  useEffect(() => {
    if (isLoading) return;

    if (!user) {
      const redirectUrl = encodeURIComponent(pathname);
      router.push(`/login?redirect=${redirectUrl}`);
      return;
    }

    if (roles && roles.length > 0 && !roles.includes(user.role as UserRole)) {
      router.push('/');
    }
  }, [user, isLoading, roles, router, pathname]);

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-white">Loading...</div>
      </div>
    );
  }

  if (!user) {
    return null;
  }

  if (roles && roles.length > 0 && !roles.includes(user.role as UserRole)) {
    return null;
  }

  return <>{children}</>;
}
