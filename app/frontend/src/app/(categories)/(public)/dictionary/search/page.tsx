'use client';

import { Suspense, useEffect } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { useQueryClient } from '@tanstack/react-query';
import { ArrowLeft } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { PageHeader } from '@/components/PageHeader';
import { SimpleWordList } from '../_components/WordList';
import { useSearchWords, dictionaryKeys } from '@/hooks/dictionary/queries';
import { PaginationBar } from '@/components/PaginationBar';
import { useAuth } from '@/contexts/AuthContext';
import { usePageParams } from '@/hooks/usePageParams';
import { DictionarySearchBar } from '../_components/DictionarySearchBar';
import Link from 'next/link';

function SearchContent() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const queryClient = useQueryClient();
  const { isAuthenticated } = useAuth();
  const queryParam = searchParams.get('query') || '';

  const { pageParams, setPage, setSize } = usePageParams({ defaultSize: 10 });

  const { data: searchResults, isLoading: searchLoading } = useSearchWords(
    queryParam,
    pageParams.page,
    pageParams.size,
    isAuthenticated,
  );

  useEffect(() => {
    if (queryParam && isAuthenticated) {
      queryClient.invalidateQueries({
        queryKey: dictionaryKeys.searchHistory(),
      });
    }
  }, [queryParam, isAuthenticated, queryClient]);

  const handleSearch = (query: string) => {
    router.push(
      `/dictionary/search?query=${encodeURIComponent(query)}&page=0&size=${pageParams.size}`,
    );
  };

  return (
    <div className="space-y-6">
      <PageHeader
        title="단어 검색"
        description={
          queryParam
            ? `"${queryParam}" 검색 결과`
            : '단어를 입력하여 검색하세요'
        }
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

      <DictionarySearchBar
        initialValue={queryParam}
        showHistory={false}
        onSearch={handleSearch}
      />

      {searchLoading && (
        <div className="text-center py-12">
          <div className="inline-block h-8 w-8 animate-spin rounded-full border-4 border-solid border-current border-r-transparent"></div>
          <p className="mt-4 text-gray-400">검색 중...</p>
        </div>
      )}

      {!searchLoading &&
        searchResults &&
        searchResults.content.length === 0 && (
          <div className="text-center py-12 bg-zinc-900 rounded-lg border border-zinc-800">
            <p className="text-gray-400">검색 결과가 없습니다.</p>
          </div>
        )}

      {!searchLoading && searchResults && searchResults.content.length > 0 && (
        <>
          <SimpleWordList words={searchResults.content} />
          <PaginationBar
            pageData={searchResults}
            onPageChange={setPage}
            onItemsPerPageChange={setSize}
          />
        </>
      )}
    </div>
  );
}

export default function DictionarySearchPage() {
  return (
    <Suspense
      fallback={
        <div className="text-center py-12 text-gray-400">로딩 중...</div>
      }
    >
      <SearchContent />
    </Suspense>
  );
}
