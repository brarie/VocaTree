'use client';

import { Suspense, useEffect, useState } from 'react';
import { useParams, useRouter, useSearchParams } from 'next/navigation';
import { PageHeader } from '@/components/PageHeader';
import { PaginationBar } from '@/components/PaginationBar';
import { usePageParams } from '@/hooks/usePageParams';
import {
  useSharedWordbookDetail,
  useSharedWordbookVersions,
  useSharedWordsByVersion,
} from '@/hooks/library/queries';
import { TrustedLevelBadge } from '../../_components/TrustedLevelBadge';
import { SharedWordbookWordsTable } from '../../_components/SharedWordbookWordsTable';
import { Button } from '@/components/ui/button';
import { User, Calendar, ArrowLeft, BookOpen } from 'lucide-react';
import Link from 'next/link';

type Tab = 'info' | 'versions';

function WordbookDetailContent() {
  const params = useParams();
  const router = useRouter();
  const searchParams = useSearchParams();
  const slug = params.slug as string;

  const activeTab = (searchParams.get('tab') as Tab) || 'info';
  const { pageParams, setPage, setSize } = usePageParams({ defaultSize: 10 });

  const [selectedVersion, setSelectedVersion] = useState<number | null>(null);

  function setTab(tab: Tab) {
    const next = new URLSearchParams(searchParams.toString());
    next.set('tab', tab);
    next.delete('page');
    router.push(`?${next.toString()}`, { scroll: false });
  }

  const { data: detail, isLoading, isError } = useSharedWordbookDetail(slug);

  // Canonical redirect: 타이틀이 바뀌어 slug가 달라진 경우 최신 URL로 이동
  useEffect(() => {
    if (detail && detail.slug !== slug) {
      router.replace(`/library/wordbook/${detail.slug}`);
    }
  }, [detail, slug, router]);

  const versionsQuery = useSharedWordbookVersions(
    detail?.id ?? 0,
    pageParams.page,
    pageParams.size,
    activeTab === 'versions' && !!detail?.id,
  );

  // 명시적으로 선택된 버전이 없으면 목록의 첫 번째(최신) 버전을 기본으로 사용
  const effectiveVersion =
    selectedVersion ?? versionsQuery.data?.content[0]?.version ?? null;

  const wordsQuery = useSharedWordsByVersion(
    detail?.id ?? 0,
    effectiveVersion ?? 0,
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
          단어장을 찾을 수 없습니다.
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
        description={`최신 버전: v${detail.latestVersion}`}
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
            { key: 'versions', label: '버전 목록' },
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

      {/* 버전 목록 탭 */}
      {activeTab === 'versions' && (
        <div className="space-y-3">
          {versionsQuery.isLoading && (
            <>
              {[...Array(3)].map((_, i) => (
                <div
                  key={i}
                  className="h-16 bg-zinc-900 rounded-lg border border-zinc-800 animate-pulse"
                />
              ))}
            </>
          )}

          {versionsQuery.data && versionsQuery.data.content.length === 0 && (
            <div className="flex items-center justify-center py-12 bg-white/5 rounded-lg border border-white/10">
              <p className="text-sm text-gray-400">버전 정보가 없습니다.</p>
            </div>
          )}

          {versionsQuery.data &&
            versionsQuery.data.content.map((v) => (
              <button
                key={v.id}
                onClick={() =>
                  setSelectedVersion(
                    effectiveVersion === v.version ? null : v.version,
                  )
                }
                className={`w-full rounded-lg border p-4 flex items-center justify-between transition-colors text-left ${
                  effectiveVersion === v.version
                    ? 'bg-zinc-800 border-white/20'
                    : 'bg-zinc-900 border-zinc-800 hover:border-zinc-600'
                }`}
              >
                <div className="flex items-center gap-3">
                  <span className="text-[11px] px-2 py-0.5 rounded bg-zinc-700 text-zinc-300 font-medium shrink-0">
                    v{v.version}
                  </span>
                  <div>
                    <p className="text-sm font-medium text-white">
                      {v.versionNote ?? '버전 메모 없음'}
                    </p>
                    <p className="text-xs text-zinc-500 mt-0.5">
                      단어 {v.wordCount}개 ·{' '}
                      {v.revisedAt.slice(0, 10).replace(/-/g, '.')}
                    </p>
                  </div>
                </div>
                <BookOpen
                  className={`w-4 h-4 shrink-0 transition-colors ${
                    effectiveVersion === v.version ? 'text-white' : 'text-zinc-600'
                  }`}
                />
              </button>
            ))}

          {versionsQuery.data && versionsQuery.data.totalElements > 0 && (
            <PaginationBar
              pageData={versionsQuery.data}
              onPageChange={setPage}
              onItemsPerPageChange={setSize}
              className="pt-2"
            />
          )}

          {/* 선택된 버전의 단어 목록 */}
          {effectiveVersion !== null && (
            <div className="pt-2 space-y-3">
              <div className="flex items-center gap-2">
                <span className="text-[11px] px-2 py-0.5 rounded bg-zinc-700 text-zinc-300 font-medium">
                  v{effectiveVersion}
                </span>
                <p className="text-sm text-gray-400">단어 목록</p>
              </div>

              {wordsQuery.isLoading && (
                <div className="h-48 bg-zinc-900 rounded-lg border border-zinc-800 animate-pulse" />
              )}

              {wordsQuery.data && (
                <SharedWordbookWordsTable words={wordsQuery.data} />
              )}
            </div>
          )}
        </div>
      )}
    </div>
  );
}

export default function WordbookDetailPage() {
  return (
    <Suspense
      fallback={
        <div className="text-center py-12 text-gray-400">로딩 중...</div>
      }
    >
      <WordbookDetailContent />
    </Suspense>
  );
}
