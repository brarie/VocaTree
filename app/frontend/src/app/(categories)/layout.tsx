'use client';

import { Header } from '@/components/Header';
import { DashboardHeader } from '../../../components/DashboardHeader';
import { AuthOnly } from '@/components/auth/AuthOnly';

export default function CategoriesLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="flex flex-col min-h-screen">
      <Header />
      <AuthOnly>
        <DashboardHeader />
      </AuthOnly>
      {children}
    </div>
  );
}
