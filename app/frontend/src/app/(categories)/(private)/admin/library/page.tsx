'use client';

import { Suspense } from 'react';
import { PageHeader } from '@/components/PageHeader';
import { PrivateRoute } from '@/components/auth/PrivateRoute';
import { SharedWordbookAdminSection } from './_components/SharedWordbookAdminSection';
import { SharedBundleAdminSection } from './_components/SharedBundleAdminSection';

function LibraryAdminContent() {
  return (
    <div className="space-y-10">
      <PageHeader
        title="라이브러리 관리"
        description="공유 단어장과 번들의 신뢰 등급 및 차단을 관리합니다."
      />
      <SharedWordbookAdminSection />
      <div className="border-t border-zinc-800" />
      <SharedBundleAdminSection />
    </div>
  );
}

export default function LibraryAdminPage() {
  return (
    <PrivateRoute roles={['ADMIN']}>
      <Suspense
        fallback={
          <div className="text-center py-12 text-gray-400">로딩 중...</div>
        }
      >
        <LibraryAdminContent />
      </Suspense>
    </PrivateRoute>
  );
}
