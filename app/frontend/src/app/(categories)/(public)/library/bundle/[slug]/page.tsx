'use client';

import { Suspense, useEffect } from 'react';
import { useParams, useRouter, useSearchParams } from 'next/navigation';
import { PageHeader } from '@/components/PageHeader';
import { PaginationBar } from '@/components/PaginationBar';
import { usePageParams } from '@/hooks/usePageParams';
import {
  useSharedBundleDetail,
  useWordbooksInBundle,
} from '@/hooks/library/queries';
import { TrustedLevelBadge } from '../../_components/TrustedLevelBadge';
import { SharedWordbookCard } from '../../_components/SharedWordbookCard';
import { Button } from '@/components/ui/button';
import { User, Calendar, Layers, ArrowLeft } from 'lucide-react';
import Link from 'next/link';

type Tab = 'info' | 'wordbooks';

function BundleDetailContent() {
  const params = useParams();
  const router = useRouter();
  const searchParams = useSearchParams();
  const slug = params.slug as string;

  const activeTab = (searchParams.get('tab') as Tab) || 'info';
  const { pageParams, setPage, setSize } = usePageParams({ defaultSize: 10 });

  function setTab(tab: Tab) {
    const next = new URLSearchParams(searchParams.toString());
    next.set('tab', tab);
    next.delete('page');
    router.push(`?${next.toString()}`, { scroll: false });
  }

  const { data: detail, isLoading, isError } = useSharedBundleDetail(slug);

  // Canonical redirect: 타이틀이 바뀌어 slug가 달라진 경우 최신 URL로 이동
  useEffect(() => {
    if (detail && detail.slug !== slug) {
      router.replace(`/library/bundle/${detail.slug}`);
    }
  }, [detail, slug, router]);

  const wordbooksQuery = useWordbooksInBundle(
    detail?.id ?? 0,
    pageParams.page,
    pageParams.size,
    activeTab === 'wordbooks' && !!detail?.id,
  );

  if (isLoading) {
    return (
      <div className="space-y-6">
        <div className="h-16 bg-zinc-900 rounded-lg animate-pulse" />
        <div className="h-32 bg-zinc-900 rounded-lg animate-pulse" />
      </div>
    );
  }

  if (isError || !detail) {
    return (
      <div className="flex flex-col items-center justify-center py-20 bg-white/5 rounded-lg border border-white/10">
        <p className="text-sm font-medium text-gray-400">
          번들을 찾을 수 없습니다.
        </p>
        <Link
          href="/library"
          className="mt-3 text-xs text-gray-500 hover:text-gray-300 transition-colors"
        >
          라이브러리로 돌아가기
        </Link>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <PageHeader
        title={detail.title}
        description={`단어장 ${detail.wordbookCount}개`}
      >
        <Link href="/library">
          <Button
            variant="outline"
            className="bg-white/5 border-white/10 text-white hover:bg-white/10 mt-3"
          >
            <ArrowLeft className="w-4 h-4 mr-2" />
            돌아가기
          </Button>
        </Link>
      </PageHeader>

      {/* 탭 */}
      <div className="flex gap-1 border-b border-white/10">
        {(
          [
            { key: 'info', label: '정보' },
            { key: 'wordbooks', label: '단어장 목록' },
          ] as const
        ).map(({ key, label }) => (
          <button
            key={key}
            onClick={() => setTab(key)}
            className={`px-4 py-2.5 text-sm font-medium transition-colors relative ${
              activeTab === key
                ? 'text-white'
                : 'text-gray-400 hover:text-gray-200'
            }`}
          >
            {label}
            {activeTab === key && (
              <span className="absolute bottom-0 left-0 right-0 h-0.5 bg-white rounded-full" />
            )}
          </button>
        ))}
      </div>

      {/* 정보 탭 */}
      {activeTab === 'info' && (
        <div className="bg-zinc-900 rounded-lg border border-zinc-800 p-5 min-h-[calc(100vh-22rem)] flex flex-col gap-5">
          {/* 메타 정보 */}
          <div className="flex flex-wrap items-center gap-x-4 gap-y-2 text-sm text-gray-400 pb-4 border-b border-zinc-800">
            <div className="flex items-center gap-1.5">
              <User className="w-3.5 h-3.5" />
              <span>{detail.creator.nickname}</span>
            </div>
            <div className="flex items-center gap-1.5">
              <Calendar className="w-3.5 h-3.5" />
              <span>{detail.createdAt.slice(0, 10).replace(/-/g, '.')}</span>
            </div>
            <div className="flex items-center gap-1.5">
              <Layers className="w-3.5 h-3.5" />
              <span>단어장 {detail.wordbookCount}개</span>
            </div>
            <TrustedLevelBadge level={detail.trustedLevel} />
          </div>

          {/* 설명 */}
          <div className="flex-1">
            {detail.description ? (
              <p className="text-sm text-gray-300 leading-relaxed whitespace-pre-wrap">
                {detail.description}
              </p>
            ) : (
              <p className="text-sm text-zinc-500">설명이 없습니다.</p>
            )}
          </div>
        </div>
      )}

      {/* 단어장 목록 탭 */}
      {activeTab === 'wordbooks' && (
        <div className="space-y-4">
          {wordbooksQuery.isLoading && (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
              {[...Array(6)].map((_, i) => (
                <div
                  key={i}
                  className="h-40 rounded-xl bg-zinc-900 border border-zinc-800 animate-pulse"
                />
              ))}
            </div>
          )}

          {wordbooksQuery.data && wordbooksQuery.data.content.length === 0 && (
            <div className="flex items-center justify-center py-12 bg-white/5 rounded-lg border border-white/10">
              <p className="text-sm text-gray-400">단어장이 없습니다.</p>
            </div>
          )}

          {wordbooksQuery.data && wordbooksQuery.data.content.length > 0 && (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
              {wordbooksQuery.data.content.map((wb) => (
                <SharedWordbookCard key={wb.id} wordbook={wb} />
              ))}
            </div>
          )}

          {wordbooksQuery.data && wordbooksQuery.data.totalElements > 0 && (
            <PaginationBar
              pageData={wordbooksQuery.data}
              onPageChange={setPage}
              onItemsPerPageChange={setSize}
              className="pt-2"
            />
          )}
        </div>
      )}
    </div>
  );
}

export default function BundleDetailPage() {
  return (
    <Suspense
      fallback={
        <div className="text-center py-12 text-gray-400">로딩 중...</div>
      }
    >
      <BundleDetailContent />
    </Suspense>
  );
}
