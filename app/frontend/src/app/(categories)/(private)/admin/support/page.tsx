'use client';

import { CategoryManagement } from './_components/CategoryManagement';
import { PostManagement } from './_components/PostManagement';
import { PrivateRoute } from '@/components/auth/PrivateRoute';

export default function AdminSupportPage() {
  return (
    <PrivateRoute roles={['ADMIN']}>
      <div className="space-y-8">
        <div>
          <h1 className="text-3xl font-bold text-white mb-2">도움마당 관리</h1>
          <p className="text-gray-400">카테고리와 게시글을 관리합니다.</p>
        </div>

        <CategoryManagement />
        <PostManagement />
      </div>
    </PrivateRoute>
  );
}
