'use client';

import { Wordbook } from '@/types/wordbook';
import { Star, Calendar, Clock, Edit } from 'lucide-react';
import { cn } from '@/lib/utils';
import Link from 'next/link';

interface WordbookCardProps {
  wordbook: Wordbook;
  onTogglePin: (id: string) => void;
}

export function WordbookCard({ wordbook, onTogglePin }: WordbookCardProps) {
  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('ko-KR', {
      year: '2-digit',
      month: '2-digit',
      day: '2-digit',
    });
  };

  return (
    <Link href={`/dashboard/my-words/${wordbook.id}`}>
      <div className="group relative bg-white/5 border border-white/10 rounded-lg p-4 hover:bg-white/10 transition-all hover:border-white/20">
        <div className="flex gap-4">
          {/* Pin Button */}
          <button
            onClick={(e) => {
              e.preventDefault();
              onTogglePin(wordbook.id);
            }}
            className={cn(
              'shrink-0 transition-colors self-start',
              wordbook.isPinned
                ? 'text-yellow-400 hover:text-yellow-500'
                : 'text-gray-600 hover:text-gray-400'
            )}
            aria-label={wordbook.isPinned ? '즐겨찾기 해제' : '즐겨찾기 추가'}
          >
            <Star
              className={cn('w-5 h-5', wordbook.isPinned && 'fill-yellow-400')}
            />
          </button>

          {/* Content */}
          <div className="flex-1 min-w-0 space-y-2">
            {/* Title */}
            <h3 className="text-white font-medium text-lg group-hover:text-white/90 truncate">
              {wordbook.name}
            </h3>

            {/* Stats & Dates */}
            <div className="flex flex-wrap items-center gap-x-4 gap-y-1 text-sm text-gray-400">
              {/* Word Count */}
              <span className="font-medium text-white">
                단어 {wordbook.wordCount}개
              </span>

              {/* Created Date */}
              <span className="flex items-center gap-1">
                <Calendar className="w-3.5 h-3.5" />
                <span className="hidden sm:inline">생성</span>
                {formatDate(wordbook.createdAt)}
              </span>

              {/* Updated Date */}
              <span className="flex items-center gap-1">
                <Edit className="w-3.5 h-3.5" />
                <span className="hidden sm:inline">수정</span>
                {formatDate(wordbook.updatedAt)}
              </span>

              {/* Last Tested */}
              {wordbook.lastTestedAt && (
                <span className="hidden md:flex items-center gap-1">
                  <Clock className="w-3.5 h-3.5" />
                  <span className="hidden lg:inline">최근 테스트</span>
                  <span className="lg:hidden">테스트</span>
                  {formatDate(wordbook.lastTestedAt)}
                </span>
              )}
            </div>
          </div>
        </div>
      </div>
    </Link>
  );
}
