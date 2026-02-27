'use client';

import { useState } from 'react';
import {
  useAdminSharedWordbooks,
  useBanSharedWordbook,
  useUnbanSharedWordbook,
  useUpdateTrustedLevel,
} from '@/hooks/library/queries';
import {
  SharedWordbookListView,
  TrustedLevel,
  SharedState,
} from '@/types/api/library';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { PaginationBar } from '@/components/PaginationBar';
import { Search, Ban, ShieldOff, BookOpen, ExternalLink } from 'lucide-react';
import Link from 'next/link';
import { cn } from '@/lib/utils';

const TRUSTED_LEVEL_LABEL: Record<TrustedLevel, string> = {
  OFFICIAL: '공식',
  VERIFIED: '검증',
  BASIC: '기본',
};

const TRUSTED_LEVEL_COLOR: Record<TrustedLevel, string> = {
  OFFICIAL: 'text-indigo-400 bg-indigo-400/10 border-indigo-400/20',
  VERIFIED: 'text-emerald-400 bg-emerald-400/10 border-emerald-400/20',
  BASIC: 'text-zinc-400 bg-zinc-400/10 border-zinc-400/20',
};

const STATE_LABEL: Record<SharedState, string> = {
  PUBLIC: '공개',
  DELETED_BY_OWNER: '삭제됨',
  BANNED: '차단됨',
};

const STATE_COLOR: Record<SharedState, string> = {
  PUBLIC: 'text-emerald-400 bg-emerald-400/10 border-emerald-400/20',
  DELETED_BY_OWNER: 'text-zinc-500 bg-zinc-500/10 border-zinc-500/20',
  BANNED: 'text-red-400 bg-red-400/10 border-red-400/20',
};

export function SharedWordbookAdminSection() {
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [inputValue, setInputValue] = useState('');
  const [titlePrefix, setTitlePrefix] = useState('');

  const { data, isLoading } = useAdminSharedWordbooks(
    page,
    size,
    titlePrefix || undefined,
  );
  const banWordbook = useBanSharedWordbook();
  const unbanWordbook = useUnbanSharedWordbook();
  const updateLevel = useUpdateTrustedLevel();

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setPage(0);
    setTitlePrefix(inputValue);
  };

  function handleBan(item: SharedWordbookListView) {
    if (!confirm(`"${item.title}" 단어장을 차단할까요?`)) return;
    banWordbook.mutate(item.id);
  }

  return (
    <section className="space-y-4">
      <div className="flex items-center justify-between flex-wrap gap-3">
        <div className="flex items-center gap-2">
          <BookOpen className="w-4 h-4 text-zinc-400" />
          <h2 className="text-sm font-semibold text-zinc-200">공유 단어장</h2>
          {data && (
            <span className="text-xs text-zinc-500">
              ({data.totalElements}개)
            </span>
          )}
        </div>
        <form onSubmit={handleSearch} className="flex gap-2">
          <div className="relative">
            <Search className="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-zinc-500 pointer-events-none" />
            <Input
              value={inputValue}
              onChange={(e) => setInputValue(e.target.value)}
              placeholder="제목 검색..."
              className="pl-8 h-8 bg-zinc-900 border-zinc-700 text-white placeholder:text-zinc-500 text-sm w-48"
            />
          </div>
          <Button
            type="submit"
            size="sm"
            className="h-8 bg-white text-zinc-900 hover:bg-zinc-200"
          >
            검색
          </Button>
        </form>
      </div>

      {isLoading && (
        <div className="space-y-2">
          {[...Array(3)].map((_, i) => (
            <div
              key={i}
              className="h-14 bg-zinc-900 rounded-lg border border-zinc-800 animate-pulse"
            />
          ))}
        </div>
      )}

      {!isLoading && data && data.content.length === 0 && (
        <div className="flex items-center justify-center py-10 bg-zinc-900 rounded-lg border border-zinc-800">
          <p className="text-sm text-zinc-500">
            {titlePrefix ? '검색 결과가 없습니다.' : '공유된 단어장이 없습니다.'}
          </p>
        </div>
      )}

      {!isLoading && data && data.content.length > 0 && (
        <div className="space-y-3">
          <div className="border border-zinc-800 rounded-lg divide-y divide-zinc-800">
            {data.content.map((item) => (
              <div
                key={item.id}
                className="flex items-center gap-4 px-4 py-3 bg-zinc-900 first:rounded-t-lg last:rounded-b-lg"
              >
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-2 flex-wrap">
                    <p className="text-sm font-medium text-white truncate">
                      {item.title}
                    </p>
                    <span
                      className={cn(
                        'text-[10px] px-1.5 py-0.5 rounded border font-medium',
                        STATE_COLOR[item.sharedState],
                      )}
                    >
                      {STATE_LABEL[item.sharedState]}
                    </span>
                    <span
                      className={cn(
                        'text-[10px] px-1.5 py-0.5 rounded border font-medium',
                        TRUSTED_LEVEL_COLOR[item.trustedLevel],
                      )}
                    >
                      {TRUSTED_LEVEL_LABEL[item.trustedLevel]}
                    </span>
                  </div>
                  <p className="text-xs text-zinc-500 mt-0.5">
                    {item.creator.nickname} · v{item.latestVersion} ·{' '}
                    {item.createdAt.slice(0, 10).replace(/-/g, '.')}
                  </p>
                </div>

                <div className="flex items-center gap-2 shrink-0">
                  {item.sharedState === SharedState.PUBLIC && (
                    <Link
                      href={`/library/wordbook/${item.slug}`}
                      target="_blank"
                    >
                      <Button
                        variant="outline"
                        size="sm"
                        className="h-7 px-2 bg-transparent border-zinc-700 text-zinc-400 hover:text-white hover:bg-zinc-800"
                      >
                        <ExternalLink className="w-3.5 h-3.5" />
                      </Button>
                    </Link>
                  )}

                  <Select
                    value={item.trustedLevel}
                    onValueChange={(value) =>
                      updateLevel.mutate({
                        id: item.id,
                        data: { trustedLevel: value as TrustedLevel },
                      })
                    }
                    disabled={item.sharedState === SharedState.BANNED}
                  >
                    <SelectTrigger className="h-7 w-24 bg-zinc-800 border-zinc-700 text-zinc-300 text-xs">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent className="bg-zinc-900 border-zinc-700">
                      <SelectItem
                        value={TrustedLevel.OFFICIAL}
                        className="text-xs text-zinc-300"
                      >
                        공식
                      </SelectItem>
                      <SelectItem
                        value={TrustedLevel.VERIFIED}
                        className="text-xs text-zinc-300"
                      >
                        검증
                      </SelectItem>
                      <SelectItem
                        value={TrustedLevel.BASIC}
                        className="text-xs text-zinc-300"
                      >
                        기본
                      </SelectItem>
                    </SelectContent>
                  </Select>

                  {item.sharedState === SharedState.BANNED ? (
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => unbanWordbook.mutate(item.id)}
                      disabled={unbanWordbook.isPending}
                      title="차단 해제"
                      className="h-7 px-2 bg-transparent border-zinc-700 text-zinc-400 hover:text-emerald-400 hover:border-emerald-400/50 hover:bg-emerald-400/5 disabled:opacity-30"
                    >
                      <ShieldOff className="w-3.5 h-3.5" />
                    </Button>
                  ) : (
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => handleBan(item)}
                      disabled={banWordbook.isPending}
                      title="차단"
                      className="h-7 px-2 bg-transparent border-zinc-700 text-zinc-400 hover:text-red-400 hover:border-red-400/50 hover:bg-red-400/5 disabled:opacity-30"
                    >
                      <Ban className="w-3.5 h-3.5" />
                    </Button>
                  )}
                </div>
              </div>
            ))}
          </div>

          <PaginationBar
            pageData={data}
            onPageChange={setPage}
            onItemsPerPageChange={(newSize) => {
              setSize(newSize);
              setPage(0);
            }}
            className="pt-1"
          />
        </div>
      )}
    </section>
  );
}
