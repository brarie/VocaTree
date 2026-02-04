'use client';

import { useParams, useRouter } from 'next/navigation';
import { ArrowLeft, Settings } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { PageHeader } from '@/components/PageHeader';
import { useWord } from '@/hooks/dictionary/queries';
import { AuthOnly } from '@/components/auth/AuthOnly';
import { DictionarySense } from '../_components/DictionarySense';
import Link from 'next/link';

export default function WordDetailPage() {
  const params = useParams();
  const router = useRouter();
  const slug = params.slug as string;

  const { data: word, isLoading, error } = useWord(slug);

  if (isLoading) {
    return (
      <div className="text-center py-12">
        <div className="inline-block h-8 w-8 animate-spin rounded-full border-4 border-solid border-current border-r-transparent"></div>
        <p className="mt-4 text-gray-400">로딩 중...</p>
      </div>
    );
  }

  if (error || !word) {
    return (
      <div className="text-center py-12 space-y-4">
        <p className="text-gray-400">단어를 찾을 수 없습니다.</p>
        <Button onClick={() => router.push('/dictionary')}>
          검색 페이지로 돌아가기
        </Button>
      </div>
    );
  }

  const topDefinitions =
    word.senses.length > 0
      ? word.senses
          .slice(0, 3)
          .map((s) => s.sense.definition)
          .join(', ')
      : '의미 없음';

  return (
    <div className="space-y-6">
      <PageHeader title={word.spelling} description={topDefinitions}>
        <AuthOnly roles={['ADMIN']}>
          <Link href={`/admin/dictionary?slug=${slug}`}>
            <Button
              variant="outline"
              className="bg-white/5 border-white/10 text-white hover:bg-white/10 mt-3"
            >
              <Settings className="w-4 h-4 mr-2" />
              관리
            </Button>
          </Link>
        </AuthOnly>
        <Link href="/dictionary">
          <Button
            variant="outline"
            className="bg-white/5 border-white/10 text-white hover:bg-white/10 mt-3"
          >
            <ArrowLeft className="w-4 h-4 mr-2" />
            돌아가기
          </Button>
        </Link>
      </PageHeader>

      <div className="space-y-4">
        {word.senses.map((publicWordSense, senseIdx) => (
          <DictionarySense
            key={publicWordSense.id}
            publicWordSense={publicWordSense}
            index={senseIdx}
          />
        ))}
      </div>

      {word.senses.length === 0 && (
        <div className="text-center py-12 bg-zinc-900 rounded-lg border border-zinc-800">
          <p className="text-gray-400">등록된 의미가 없습니다.</p>
        </div>
      )}
    </div>
  );
}
