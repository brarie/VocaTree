import { ReactNode } from 'react';
import { ChevronDown, ChevronUp } from 'lucide-react';
import { SupportPostView } from '@/types/api/supportBoard';

interface SupportPostItemProps {
  post: SupportPostView;
  isExpanded: boolean;
  onToggle: () => void;
  actions?: ReactNode;
}

export function SupportPostItem({ post, isExpanded, onToggle, actions }: SupportPostItemProps) {
  const formattedDate = new Date(post.createdAt).toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  });

  return (
    <div className="bg-white/5 border border-white/10 rounded-lg overflow-hidden">
      <div
        className="w-full px-6 py-4 flex items-center justify-between hover:bg-white/5 transition-colors cursor-pointer"
      >
        <div className="flex-1 text-left" onClick={onToggle}>
          <h3 className="text-lg font-medium text-white">{post.title}</h3>
        </div>
        <div className="flex items-center gap-3">
          {actions && <div>{actions}</div>}
          <div onClick={onToggle}>
            {isExpanded ? (
              <ChevronUp className="w-5 h-5 text-gray-400" />
            ) : (
              <ChevronDown className="w-5 h-5 text-gray-400" />
            )}
          </div>
        </div>
      </div>

      {isExpanded && (
        <div className="px-6 py-4 border-t border-white/10">
          <div className="flex items-center gap-3 mb-4 text-sm text-gray-400">
            <span>{post.author.nickname}</span>
            <span>•</span>
            <span>{formattedDate}</span>
            <span>•</span>
            <span>{post.supportCategoryName}</span>
          </div>
          <div className="prose prose-invert max-w-none">
            <div className="text-gray-300 whitespace-pre-wrap">{post.content}</div>
          </div>
        </div>
      )}
    </div>
  );
}
