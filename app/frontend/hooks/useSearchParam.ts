import { useState, useEffect, useCallback } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { useDebounce } from 'use-debounce';

export interface UseSearchParamOptions {
  paramName?: string; // URL param name (default: 'search')
  debounceMs?: number; // Debounce delay for URL update (default: 300ms)
}

export interface UseSearchParamReturn {
  search: string; // Current search value (immediate)
  debouncedSearch: string; // Debounced search value (for API calls)
  setSearch: (value: string) => void;
}

/**
 * Hook for managing search parameter with URL sync and debounce
 *
 * Usage:
 * ```tsx
 * const { search, debouncedSearch, setSearch } = useSearchParam();
 *
 * // Input shows immediate value
 * <input value={search} onChange={(e) => setSearch(e.target.value)} />
 *
 * // API calls use debounced value
 * useEffect(() => {
 *   fetchData(debouncedSearch);
 * }, [debouncedSearch]);
 * ```
 */
export function useSearchParam(
  options: UseSearchParamOptions = {}
): UseSearchParamReturn {
  const { paramName = 'search', debounceMs = 300 } = options;

  const router = useRouter();
  const searchParams = useSearchParams();

  // Initialize from URL
  const [search, setSearchState] = useState<string>(
    () => searchParams.get(paramName) || ''
  );

  // Debounced value for URL updates and API calls
  const [debouncedSearch] = useDebounce(search, debounceMs);

  // Update URL when debounced search changes
  useEffect(() => {
    const params = new URLSearchParams(searchParams.toString());

    if (debouncedSearch) {
      params.set(paramName, debouncedSearch);
    } else {
      params.delete(paramName);
    }

    const newSearch = params.toString();
    const newUrl = newSearch ? `?${newSearch}` : window.location.pathname;

    router.replace(newUrl, { scroll: false });
  }, [debouncedSearch, paramName, router]);

  const setSearch = useCallback((value: string) => {
    setSearchState(value);
  }, []);

  return {
    search,
    debouncedSearch,
    setSearch,
  };
}
