'use client';

import { PageHeader } from '@/components/PageHeader';
import { CategoryManagement } from './_components/CategoryManagement';
import { PostManagement } from './_components/PostManagement';
import { PrivateRoute } from '@/components/auth/PrivateRoute';

export default function AdminSupportPage() {
  return (
    <PrivateRoute roles={['ADMIN']}>
      <div className="space-y-8">
        <PageHeader
          title="도움마당 관리"
          description="카테고리와 게시글을 관리합니다."
        />
        <CategoryManagement />
        <PostManagement />
      </div>
    </PrivateRoute>
  );
}
