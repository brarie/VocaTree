'use client';

import Link from 'next/link';
import { SharedWordbookListView, SharedState } from '@/types/api/library';
import { Book, User, Clock, Ban } from 'lucide-react';
import { TrustedLevelBadge } from './TrustedLevelBadge';

interface SharedWordbookCardProps {
  wordbook: SharedWordbookListView;
}

const STATE_MESSAGE: Partial<Record<SharedState, string>> = {
  BANNED: '관리자에 의해 삭제된 단어장입니다',
  DELETED_BY_OWNER: '삭제된 단어장입니다',
};

export function SharedWordbookCard({ wordbook }: SharedWordbookCardProps) {
  const isPublic = wordbook.sharedState === SharedState.PUBLIC;
  const stateMessage = STATE_MESSAGE[wordbook.sharedState];

  const inner = (
    <div
      className={`h-full rounded-xl border bg-zinc-900 flex flex-col p-4 relative transition-all ${
        isPublic
          ? 'border-zinc-800 hover:border-zinc-600 hover:shadow-lg'
          : 'border-zinc-800 opacity-60 cursor-not-allowed'
      }`}
    >
      {/* 버전 뱃지 */}
      <div className="absolute top-3 right-3">
        <span className="text-[10px] px-1.5 py-0.5 rounded bg-zinc-700 text-zinc-300 font-medium">
          v{wordbook.latestVersion}
        </span>
      </div>

      {/* 타이틀 + 신뢰 등급 */}
      <div className="flex items-start gap-1.5 flex-1 mb-2 pr-8">
        <h3
          className={`font-bold text-sm leading-snug line-clamp-2 ${
            isPublic
              ? 'group-hover:text-gray-300 transition-colors'
              : 'text-zinc-400'
          }`}
        >
          {wordbook.title}
        </h3>
        {isPublic && (
          <TrustedLevelBadge level={wordbook.trustedLevel} className="mt-px" />
        )}
      </div>

      {/* 작성자 */}
      <div className="flex items-center gap-1.5">
        <div className="w-5 h-5 rounded-full bg-zinc-700 flex items-center justify-center shrink-0">
          <User className="w-3 h-3 text-zinc-400" />
        </div>
        <span className="text-xs text-zinc-400 truncate">
          {wordbook.creator.nickname}
        </span>
      </div>

      {/* 메타 */}
      <div className="flex items-center justify-between mt-2.5 pt-2.5 border-t border-zinc-800 text-[11px] text-zinc-500">
        {isPublic ? (
          <>
            <div className="flex items-center gap-1">
              <Clock className="w-3 h-3" />
              <span>{wordbook.createdAt.slice(0, 10).replace(/-/g, '.')}</span>
            </div>
            <div className="flex items-center gap-1">
              <Book className="w-3 h-3" />
              <span>단어장</span>
            </div>
          </>
        ) : (
          <div className="flex items-center gap-1">
            <Ban className="w-3 h-3" />
            <span>{stateMessage}</span>
          </div>
        )}
      </div>
    </div>
  );

  if (!isPublic) {
    return <div className="h-full">{inner}</div>;
  }

  return (
    <Link
      href={`/library/wordbook/${wordbook.slug}`}
      className="group block h-full"
    >
      {inner}
    </Link>
  );
}
