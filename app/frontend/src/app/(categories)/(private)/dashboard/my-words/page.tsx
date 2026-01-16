'use client';

import { useState, useMemo, useEffect } from 'react';
import { WordbookCard } from './_components/WordbookCard';
import { mockWordbooks } from '@/lib/mock-data';
import { Wordbook } from '@/types/wordbook';
import { Page, createPage } from '@/types/page';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog';
import { Label } from '@/components/ui/label';
import { Plus, Search } from 'lucide-react';
import { usePageParams } from '@/hooks/usePageParams';
import { useSearchParam } from '@/hooks/useSearchParam';
import { PaginationBar } from '@/components/PaginationBar';

export default function WordbooksPage() {
  // Wordbooks state
  const [wordbooks, setWordbooks] = useState<Wordbook[]>(mockWordbooks);
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
  const [newWordbookName, setNewWordbookName] = useState('');

  // 1. PageParams (page, size, sort) - URL synced
  const { pageParams, setPage, setSize, setSort } = usePageParams({
    defaultPage: 0,
    defaultSize: 10,
    defaultSort: 'createdAt,desc',
  });

  // 2. Search (separate from pagination) - URL synced with debounce
  const { search, debouncedSearch, setSearch } = useSearchParam({
    debounceMs: 300,
  });

  // Sort condition (from URL)
  const [sortBy, setSortBy] = useState(pageParams.sort || 'createdAt,desc');

  // Page data from backend (or mock)
  const [pageData, setPageData] = useState<Page<Wordbook> | null>(null);

  // 2. Filter and sort wordbooks (client-side for now, will be backend later)
  const filteredAndSortedWordbooks = useMemo(() => {
    let result = [...wordbooks];

    // Search filter
    if (debouncedSearch) {
      result = result.filter((wb) =>
        wb.name.toLowerCase().includes(debouncedSearch.toLowerCase())
      );
    }

    // Sort
    const [property, direction] = sortBy.split(',');
    result.sort((a, b) => {
      const isAsc = direction === 'asc';
      let comparison = 0;

      switch (property) {
        case 'name':
          comparison = a.name.localeCompare(b.name);
          break;
        case 'createdAt':
          comparison =
            new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime();
          break;
        case 'updatedAt':
          comparison =
            new Date(a.updatedAt).getTime() - new Date(b.updatedAt).getTime();
          break;
      }

      return isAsc ? comparison : -comparison;
    });

    // Pinned items always on top
    return result.sort((a, b) => (b.isPinned ? 1 : 0) - (a.isPinned ? 1 : 0));
  }, [wordbooks, debouncedSearch, sortBy]);

  // 3. Fetch data when PageParams or SearchConditions change
  useEffect(() => {
    // In real app: fetchWordbooks({ ...pageParams, search: debouncedSearch })
    // For now: client-side pagination with createPage helper
    const startIndex = pageParams.page * pageParams.size;
    const endIndex = startIndex + pageParams.size;
    const paginatedItems = filteredAndSortedWordbooks.slice(
      startIndex,
      endIndex
    );

    const page = createPage(
      paginatedItems,
      pageParams.page,
      pageParams.size,
      filteredAndSortedWordbooks.length
    );

    setPageData(page);
  }, [
    pageParams.page,
    pageParams.size,
    pageParams.sort,
    debouncedSearch,
    filteredAndSortedWordbooks,
  ]);

  // Toggle pin
  const handleTogglePin = (id: string) => {
    setWordbooks((prev) =>
      prev.map((wb) => (wb.id === id ? { ...wb, isPinned: !wb.isPinned } : wb))
    );
  };

  // Create wordbook
  const handleCreateWordbook = () => {
    if (!newWordbookName.trim()) return;

    const now = new Date().toISOString();
    const newWordbook: Wordbook = {
      id: Date.now().toString(),
      name: newWordbookName,
      wordCount: 0,
      isPinned: false,
      createdAt: now,
      updatedAt: now,
    };

    setWordbooks((prev) => [newWordbook, ...prev]);
    setNewWordbookName('');
    setIsCreateDialogOpen(false);
  };

  // Handle sort change
  const handleSortChange = (value: string) => {
    setSortBy(value);
    setSort(value);
  };

  return (
    <div className="space-y-4">
      {/* Search & Actions */}
      <div className="flex flex-col md:flex-row gap-4 items-start md:items-center justify-between">
        {/* Search */}
        <div className="relative flex-1 max-w-md">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
          <Input
            type="text"
            placeholder="단어장 이름으로 검색..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="pl-10 bg-white/5 border-white/10 text-white placeholder:text-gray-500"
          />
        </div>

        <div className="flex gap-2">
          {/* Sort */}
          <Select value={sortBy} onValueChange={handleSortChange}>
            <SelectTrigger className="w-40 bg-white/5 border-white/10 text-white">
              <SelectValue />
            </SelectTrigger>
            <SelectContent className="bg-black border-white/10">
              <SelectItem value="createdAt,desc">생성 최신순</SelectItem>
              <SelectItem value="createdAt,asc">생성 오래된순</SelectItem>
              <SelectItem value="updatedAt,desc">수정 최신순</SelectItem>
              <SelectItem value="updatedAt,asc">수정 오래된순</SelectItem>
              <SelectItem value="name,asc">이름 (가-하)</SelectItem>
              <SelectItem value="name,desc">이름 (하-가)</SelectItem>
            </SelectContent>
          </Select>

          {/* Create Button */}
          <Dialog
            open={isCreateDialogOpen}
            onOpenChange={setIsCreateDialogOpen}
          >
            <DialogTrigger asChild>
              <Button className="bg-white text-black hover:bg-gray-200">
                <Plus className="w-4 h-4 mr-2" />
                단어장 만들기
              </Button>
            </DialogTrigger>
            <DialogContent className="bg-black border-white/10">
              <DialogHeader>
                <DialogTitle className="text-white">
                  새 단어장 만들기
                </DialogTitle>
                <DialogDescription className="text-gray-400">
                  단어장 이름을 입력하세요.
                </DialogDescription>
              </DialogHeader>
              <div className="space-y-4 py-4">
                <div className="space-y-2">
                  <Label htmlFor="name" className="text-white">
                    단어장 이름
                  </Label>
                  <Input
                    id="name"
                    value={newWordbookName}
                    onChange={(e) => setNewWordbookName(e.target.value)}
                    onKeyDown={(e) => {
                      if (e.key === 'Enter') handleCreateWordbook();
                    }}
                    placeholder="예: 토플 필수 단어"
                    className="bg-white/5 border-white/10 text-white placeholder:text-gray-500"
                  />
                </div>
              </div>
              <DialogFooter>
                <Button
                  variant="outline"
                  onClick={() => setIsCreateDialogOpen(false)}
                  className="bg-white/5 border-white/10 text-white hover:bg-white/10"
                >
                  취소
                </Button>
                <Button
                  onClick={handleCreateWordbook}
                  className="bg-white text-black hover:bg-gray-200"
                >
                  만들기
                </Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>
        </div>
      </div>

      {/* Wordbook List */}
      <div className="flex flex-col gap-3">
        {pageData && pageData.content.length > 0 ? (
          pageData.content.map((wordbook) => (
            <WordbookCard
              key={wordbook.id}
              wordbook={wordbook}
              onTogglePin={handleTogglePin}
            />
          ))
        ) : (
          <div className="text-center py-12 text-gray-500">
            {search
              ? '검색 결과가 없습니다.'
              : '단어장이 없습니다. 새로운 단어장을 만들어보세요!'}
          </div>
        )}
      </div>

      {/* Pagination */}
      {pageData && pageData.totalElements > 0 && (
        <PaginationBar
          pageData={pageData}
          onPageChange={setPage}
          onItemsPerPageChange={setSize}
          className="pt-2"
        />
      )}
    </div>
  );
}
