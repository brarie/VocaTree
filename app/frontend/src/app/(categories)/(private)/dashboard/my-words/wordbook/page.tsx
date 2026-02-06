'use client';

import { Suspense } from 'react';
import { useState } from 'react';
import { useSearchParams, useRouter } from 'next/navigation';
import { Plus, Star } from 'lucide-react';
import { usePaginateNodes } from '@/hooks/wordbook/queries';
import { WordbookNodeSortType } from '@/types/api/wordbook';
import { Button } from '@/components/ui/button';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { PaginationBar } from '@/components/PaginationBar';
import { cn } from '@/lib/utils';
import { WordbookCard } from './_components/WordbookCard';
import { CreateDialog } from './_components/CreateDialog';

const SORT_TYPE_LABELS: Record<WordbookNodeSortType, string> = {
  [WordbookNodeSortType.TITLE_ASC]: '제목 오름차순',
  [WordbookNodeSortType.TITLE_DESC]: '제목 내림차순',
  [WordbookNodeSortType.CREATED_AT_ASC]: '생성일 오름차순',
  [WordbookNodeSortType.CREATED_AT_DESC]: '생성일 내림차순',
};

export function WordbookContent() {
  const router = useRouter();
  const searchParams = useSearchParams();

  const page = parseInt(searchParams.get('page') || '0', 10);
  const size = parseInt(searchParams.get('size') || '30', 10);
  const sortType =
    (searchParams.get('sortType') as WordbookNodeSortType) ||
    WordbookNodeSortType.CREATED_AT_DESC;
  const starOnly = searchParams.get('starOnly') === 'true';

  const { data: pageData, isLoading } = usePaginateNodes(
    page,
    size,
    sortType,
    starOnly,
  );

  const [createDialogOpen, setCreateDialogOpen] = useState(false);

  const updateParams = (updates: Record<string, string | number | boolean>) => {
    const params = new URLSearchParams(searchParams.toString());

    Object.entries(updates).forEach(([key, value]) => {
      if (
        value === '' ||
        value === null ||
        value === undefined ||
        (key === 'page' && value === 0) ||
        (key === 'size' && value === 30) ||
        (key === 'sortType' &&
          value === WordbookNodeSortType.CREATED_AT_DESC) ||
        (key === 'starOnly' && value === false)
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

  const handleSortChange = (newSort: string) => {
    updateParams({ page: 0, sortType: newSort });
  };

  const handleStarOnlyChange = (checked: boolean) => {
    updateParams({ page: 0, starOnly: checked });
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div className="flex flex-wrap gap-4 items-center">
          <Select value={sortType} onValueChange={handleSortChange}>
            <SelectTrigger className="w-[180px]">
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              {Object.entries(SORT_TYPE_LABELS).map(([value, label]) => (
                <SelectItem key={value} value={value}>
                  {label}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>

          <Button
            variant="outline"
            onClick={() => handleStarOnlyChange(!starOnly)}
            className="gap-2"
          >
            <Star
              className={cn(
                'w-4 h-4 transition-all',
                starOnly
                  ? 'fill-yellow-400 text-yellow-400'
                  : 'text-muted-foreground',
              )}
            />
            즐겨찾기만 보기
          </Button>
        </div>

        <div className="flex gap-2">
          <Button onClick={() => setCreateDialogOpen(true)}>
            <Plus className="w-4 h-4 mr-2" />
            단어장 만들기
          </Button>
        </div>
      </div>

      {isLoading ? (
        <div className="text-center py-12 text-muted-foreground">
          로딩 중...
        </div>
      ) : pageData && pageData.content.length > 0 ? (
        <>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {pageData.content.map((node) => (
              <WordbookCard key={node.id} node={node} />
            ))}
          </div>

          <PaginationBar
            pageData={pageData}
            onPageChange={handlePageChange}
            onItemsPerPageChange={handleSizeChange}
            itemsPerPageOptions={[15, 30, 45]}
          />
        </>
      ) : (
        <div className="text-center py-12 text-muted-foreground">
          {starOnly
            ? '즐겨찾기한 단어장이 없습니다.'
            : '단어장이 없습니다. 새로운 단어장을 만들어보세요!'}
        </div>
      )}

      <CreateDialog
        open={createDialogOpen}
        onOpenChange={setCreateDialogOpen}
      />
    </div>
  );
}

export default function WordbookPage() {
  return (
    <Suspense fallback={<div>로딩 중...</div>}>
      <WordbookContent />
    </Suspense>
  );
}
