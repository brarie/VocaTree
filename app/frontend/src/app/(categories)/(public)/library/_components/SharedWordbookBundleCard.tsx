'use client';

import Link from 'next/link';
import { SharedWordbookBundleListView } from '@/types/api/library';
import { User, Layers, Clock } from 'lucide-react';
import { TrustedLevelBadge } from './TrustedLevelBadge';

interface SharedWordbookBundleCardProps {
  bundle: SharedWordbookBundleListView;
}

export function SharedWordbookBundleCard({
  bundle,
}: SharedWordbookBundleCardProps) {
  return (
    <Link
      href={`/library/bundle/${bundle.slug}`}
      className="group block h-full"
    >
      <div className="h-full rounded-xl overflow-hidden border border-zinc-800 bg-zinc-900 hover:border-zinc-600 hover:shadow-lg transition-all flex flex-col">
        {/* 이미지 플레이스홀더 */}
        <div className="h-36 bg-zinc-800 shrink-0" />

        {/* 컨텐츠 */}
        <div className="flex flex-col flex-1 p-4">
          {/* 타이틀 + 신뢰 등급 */}
          <div className="flex items-start gap-1.5 flex-1 mb-2">
            <h3 className="font-bold text-sm leading-snug line-clamp-2 group-hover:text-gray-300 transition-colors">
              {bundle.title}
            </h3>
            <TrustedLevelBadge level={bundle.trustedLevel} className="mt-px" />
          </div>

          {/* 작성자 */}
          <div className="flex items-center gap-1.5">
            <div className="w-5 h-5 rounded-full bg-zinc-700 flex items-center justify-center shrink-0">
              <User className="w-3 h-3 text-zinc-400" />
            </div>
            <span className="text-xs text-zinc-400 truncate">
              {bundle.creator.nickname}
            </span>
          </div>

          {/* 메타 */}
          <div className="flex items-center justify-between mt-2.5 pt-2.5 border-t border-zinc-800 text-[11px] text-zinc-500">
            <div className="flex items-center gap-1">
              <Clock className="w-3 h-3" />
              <span>{bundle.createdAt.slice(0, 10).replace(/-/g, '.')}</span>
            </div>
            <div className="flex items-center gap-1">
              <Layers className="w-3 h-3" />
              <span>단어장 {bundle.wordbookCount}개</span>
            </div>
          </div>
        </div>
      </div>
    </Link>
  );
}
