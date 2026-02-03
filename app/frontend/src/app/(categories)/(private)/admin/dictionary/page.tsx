'use client';

import { Suspense } from 'react';
import { PageHeader } from '@/components/PageHeader';
import { PrivateRoute } from '@/components/auth/PrivateRoute';
import { WordManagement } from './_components/WordManagement';

function DictionaryAdminContent() {
  return (
    <div className="space-y-8">
      <PageHeader
        title="사전 관리"
        description="단어와 의미, 예문을 추가하고 수정하세요"
      />
      <WordManagement />
    </div>
  );
}

export default function DictionaryAdminPage() {
  return (
    <PrivateRoute roles={['ADMIN']}>
      <Suspense fallback={<div>로딩 중...</div>}>
        <DictionaryAdminContent />
      </Suspense>
    </PrivateRoute>
  );
}
