'use client';

import { TrendingUp, Settings, BookOpen } from 'lucide-react';
import { PageHeader } from '@/components/PageHeader';
import { DictionarySearchBar } from './_components/DictionarySearchBar';
import { AuthOnly } from '@/components/auth/AuthOnly';
import { Button } from '@/components/ui/button';
import { useDictionaryCounts } from '@/hooks/dictionary/queries';
import Link from 'next/link';

export default function DictionaryPage() {
  const { data: counts, isLoading } = useDictionaryCounts();

  return (
    <div className="space-y-6">
      <PageHeader
        title="공용 사전"
        description="영어 단어를 검색하고 다양한 의미와 예문을 확인하세요"
      >
        <AuthOnly roles={['ADMIN']}>
          <Link href="/admin/dictionary">
            <Button
              variant="outline"
              className="bg-white/5 border-white/10 text-white hover:bg-white/10 mt-3"
            >
              <Settings className="w-4 h-4 mr-2" />
              관리
            </Button>
          </Link>
        </AuthOnly>
      </PageHeader>

      <div className="max-w-2xl mx-auto mt-7">
        <DictionarySearchBar showHistory={true} />
      </div>

      <div className="max-w-2xl mx-auto mt-7 space-y-6">
        <div className="bg-zinc-900 rounded-lg p-6 border border-zinc-800">
          <div className="flex items-center gap-2 mb-4">
            <TrendingUp className="h-5 w-5 text-blue-400" />
            <h2 className="text-lg font-semibold">빠른 시작</h2>
          </div>
          <p className="text-sm text-gray-400 mb-4">
            공용 사전을 통해 영어 단어의 의미와 예문을 확인하세요.
          </p>
          <ul className="text-sm text-gray-400 space-y-2">
            <li className="flex items-start gap-2">
              <span className="text-blue-400">•</span>
              <span>단어 검색 및 자동완성</span>
            </li>
            <li className="flex items-start gap-2">
              <span className="text-blue-400">•</span>
              <span>상세한 의미와 예문</span>
            </li>
            <li className="flex items-start gap-2">
              <span className="text-blue-400">•</span>
              <span>발음 기호 (영국/미국)</span>
            </li>
          </ul>
        </div>

        {!isLoading && counts && (
          <div className="bg-zinc-900 rounded-lg p-6 border border-zinc-800">
            <div className="flex items-start justify-between mb-4">
              <div>
                <h3 className="text-lg font-semibold text-white mb-2">
                  사전 통계
                </h3>
                <p className="text-sm text-gray-400">
                  현재까지
                  <span className="text-white font-semibold">
                    {counts.totalWords}개
                  </span>
                  의 단어,
                  <span className="text-white font-semibold">
                    {counts.totalSenses}개
                  </span>
                  의 뜻,
                  <span className="text-white font-semibold">
                    {counts.totalExamples}개
                  </span>
                  의 예문이 등록되어 있습니다.
                </p>
              </div>
              <TrendingUp className="w-8 h-8 text-gray-400" />
            </div>
            <Link href="/dictionary/all">
              <Button
                variant="outline"
                className="bg-white/5 border-white/10 text-white hover:bg-white/10"
              >
                <BookOpen className="w-4 h-4 mr-2" />
                사전에 등록된 모든 단어 살펴보기
              </Button>
            </Link>
          </div>
        )}
      </div>
    </div>
  );
}
