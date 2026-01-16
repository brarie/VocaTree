/**
 * API Integration Example
 *
 * This file shows how to integrate the pagination system with Spring Boot backend
 */

import { Page } from '@/types/page';
import { Wordbook } from '@/types/wordbook';

/**
 * Example: Fetch wordbooks from backend
 *
 * Usage in component:
 * ```tsx
 * const pagination = usePagination({ basePath: '/dashboard/my-words' });
 *
 * useEffect(() => {
 *   fetchWordbooks(pagination.params).then(data => {
 *     pagination.setPageData(data); // Store backend Page<T> response
 *   });
 * }, [pagination.params]); // Re-fetch when URL params change
 *
 * // Use backend data
 * const wordbooks = pagination.pageData?.content || [];
 * ```
 */
export async function fetchWordbooks(params: {
  page: number; // 0-based
  size: number;
  sort: string; // e.g., "name,asc"
  search?: string;
}): Promise<Page<Wordbook>> {
  // Build query string in Spring Boot format
  const queryParams = new URLSearchParams({
    page: params.page.toString(),
    size: params.size.toString(),
  });

  if (params.sort) {
    queryParams.append('sort', params.sort);
  }

  if (params.search) {
    queryParams.append('search', params.search);
  }

  // Example API call
  const response = await fetch(
    `/api/wordbooks?${queryParams.toString()}`,
    {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    }
  );

  if (!response.ok) {
    throw new Error('Failed to fetch wordbooks');
  }

  // Spring Boot returns Page<T> format:
  // {
  //   content: [...],
  //   totalElements: 100,
  //   totalPages: 10,
  //   size: 10,
  //   number: 0,
  //   first: true,
  //   last: false
  // }
  return response.json();
}

/**
 * Alternative: Using params object directly
 */
export async function fetchWordbooksAlt(
  page: number,
  size: number,
  sort?: string,
  search?: string
): Promise<Page<Wordbook>> {
  return fetchWordbooks({ page, size, sort: sort || '', search });
}

/**
 * Example: Wordbooks page with backend integration
 *
 * ```tsx
 * export default function WordbooksPage() {
 *   const pagination = usePagination({
 *     defaultPage: 0,
 *     defaultSize: 10,
 *     basePath: '/dashboard/my-words',
 *   });
 *
 *   const [loading, setLoading] = useState(false);
 *
 *   // Fetch data when params change
 *   useEffect(() => {
 *     setLoading(true);
 *     fetchWordbooks(pagination.params)
 *       .then(data => {
 *         pagination.setPageData(data);
 *       })
 *       .catch(error => {
 *         console.error('Failed to fetch:', error);
 *       })
 *       .finally(() => {
 *         setLoading(false);
 *       });
 *   }, [
 *     pagination.params.page,
 *     pagination.params.size,
 *     pagination.params.sort,
 *     pagination.params.search,
 *   ]);
 *
 *   // Use backend data
 *   const wordbooks = pagination.pageData?.content || [];
 *   const totalPages = pagination.pageData?.totalPages || 0;
 *
 *   return (
 *     <div>
 *       {loading && <div>Loading...</div>}
 *
 *       {wordbooks.map(wb => (
 *         <WordbookCard key={wb.id} wordbook={wb} />
 *       ))}
 *
 *       <PaginationBar
 *         currentPage={pagination.params.page + 1}
 *         totalPages={totalPages}
 *         itemsPerPage={pagination.params.size}
 *         totalItems={pagination.pageData?.totalElements || 0}
 *         onPageChange={pagination.setPage}
 *         onItemsPerPageChange={pagination.setSize}
 *       />
 *     </div>
 *   );
 * }
 * ```
 */
