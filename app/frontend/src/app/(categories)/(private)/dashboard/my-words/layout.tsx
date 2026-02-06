'use client';

import { usePathname } from 'next/navigation';
import { SideTabs, SideTabItem } from '@/components/SideTabs';
import { BookOpen, Bookmark } from 'lucide-react';
import { PageHeader } from '@/components/PageHeader';
import {
  PageHeaderProvider,
  usePageHeader,
} from '@/contexts/PageHeaderContext';

const tabs: SideTabItem[] = [
  { id: '/dashboard/my-words/wordbook', label: '단어장', icon: BookOpen },
  { id: '/dashboard/my-words/subscription', label: '구독함', icon: Bookmark },
];

const tabHeaders = {
  '/dashboard/my-words/wordbook': {
    title: '단어장',
    description: '내 단어장을 관리할 수 있어요.',
  },
  '/dashboard/my-words/subscription': {
    title: '구독함',
    description: '라이브러리에서 구독한 단어장을 볼 수 있어요.',
  },
};

function MyWordsLayoutContent({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();
  const { title: contextTitle, description: contextDescription } =
    usePageHeader();

  const staticHeader = tabHeaders[pathname as keyof typeof tabHeaders];

  // Context 값이 있으면 우선 사용, 없으면 정적 헤더 사용
  const title = contextTitle || staticHeader?.title || '';
  const description = contextDescription || staticHeader?.description || '';

  return (
    <div>
      <PageHeader title={title} description={description} />

      <div className="relative flex gap-3">
        <SideTabs items={tabs} activeTab={pathname} />
        <div className="flex-1 min-w-0">{children}</div>
      </div>
    </div>
  );
}

export default function MyWordsLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <PageHeaderProvider>
      <MyWordsLayoutContent>{children}</MyWordsLayoutContent>
    </PageHeaderProvider>
  );
}
