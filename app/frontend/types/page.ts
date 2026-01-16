/**
 * Spring Boot Page response type
 */
export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number; // 0-based page number
  first: boolean;
  last: boolean;
}

/**
 * Sort direction
 */
export type SortDirection = 'asc' | 'desc';

/**
 * Sort configuration
 */
export interface Sort {
  property: string;
  direction: SortDirection;
}

/**
 * Spring Boot Pageable parameters
 */
export interface PageParams {
  page: number; // 0-based for backend
  size: number;
  sort?: Sort[];
}

/**
 * Convert PageParams to URL query parameters (Spring Boot format)
 * @example
 * // Single sort
 * pageParamsToQueryString({ page: 0, size: 10, sort: [{ property: 'name', direction: 'asc' }] })
 * // Returns: "page=0&size=10&sort=name,asc"
 *
 * // Multiple sorts
 * pageParamsToQueryString({ page: 1, size: 20, sort: [
 *   { property: 'isPinned', direction: 'desc' },
 *   { property: 'createdAt', direction: 'desc' }
 * ]})
 * // Returns: "page=1&size=20&sort=isPinned,desc&sort=createdAt,desc"
 */
export function pageParamsToQueryString(params: PageParams): string {
  const searchParams = new URLSearchParams();
  searchParams.set('page', params.page.toString());
  searchParams.set('size', params.size.toString());

  if (params.sort && params.sort.length > 0) {
    params.sort.forEach((sort) => {
      searchParams.append('sort', `${sort.property},${sort.direction}`);
    });
  }

  return searchParams.toString();
}

/**
 * Parse URL query parameters to PageParams (Spring Boot format)
 */
export function queryStringToPageParams(
  searchParams: URLSearchParams
): PageParams {
  const page = parseInt(searchParams.get('page') || '0');
  const size = parseInt(searchParams.get('size') || '10');
  const sortParams = searchParams.getAll('sort');

  const sort: Sort[] = sortParams
    .map((s) => {
      const [property, direction] = s.split(',');
      if (property && (direction === 'asc' || direction === 'desc')) {
        return { property, direction };
      }
      return null;
    })
    .filter((s): s is Sort => s !== null);

  return {
    page,
    size,
    sort: sort.length > 0 ? sort : undefined,
  };
}

/**
 * Create a mock Page response for development
 */
export function createMockPage<T>(
  content: T[],
  params: PageParams
): Page<T> {
  const totalElements = content.length;
  const totalPages = Math.ceil(totalElements / params.size);
  const number = params.page;

  return {
    content,
    totalElements,
    totalPages,
    size: params.size,
    number,
    first: number === 0,
    last: number === totalPages - 1,
  };
}

/**
 * Create a Page<T> object from paginated data
 * Useful for client-side pagination simulation
 */
export function createPage<T>(
  content: T[],
  page: number,
  size: number,
  totalElements: number
): Page<T> {
  const totalPages = Math.ceil(totalElements / size);

  return {
    content,
    totalElements,
    totalPages,
    size,
    number: page,
    first: page === 0,
    last: page >= totalPages - 1 || totalPages === 0,
  };
}
