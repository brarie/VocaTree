'use client';

import { Suspense, useState } from 'react';
import { useSearchParams } from 'next/navigation';
import { PageHeader } from '@/components/PageHeader';
import { PaginationBar } from '@/components/PaginationBar';
import { usePageParams } from '@/hooks/usePageParams';
import { useSharedWordbooks, useSharedBundles } from '@/hooks/library/queries';
import { LibraryFilterSidebar } from './_components/LibraryFilterSidebar';
import { LibrarySearchBar } from './_components/LibrarySearchBar';
import { SharedWordbookCard } from './_components/SharedWordbookCard';
import { SharedWordbookBundleCard } from './_components/SharedWordbookBundleCard';
import { AuthOnly } from '@/components/auth/AuthOnly';
import {
  TrustedLevel,
  SharedWordbookSortType,
  SharedWordbookBundleSortType,
} from '@/types/api/library';
import { Button } from '@/components/ui/button';
import { Filter, Plus, Library, Layers } from 'lucide-react';
import Link from 'next/link';
import {
  Sheet,
  SheetContent,
  SheetTrigger,
  SheetHeader,
  SheetTitle,
} from '@/components/ui/sheet';

function LibraryContent() {
  const searchParams = useSearchParams();
  const { pageParams, setPage, setSize } = usePageParams({ defaultSize: 12 });

  const query = searchParams.get('query') || undefined;
  const sortParam = searchParams.get('sortType');
  const trustedLevelsParam = searchParams.get('trustedLevels');
  const type = (searchParams.get('type') as 'wordbook' | 'bundle') || 'bundle';

  const trustedLevels = trustedLevelsParam
    ? (trustedLevelsParam.split(',') as TrustedLevel[])
    : undefined;

  const wordbooksQuery = useSharedWordbooks(
    pageParams.page,
    pageParams.size,
    query,
    trustedLevels,
    (sortParam as SharedWordbookSortType) ||
      SharedWordbookSortType.CREATED_AT_DESC,
    type === 'wordbook',
  );

  const bundlesQuery = useSharedBundles(
    pageParams.page,
    pageParams.size,
    query,
    trustedLevels,
    (sortParam as SharedWordbookBundleSortType) ||
      SharedWordbookBundleSortType.CREATED_AT_DESC,
    type === 'bundle',
  );

  const isLoading =
    type === 'wordbook' ? wordbooksQuery.isLoading : bundlesQuery.isLoading;
  const data = type === 'wordbook' ? wordbooksQuery.data : bundlesQuery.data;

  const [isMobileFilterOpen, setIsMobileFilterOpen] = useState(false);

  return (
    <div className="space-y-6">
      <PageHeader
        title="라이브러리"
        description="다른 사람들이 공유한 단어장과 번들을 탐색하세요."
      />

      <div className="flex flex-col md:flex-row gap-6">
        {/* 데스크톱 사이드바 */}
        <aside className="hidden md:block w-52 shrink-0 sticky top-24 self-start h-fit space-y-3">
          <div className="bg-white/5 border border-white/10 rounded-lg p-4">
            <LibraryFilterSidebar type={type} />
          </div>

          <AuthOnly>
            <div className="bg-white/5 border border-white/10 rounded-lg p-4 space-y-2">
              <h3 className="text-xs font-semibold text-zinc-500 uppercase tracking-wider mb-3">
                내 공유
              </h3>
              <Link
                href="/dashboard/shared-wordbooks"
                className="w-full flex items-center gap-2 px-3 py-2 rounded-md text-sm text-gray-300 hover:bg-white/10 hover:text-white transition-colors"
              >
                <Library className="w-3.5 h-3.5 shrink-0" />내 공유 단어장
              </Link>
              <Link
                href="/dashboard/shared-bundles"
                className="w-full flex items-center gap-2 px-3 py-2 rounded-md text-sm text-gray-300 hover:bg-white/10 hover:text-white transition-colors"
              >
                <Layers className="w-3.5 h-3.5 shrink-0" />내 공유 번들
              </Link>
            </div>
          </AuthOnly>
        </aside>

        {/* 메인 컨텐츠 */}
        <main className="flex-1 min-w-0 space-y-4">
          <div className="flex items-center gap-3">
            <div className="flex-1">
              <LibrarySearchBar
                placeholder={
                  type === 'wordbook'
                    ? '단어장 제목으로 검색...'
                    : '번들 제목으로 검색...'
                }
              />
            </div>

            {/* 모바일 필터 버튼 */}
            <div className="md:hidden shrink-0">
              <Sheet
                open={isMobileFilterOpen}
                onOpenChange={setIsMobileFilterOpen}
              >
                <SheetTrigger asChild>
                  <Button
                    variant="outline"
                    size="sm"
                    className="bg-white/5 border-white/10 text-white hover:bg-white/10"
                  >
                    <Filter className="w-4 h-4" />
                  </Button>
                </SheetTrigger>
                <SheetContent
                  side="left"
                  className="w-[280px] overflow-y-auto bg-zinc-950 border-white/10 pl-3"
                >
                  <SheetHeader className="px-1 pb-0">
                    <SheetTitle className="text-white">필터</SheetTitle>
                  </SheetHeader>
                  <div className="px-1 space-y-4">
                    <LibraryFilterSidebar type={type} />
                    <AuthOnly>
                      <div className="border-t border-white/10 pt-4 space-y-1">
                        <h3 className="text-xs font-semibold text-zinc-500 uppercase tracking-wider mb-2 px-2">
                          내 공유
                        </h3>
                        <button
                          onClick={() => {
                            setIsMobileFilterOpen(false);
                          }}
                          className="w-full flex items-center gap-2 px-2 py-2 rounded-md text-sm text-gray-300 hover:bg-white/10 hover:text-white transition-colors text-left"
                        >
                          <Plus className="w-3.5 h-3.5 shrink-0" />
                          단어장 공유하기
                        </button>
                        <Link
                          href="/dashboard/shared-wordbooks"
                          className="w-full flex items-center gap-2 px-2 py-2 rounded-md text-sm text-gray-300 hover:bg-white/10 hover:text-white transition-colors"
                        >
                          <Library className="w-3.5 h-3.5 shrink-0" />내 공유
                          목록
                        </Link>
                      </div>
                    </AuthOnly>
                  </div>
                </SheetContent>
              </Sheet>
            </div>
          </div>

          {/* 로딩 스켈레톤 */}
          {isLoading && (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
              {[...Array(6)].map((_, i) => (
                <div
                  key={i}
                  className={`rounded-xl bg-zinc-900 border border-zinc-800 animate-pulse ${type === 'bundle' ? 'h-56' : 'h-40'}`}
                />
              ))}
            </div>
          )}

          {/* 빈 상태 */}
          {!isLoading && data && data.content.length === 0 && (
            <div className="flex flex-col items-center justify-center py-16 bg-white/5 rounded-lg border border-white/10">
              <p className="text-sm font-medium text-gray-400">
                검색 결과가 없습니다.
              </p>
              <p className="text-xs text-gray-500 mt-1">
                다른 검색어나 필터를 사용해보세요.
              </p>
            </div>
          )}

          {/* 컨텐츠 그리드 */}
          {!isLoading &&
            type === 'wordbook' &&
            wordbooksQuery.data &&
            wordbooksQuery.data.content.length > 0 && (
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                {wordbooksQuery.data.content.map((item) => (
                  <SharedWordbookCard key={item.id} wordbook={item} />
                ))}
              </div>
            )}

          {!isLoading &&
            type === 'bundle' &&
            bundlesQuery.data &&
            bundlesQuery.data.content.length > 0 && (
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                {bundlesQuery.data.content.map((item) => (
                  <SharedWordbookBundleCard key={item.id} bundle={item} />
                ))}
              </div>
            )}

          {/* 페이지네이션 */}
          {!isLoading &&
            type === 'wordbook' &&
            wordbooksQuery.data &&
            wordbooksQuery.data.content.length > 0 && (
              <PaginationBar
                pageData={wordbooksQuery.data}
                onPageChange={setPage}
                onItemsPerPageChange={setSize}
                className="pt-2"
                itemsPerPageOptions={[12, 24, 36]}
              />
            )}

          {!isLoading &&
            type === 'bundle' &&
            bundlesQuery.data &&
            bundlesQuery.data.content.length > 0 && (
              <PaginationBar
                pageData={bundlesQuery.data}
                onPageChange={setPage}
                onItemsPerPageChange={setSize}
                className="pt-2"
                itemsPerPageOptions={[12, 24, 36]}
              />
            )}
        </main>
      </div>
    </div>
  );
}

export default function LibraryPage() {
  return (
    <Suspense
      fallback={
        <div className="text-center py-12 text-gray-400">로딩 중...</div>
      }
    >
      <LibraryContent />
    </Suspense>
  );
}
