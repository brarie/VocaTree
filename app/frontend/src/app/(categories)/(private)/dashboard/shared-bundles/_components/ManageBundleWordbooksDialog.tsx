'use client';

import { useState, useEffect } from 'react';
import {
  useWordbooksInBundle,
  useSharedWordbooks,
  useAddWordbookToBundle,
  useRemoveWordbookFromBundle,
} from '@/hooks/library/queries';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Plus, Minus, Loader2, Search, ChevronLeft, ChevronRight } from 'lucide-react';

const SEARCH_SIZE = 10;

interface ManageBundleWordbooksDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  bundleId: number;
  bundleTitle: string;
}

export function ManageBundleWordbooksDialog({
  open,
  onOpenChange,
  bundleId,
  bundleTitle,
}: ManageBundleWordbooksDialogProps) {
  const [searchTerm, setSearchTerm] = useState('');
  const [searchPage, setSearchPage] = useState(0);

  // 검색어 바뀌면 페이지 초기화
  useEffect(() => {
    setSearchPage(0);
  }, [searchTerm]);

  const { data: bundleData, isLoading: bundleLoading } = useWordbooksInBundle(
    bundleId,
    0,
    100,
  );
  const { data: searchData, isLoading: searchLoading } = useSharedWordbooks(
    searchPage,
    SEARCH_SIZE,
    searchTerm || undefined,
  );

  const addWordbook = useAddWordbookToBundle();
  const removeWordbook = useRemoveWordbookFromBundle();

  const bundleIds = new Set(bundleData?.content.map((wb) => wb.id) ?? []);

  const totalSearchPages = searchData?.totalPages ?? 1;
  const hasNextPage = !(searchData?.last ?? true);
  const hasPrevPage = !(searchData?.first ?? true);

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="bg-zinc-950 border-zinc-800 text-white sm:max-w-lg">
        <DialogHeader>
          <DialogTitle className="text-white">
            단어장 구성 관리
            <span className="ml-2 text-zinc-500 font-normal text-sm">
              {bundleTitle}
            </span>
          </DialogTitle>
        </DialogHeader>

        <div className="space-y-5 max-h-[65vh] overflow-y-auto pr-1">
          {/* 포함된 단어장 */}
          <section>
            <p className="text-xs font-medium text-zinc-400 uppercase tracking-wider mb-2">
              포함된 단어장{' '}
              <span className="text-zinc-600 normal-case font-normal">
                ({bundleData?.content.length ?? 0}개)
              </span>
            </p>

            {bundleLoading ? (
              <div className="flex items-center justify-center py-4">
                <Loader2 className="w-4 h-4 animate-spin text-zinc-500" />
              </div>
            ) : (bundleData?.content.length ?? 0) === 0 ? (
              <p className="text-sm text-zinc-600 py-3 text-center bg-zinc-900 rounded-lg border border-zinc-800">
                포함된 단어장이 없습니다.
              </p>
            ) : (
              <ul className="space-y-1.5">
                {bundleData!.content.map((wb) => (
                  <li
                    key={wb.id}
                    className="flex items-center justify-between gap-3 bg-zinc-900 border border-zinc-800 rounded-lg px-3 py-2"
                  >
                    <div className="flex-1 min-w-0">
                      <p className="text-sm text-white truncate">{wb.title}</p>
                      <p className="text-xs text-zinc-500">
                        {wb.creator.nickname} · v{wb.latestVersion}
                      </p>
                    </div>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() =>
                        removeWordbook.mutate({
                          bundleId,
                          sharedWordbookId: wb.id,
                        })
                      }
                      disabled={removeWordbook.isPending}
                      className="shrink-0 bg-transparent border-zinc-700 text-zinc-400 hover:text-red-400 hover:border-red-400/50 hover:bg-red-400/5 disabled:opacity-30"
                    >
                      <Minus className="w-3.5 h-3.5" />
                    </Button>
                  </li>
                ))}
              </ul>
            )}
          </section>

          {/* 구분선 */}
          <div className="border-t border-zinc-800" />

          {/* 공개 단어장 검색 후 추가 */}
          <section>
            <p className="text-xs font-medium text-zinc-400 uppercase tracking-wider mb-2">
              공개 단어장 검색 후 추가
            </p>

            <div className="relative mb-3">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-zinc-500 pointer-events-none" />
              <Input
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                placeholder="제목 앞 글자로 검색..."
                className="pl-8 bg-zinc-900 border-zinc-700 text-white placeholder:text-zinc-500 text-sm"
              />
            </div>

            {searchLoading ? (
              <div className="flex items-center justify-center py-4">
                <Loader2 className="w-4 h-4 animate-spin text-zinc-500" />
              </div>
            ) : (searchData?.totalElements ?? 0) === 0 ? (
              <p className="text-sm text-zinc-600 py-3 text-center bg-zinc-900 rounded-lg border border-zinc-800">
                {searchTerm
                  ? '검색 결과가 없습니다.'
                  : '제목을 입력하면 공개 단어장을 검색할 수 있습니다.'}
              </p>
            ) : (
              <>
                <ul className="space-y-1.5 mb-3">
                  {searchData!.content.map((wb) => {
                    const alreadyIn = bundleIds.has(wb.id);
                    return (
                      <li
                        key={wb.id}
                        className={`flex items-center justify-between gap-3 rounded-lg border px-3 py-2 ${
                          alreadyIn
                            ? 'bg-zinc-900/50 border-zinc-800/50 opacity-50'
                            : 'bg-zinc-900 border-zinc-800'
                        }`}
                      >
                        <div className="flex-1 min-w-0">
                          <p className="text-sm text-white truncate">
                            {wb.title}
                          </p>
                          <p className="text-xs text-zinc-500">
                            {wb.creator.nickname} · v{wb.latestVersion}
                          </p>
                        </div>
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() =>
                            addWordbook.mutate({
                              bundleId,
                              sharedWordbookId: wb.id,
                            })
                          }
                          disabled={alreadyIn || addWordbook.isPending}
                          title={alreadyIn ? '이미 포함됨' : '추가'}
                          className="shrink-0 bg-transparent border-zinc-700 text-zinc-400 hover:text-emerald-400 hover:border-emerald-400/50 hover:bg-emerald-400/5 disabled:opacity-30"
                        >
                          <Plus className="w-3.5 h-3.5" />
                        </Button>
                      </li>
                    );
                  })}
                </ul>

                {/* 페이지네이션 */}
                <div className="flex items-center justify-between">
                  <span className="text-xs text-zinc-600">
                    전체 {searchData?.totalElements ?? 0}개
                  </span>
                  <div className="flex items-center gap-1.5">
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => setSearchPage((p) => p - 1)}
                      disabled={!hasPrevPage}
                      className="h-7 px-2 bg-transparent border-zinc-700 text-zinc-400 hover:bg-zinc-800 hover:text-white disabled:opacity-30"
                    >
                      <ChevronLeft className="w-3.5 h-3.5" />
                    </Button>
                    <span className="text-xs text-zinc-500 min-w-[3rem] text-center">
                      {searchPage + 1} / {totalSearchPages}
                    </span>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => setSearchPage((p) => p + 1)}
                      disabled={!hasNextPage}
                      className="h-7 px-2 bg-transparent border-zinc-700 text-zinc-400 hover:bg-zinc-800 hover:text-white disabled:opacity-30"
                    >
                      <ChevronRight className="w-3.5 h-3.5" />
                    </Button>
                  </div>
                </div>
              </>
            )}
          </section>
        </div>

        <div className="flex justify-end pt-2 border-t border-zinc-800">
          <Button
            type="button"
            variant="outline"
            onClick={() => onOpenChange(false)}
            className="bg-transparent border-zinc-700 text-zinc-300 hover:bg-zinc-800 hover:text-white"
          >
            닫기
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
}
