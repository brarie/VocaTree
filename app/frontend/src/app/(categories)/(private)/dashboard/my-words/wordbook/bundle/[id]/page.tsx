'use client';

import { Suspense, useEffect } from 'react';
import { useParams, useSearchParams, useRouter } from 'next/navigation';
import { ArrowLeft } from 'lucide-react';
import { usePaginateNodesInBundle, useBundle } from '@/hooks/wordbook/queries';
import { usePageHeader } from '@/contexts/PageHeaderContext';
import { Button } from '@/components/ui/button';
import { PaginationBar } from '@/components/PaginationBar';
import { WordbookCard } from '../../_components/WordbookCard';

export function BundleContent() {
  const router = useRouter();
  const params = useParams();
  const searchParams = useSearchParams();

  const bundleId = parseInt(params.id as string, 10);
  const page = parseInt(searchParams.get('page') || '0', 10);
  const size = parseInt(searchParams.get('size') || '30', 10);

  const { data: bundle, isLoading: isBundleLoading } = useBundle(bundleId);
  const { data: pageData, isLoading: isPageLoading } = usePaginateNodesInBundle(
    bundleId,
    page,
    size,
  );
  const { setPageHeader, clearPageHeader } = usePageHeader();

  const isLoading = isBundleLoading || isPageLoading;

  useEffect(() => {
    if (bundle) {
      setPageHeader(
        bundle.title,
        '단어장 모음: 비슷한 단어장들을 묶어서 정리할 수 있어요.',
      );
    }
    return () => {
      clearPageHeader();
    };
  }, [bundle, setPageHeader, clearPageHeader]);

  const updateParams = (updates: Record<string, string | number | boolean>) => {
    const params = new URLSearchParams(searchParams.toString());

    Object.entries(updates).forEach(([key, value]) => {
      if (
        value === '' ||
        value === null ||
        value === undefined ||
        (key === 'page' && value === 0) ||
        (key === 'size' && value === 30)
      ) {
        params.delete(key);
      } else {
        params.set(key, String(value));
      }
    });

    const newUrl = params.toString()
      ? `?${params.toString()}`
      : window.location.pathname;
    router.replace(newUrl, { scroll: false });
  };

  const handlePageChange = (newPage: number) => {
    updateParams({ page: newPage });
  };

  const handleSizeChange = (newSize: number) => {
    updateParams({ page: 0, size: newSize });
  };

  if (isLoading) {
    return (
      <div className="text-center py-12 text-muted-foreground">로딩 중...</div>
    );
  }

  if (!bundle) {
    return (
      <div className="text-center py-12 text-muted-foreground">
        단어장 모음을 찾을 수 없습니다.
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <Button
        variant="ghost"
        size="sm"
        onClick={() => router.push('/dashboard/my-words/wordbook')}
        className="gap-2"
      >
        <ArrowLeft className="w-4 h-4" />
        뒤로가기
      </Button>

      {pageData && pageData.content.length > 0 ? (
        <>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {pageData.content.map((node) => (
              <WordbookCard key={node.id} node={node} hideStar={true} />
            ))}
          </div>

          <PaginationBar
            itemsPerPageOptions={[15, 30, 45]}
            pageData={pageData}
            onPageChange={handlePageChange}
            onItemsPerPageChange={handleSizeChange}
          />
        </>
      ) : (
        <div className="text-center py-12 text-muted-foreground">
          이 단어장 모음에는 아직 단어장이 없습니다.
        </div>
      )}
    </div>
  );
}

export default function BundleDetailPage() {
  return (
    <Suspense fallback={<div>로딩 중...</div>}>
      <BundleContent />
    </Suspense>
  );
}
