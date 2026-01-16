import { useState, useEffect, useCallback } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';

export interface PageParams {
  page: number; // 0-based for backend
  size: number;
  sort?: string; // e.g., "name,asc"
}

export interface UsePageParamsOptions {
  defaultPage?: number;
  defaultSize?: number;
  defaultSort?: string;
}

export interface UsePageParamsReturn {
  pageParams: PageParams;
  setPage: (page: number) => void;
  setSize: (size: number) => void;
  setSort: (sort: string) => void;
}

/**
 * Hook for managing PageParams with URL sync
 * Only manages: page, size, sort
 */
export function usePageParams(
  options: UsePageParamsOptions = {}
): UsePageParamsReturn {
  const { defaultPage = 0, defaultSize = 10, defaultSort = '' } = options;

  const router = useRouter();
  const searchParams = useSearchParams();

  const [pageParams, setPageParams] = useState<PageParams>(() => ({
    page: parseInt(searchParams.get('page') || String(defaultPage)),
    size: parseInt(searchParams.get('size') || String(defaultSize)),
    sort: searchParams.get('sort') || defaultSort,
  }));

  // Update URL when pageParams change
  useEffect(() => {
    const params = new URLSearchParams(searchParams.toString());

    // Update page
    if (pageParams.page !== defaultPage) {
      params.set('page', pageParams.page.toString());
    } else {
      params.delete('page');
    }

    // Update size
    if (pageParams.size !== defaultSize) {
      params.set('size', pageParams.size.toString());
    } else {
      params.delete('size');
    }

    // Update sort
    if (pageParams.sort && pageParams.sort !== defaultSort) {
      params.set('sort', pageParams.sort);
    } else {
      params.delete('sort');
    }

    const newSearch = params.toString();
    const newUrl = newSearch ? `?${newSearch}` : window.location.pathname;

    router.replace(newUrl, { scroll: false });
  }, [pageParams, defaultPage, defaultSize, defaultSort, router]);

  const setPage = useCallback((page: number) => {
    setPageParams((prev) => ({ ...prev, page }));
  }, []);

  const setSize = useCallback((size: number) => {
    setPageParams((prev) => ({ ...prev, size, page: 0 })); // Reset to first page
  }, []);

  const setSort = useCallback((sort: string) => {
    setPageParams((prev) => ({ ...prev, sort, page: 0 })); // Reset to first page
  }, []);

  return {
    pageParams,
    setPage,
    setSize,
    setSort,
  };
}
