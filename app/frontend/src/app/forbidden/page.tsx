'use client';

import Link from 'next/link';
import { Button } from '@/components/ui/button';
import { ShieldAlert } from 'lucide-react';

export default function ForbiddenPage() {
  return (
    <div className="min-h-screen flex items-center justify-center px-4">
      <div className="text-center space-y-6 max-w-md">
        <div className="flex justify-center">
          <ShieldAlert className="w-20 h-20 text-red-500" />
        </div>
        <div>
          <h1 className="text-4xl font-bold text-white mb-2">접근 권한이 없습니다</h1>
          <p className="text-gray-400">
            이 페이지에 접근할 권한이 없습니다.
            <br />
            관리자 권한이 필요합니다.
          </p>
        </div>
        <div className="flex gap-3 justify-center">
          <Button asChild variant="outline" className="bg-white/5 border-white/10 text-white hover:bg-white/10">
            <Link href="/">홈으로</Link>
          </Button>
          <Button asChild className="bg-white text-black hover:bg-gray-200">
            <Link href="/support">도움마당</Link>
          </Button>
        </div>
      </div>
    </div>
  );
}
