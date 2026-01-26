'use client';

import { useState, Suspense } from 'react';
import Link from 'next/link';
import { useRouter, useSearchParams } from 'next/navigation';
import { motion } from 'framer-motion';
import { Button } from '@/components/ui/button';
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
} from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { login } from '@/api/auth';
import { useAuth } from '@/contexts/AuthContext';

function LoginForm() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const redirect = searchParams.get('redirect') || '/dashboard';
  const { refetchUser } = useAuth();

  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (isLoading) return;

    setIsLoading(true);
    setError('');

    try {
      await login({ username, rawPassword: password });
      refetchUser();
      router.push(redirect);
    } catch (err) {
      setError('아이디 또는 비밀번호가 올바르지 않습니다.');
    } finally {
      setIsLoading(false);
    }
  };

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
          <CardContent>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="username" className="text-gray-200">
                  아이디
                </Label>
                <Input
                  id="username"
                  type="text"
                  placeholder="아이디를 입력하세요"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  className="bg-white/5 border-white/10 text-white placeholder:text-gray-500 focus:border-white/30 focus:ring-white/20"
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="password" className="text-gray-200">
                  비밀번호
                </Label>
                <Input
                  id="password"
                  type="password"
                  placeholder="비밀번호를 입력하세요"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="bg-white/5 border-white/10 text-white placeholder:text-gray-500 focus:border-white/30 focus:ring-white/20"
                />
              </div>

              {error && (
                <p className="text-sm text-red-400">{error}</p>
              )}

              <Button
                type="submit"
                className="w-full bg-white text-black hover:bg-gray-200 font-semibold"
                size="lg"
                disabled={isLoading}
              >
                {isLoading ? '로그인 중...' : '로그인'}
              </Button>

              <div className="relative">
                <div className="absolute inset-0 flex items-center">
                  <span className="w-full border-t border-white/10" />
                </div>
                <div className="relative flex justify-center text-xs uppercase">
                  <span className="bg-black px-2 text-gray-500">또는</span>
                </div>
              </div>

              <div className="space-y-2">
                <Button
                  type="button"
                  variant="outline"
                  className="w-full bg-white/5 border-white/10 text-white hover:bg-white/10 hover:text-white"
                >
                  네이버로 로그인
                </Button>

                <Button
                  type="button"
                  variant="outline"
                  className="w-full bg-white/5 border-white/10 text-white hover:bg-white/10 hover:text-white"
                >
                  구글로 로그인
                </Button>

                <Button
                  type="button"
                  variant="outline"
                  className="w-full bg-white/5 border-white/10 text-white hover:bg-white/10 hover:text-white"
                >
                  카카오로 로그인
                </Button>
              </div>
            </form>
          </CardContent>

          <CardFooter className="flex flex-col space-y-4 pb-8">
            <div className="flex items-center justify-center space-x-4 text-sm">
              <Link
                href="/sign-up"
                className="text-white hover:text-gray-300 font-medium transition-colors"
              >
                회원가입
              </Link>
              <span className="text-white/30">|</span>
              <Link
                href="/find-password"
                className="text-gray-400 hover:text-gray-300 transition-colors"
              >
                비밀번호 찾기
              </Link>
            </div>
          </CardFooter>
        </Card>
      </motion.div>
    </main>
  );
}

export default function Login() {
  return (
    <Suspense fallback={
      <main className="min-h-screen flex items-center justify-center p-4">
        <div className="text-white">Loading...</div>
      </main>
    }>
      <LoginForm />
    </Suspense>
  );
}
