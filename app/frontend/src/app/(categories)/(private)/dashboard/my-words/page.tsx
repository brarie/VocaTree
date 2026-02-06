'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';

export default function MyWordsPage() {
  const router = useRouter();

  useEffect(() => {
    router.replace('/dashboard/my-words/wordbook');
  }, [router]);

  return null;
}
