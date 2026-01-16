'use client';

import { Button } from '@/components/ui/button';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import { Page } from '@/types/page';

export interface PaginationBarProps<T = any> {
  pageData: Page<T>;
  itemsPerPageOptions?: number[];
  onPageChange: (page: number) => void;
  onItemsPerPageChange: (size: number) => void;
  className?: string;
}

/**
 * Pagination bar that works with Spring Boot Page<T> response
 */
export function PaginationBar<T>({
  pageData,
  itemsPerPageOptions = [10, 20, 50],
  onPageChange,
  onItemsPerPageChange,
  className,
}: PaginationBarProps<T>) {
  const { number, size, totalPages, totalElements, first, last } = pageData;

  // Generate page numbers with ellipsis
  const getPageNumbers = (): (number | string)[] => {
    const pages: (number | string)[] = [];
    const showPages = 5;
    const currentPage = number; // 0-based from backend

    if (totalPages <= showPages) {
      return Array.from({ length: totalPages }, (_, i) => i);
    }

    let startPage = Math.max(0, currentPage - 2);
    let endPage = Math.min(totalPages - 1, currentPage + 2);

    if (currentPage <= 2) {
      endPage = Math.min(showPages - 1, totalPages - 1);
    } else if (currentPage >= totalPages - 3) {
      startPage = Math.max(0, totalPages - showPages);
    }

    if (startPage > 0) {
      pages.push(0);
      if (startPage > 1) pages.push('...');
    }

    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }

    if (endPage < totalPages - 1) {
      if (endPage < totalPages - 2) pages.push('...');
      pages.push(totalPages - 1);
    }

    return pages;
  };

  if (totalElements === 0) return null;

  return (
    <div
      className={`flex flex-col sm:flex-row items-center justify-between gap-4 ${className || ''}`}
    >
      {/* Items per page selector */}
      <div className="flex items-center gap-2">
        <span className="text-sm text-gray-400">페이지당</span>
        <Select
          value={size.toString()}
          onValueChange={(value) => onItemsPerPageChange(parseInt(value))}
        >
          <SelectTrigger className="w-20 bg-white/5 border-white/10 text-white">
            <SelectValue />
          </SelectTrigger>
          <SelectContent className="bg-black border-white/10">
            {itemsPerPageOptions.map((option) => (
              <SelectItem key={option} value={option.toString()}>
                {option}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
        <span className="text-sm text-gray-400">개씩 보기</span>
      </div>

      {/* Page navigation */}
      <div className="flex items-center gap-1">
        {/* Previous button */}
        <Button
          variant="outline"
          size="sm"
          onClick={() => onPageChange(number - 1)}
          disabled={first}
          className="bg-white/5 border-white/10 text-white hover:bg-white/10 disabled:opacity-50"
        >
          <ChevronLeft className="w-4 h-4" />
        </Button>

        {/* Page numbers */}
        {getPageNumbers().map((page, index) => (
          <Button
            key={index}
            variant={page === number ? 'default' : 'outline'}
            size="sm"
            onClick={() => typeof page === 'number' && onPageChange(page)}
            disabled={typeof page === 'string'}
            className={
              page === number
                ? 'bg-white text-black hover:bg-gray-200'
                : 'bg-white/5 border-white/10 text-white hover:bg-white/10'
            }
          >
            {typeof page === 'number' ? page + 1 : page} {/* Display as 1-based */}
          </Button>
        ))}

        {/* Next button */}
        <Button
          variant="outline"
          size="sm"
          onClick={() => onPageChange(number + 1)}
          disabled={last}
          className="bg-white/5 border-white/10 text-white hover:bg-white/10 disabled:opacity-50"
        >
          <ChevronRight className="w-4 h-4" />
        </Button>
      </div>
    </div>
  );
}
