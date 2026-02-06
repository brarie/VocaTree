'use client';

import { useEffect } from 'react';
import { useParams } from 'next/navigation';
import { useWordbook } from '@/hooks/wordbook/queries';
import { usePageHeader } from '@/contexts/PageHeaderContext';

export default function WordbookDetailPage() {
  const params = useParams();
  const wordbookId = parseInt(params.id as string, 10);

  const { data: wordbook, isLoading } = useWordbook(wordbookId);
  const { setPageHeader, clearPageHeader } = usePageHeader();

  useEffect(() => {
    if (wordbook) {
      setPageHeader(
        wordbook.title,
        '단어장: 단어를 추가하고 관리할 수 있어요.',
      );
    }
    return () => {
      clearPageHeader();
    };
  }, [wordbook, setPageHeader, clearPageHeader]);

  if (isLoading) {
    return (
      <div className="text-center py-12 text-muted-foreground">로딩 중...</div>
    );
  }

  if (!wordbook) {
    return (
      <div className="text-center py-12 text-muted-foreground">
        단어장을 찾을 수 없습니다.
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <p className="text-muted-foreground">
        이 페이지는 아직 구현되지 않았습니다.
      </p>
    </div>
  );
}
