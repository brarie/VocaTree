'use client';

import { Suspense } from 'react';
import { ArrowLeft } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { PageHeader } from '@/components/PageHeader';
import { PaginationBar } from '@/components/PaginationBar';
import { SimpleWordList } from '../_components/WordList';
import { useAllWords } from '@/hooks/dictionary/queries';
import { usePageParams } from '@/hooks/usePageParams';
import Link from 'next/link';

function AllWordsContent() {
  const { pageParams, setPage, setSize } = usePageParams({ defaultSize: 10 });
  const { data: pageData, isLoading } = useAllWords(
    pageParams.page,
    pageParams.size,
  );

  return (
    <div className="space-y-6">
      <PageHeader
        title="모든 단어 보기"
        description="사전에 등록된 모든 영어 단어를 탐색합니다"
      >
        <Link href="/dictionary">
          <Button
            variant="outline"
            className="bg-white/5 border-white/10 text-white hover:bg-white/10 mt-3"
          >
            <ArrowLeft className="w-4 h-4 mr-2" />
            돌아가기
          </Button>
        </Link>
      </PageHeader>

      {isLoading ? (
        <div className="text-center py-12">
          <div className="inline-block h-8 w-8 animate-spin rounded-full border-4 border-solid border-current border-r-transparent"></div>
          <p className="mt-4 text-gray-400">로딩 중...</p>
        </div>
      ) : pageData && pageData.content.length > 0 ? (
        <>
          <SimpleWordList words={pageData.content} />
          <PaginationBar
            pageData={pageData}
            onPageChange={setPage}
            onItemsPerPageChange={setSize}
          />
        </>
      ) : (
        <div className="text-center py-12 bg-zinc-900 rounded-lg border border-zinc-800">
          <p className="text-gray-400">등록된 단어가 없습니다.</p>
        </div>
      )}
    </div>
  );
}

export default function AllWordsPage() {
  return (
    <Suspense
      fallback={
        <div className="text-center py-12 text-gray-400">로딩 중...</div>
      }
    >
      <AllWordsContent />
    </Suspense>
  );
}
