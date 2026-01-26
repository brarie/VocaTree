'use client';

import { useState, useEffect, Suspense } from 'react';
import Link from 'next/link';
import { useSearchParams } from 'next/navigation';
import { motion } from 'framer-motion';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader } from '@/components/ui/card';
import { verify } from '@/api/user';

function VerifyContent() {
  const searchParams = useSearchParams();
  const code = searchParams.get('code');

  const [status, setStatus] = useState<'loading' | 'success' | 'error'>(
    'loading',
  );
  const [error, setError] = useState('');

  useEffect(() => {
    const verifyCode = async () => {
      if (!code) {
        setStatus('error');
        setError('인증 코드가 없습니다.');
        return;
      }

      try {
        await verify({ code });
        setStatus('success');
      } catch (err) {
        setStatus('error');
        setError(err instanceof Error ? err.message : '인증에 실패했습니다.');
      }
    };

    verifyCode();
  }, [code]);

  return (
    <main className="min-h-screen flex items-center justify-center p-4">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.6, ease: 'easeOut' }}
      >
        <Card className="w-[min(var(--container-md),85vw)] bg-black/90 backdrop-blur-sm glow-border">
          <CardHeader>
            <div className="pt-8 pb-4 flex justify-center">
              <Link href="/" className="group">
                <h1
                  className="text-[min(var(--text-5xl),12vw)] font-black italic text-white group-hover:text-white/80 transition-colors"
                  style={{ fontFamily: 'var(--font-exo-2)' }}
                >
                  MonoVoca
                </h1>
              </Link>
            </div>
          </CardHeader>

          <CardContent className="space-y-4 text-center py-8 -mt-16">
            {status === 'loading' && (
              <div className="space-y-4">
                <div className="text-6xl">⏳</div>
                <h2 className="text-2xl font-bold text-white">인증 중...</h2>
                <p className="text-gray-300">잠시만 기다려주세요.</p>
              </div>
            )}

            {status === 'success' && (
              <div className="space-y-4">
                <div className="text-6xl">✅</div>
                <h2 className="text-2xl font-bold text-white pt-2">
                  회원가입 완료!
                </h2>
                <p className="text-gray-300">
                  이메일 인증이 완료되었습니다.
                  <br />
                  이제 로그인하여 MonoVoca를 사용하실 수 있습니다.
                </p>

                <div className="pt-4 -mb-8">
                  <Button
                    asChild
                    className="w-full bg-white text-black hover:bg-gray-200 font-semibold"
                    size="lg"
                  >
                    <Link href="/login">로그인하기</Link>
                  </Button>
                </div>
              </div>
            )}

            {status === 'error' && (
              <div className="space-y-4">
                <div className="text-6xl">❌</div>
                <h2 className="text-2xl font-bold text-white pt-2">
                  인증 실패
                </h2>
                <p className="text-gray-300">{error}</p>
                <p className="text-sm text-gray-400">
                  인증 링크가 만료되었거나 올바르지 않습니다.
                  <br />
                  회원가입 페이지에서 인증 메일을 다시 받아주세요.
                </p>

                <div className="pt-4 space-y-2 -mb-8">
                  <Button
                    asChild
                    variant="outline"
                    className="w-full bg-white/5 border-white/10 text-white hover:bg-white/10 hover:text-white"
                  >
                    <Link href="/sign-up">회원가입 페이지로</Link>
                  </Button>

                  <Button
                    asChild
                    variant="outline"
                    className="w-full bg-white/5 border-white/10 text-white hover:bg-white/10 hover:text-white"
                  >
                    <Link href="/">홈으로</Link>
                  </Button>
                </div>
              </div>
            )}
          </CardContent>
        </Card>
      </motion.div>
    </main>
  );
}

export default function Verify() {
  return (
    <Suspense
      fallback={
        <main className="min-h-screen flex items-center justify-center p-4">
          <div className="text-white">Loading...</div>
        </main>
      }
    >
      <VerifyContent />
    </Suspense>
  );
}
