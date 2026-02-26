'use client';

import { usePathname, useRouter, useSearchParams } from 'next/navigation';
import { useEffect, useState, useCallback } from 'react';
import { useDebounce } from 'use-debounce';
import { Search } from 'lucide-react';
import { Input } from '@/components/ui/input';

interface LibrarySearchBarProps {
  className?: string;
  placeholder?: string;
}

export function LibrarySearchBar({
  className,
  placeholder = '검색어를 입력하세요...',
}: LibrarySearchBarProps) {
  const router = useRouter();
  const pathname = usePathname();
  const searchParams = useSearchParams();

  const initialQuery = searchParams.get('query') || '';
  const [searchQuery, setSearchQuery] = useState(initialQuery);
  const [debouncedQuery] = useDebounce(searchQuery, 500);

  // URL 업데이트 로직 (검색어 변경 시)
  const createQueryString = useCallback(
    (name: string, value: string) => {
      const params = new URLSearchParams(searchParams.toString());
      if (value) {
        params.set(name, value);
      } else {
        params.delete(name);
      }
      // 검색 시 페이지를 0으로 리셋하는 것이 UX상 좋음 (선택사항)
      params.delete('page');
      return params.toString();
    },
    [searchParams],
  );

  useEffect(() => {
    // debouncedQuery가 변경되면 URL 업데이트
    // 초기 로딩 시 불필요한 push 방지: 현재 params와 다를 때만
    const currentQuery = searchParams.get('query') || '';
    if (debouncedQuery !== currentQuery) {
      router.push(pathname + '?' + createQueryString('query', debouncedQuery));
    }
  }, [debouncedQuery, createQueryString, pathname, router, searchParams]);

  // URL이 외부에서 변경되었을 때 (뒤로가기 등) state 동기화
  useEffect(() => {
    const currentQuery = searchParams.get('query') || '';
    if (currentQuery !== searchQuery) {
      setSearchQuery(currentQuery);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [searchParams]);

  return (
    <div className={`relative ${className}`}>
      <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
      <Input
        placeholder={placeholder}
        value={searchQuery}
        onChange={(e) => setSearchQuery(e.target.value)}
        className="pl-9 bg-background/50 border-input/50 focus:bg-background transition-colors"
      />
    </div>
  );
}
