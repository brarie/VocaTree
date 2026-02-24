'use client';

import Link from 'next/link';
import { motion } from 'framer-motion';
import { Button } from '@/components/ui/button';
import { useAuth } from '@/contexts/AuthContext';
import { TreeAnimation } from '@/components/TreeAnimation';

export default function Home() {
  const { isAuthenticated, isLoading } = useAuth();

  const startHref = isAuthenticated ? '/dashboard' : '/login';

  return (
    <main className="flex-1 relative flex flex-col items-center justify-center overflow-hidden">
      {/* 배경 glow */}
      <div
        className="absolute inset-0 pointer-events-none"
        style={{
          background:
            'radial-gradient(ellipse at 50% 40%, rgba(139,92,246,0.09) 0%, transparent 55%)',
        }}
      />

      {/* 텍스트: 화면 중앙 */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.8, ease: 'easeOut' }}
        className="relative z-10 flex flex-col items-center text-center gap-3 px-5 max-w-3xl w-full"
      >
        <h1 className="text-5xl sm:text-6xl xl:text-7xl font-bold text-white leading-[1.08] tracking-tight drop-shadow-2xl">
          Grow Your Vocabulary
        </h1>

        <div className="relative flex items-center justify-center gap-4 py-2 mb-2">
          <div className="absolute inset-0 bg-black/40 blur-xl rounded-full -z-10 scale-125" />

          <div className="w-8 h-px bg-white/50 shadow-[0_0_10px_rgba(255,255,255,0.5)]" />
          <p className="text-sm sm:text-base text-white/90 font-medium tracking-[0.15em] uppercase drop-shadow-md">
            어휘의 숲에서 성장하세요
          </p>
          <div className="w-8 h-px bg-white/50 shadow-[0_0_10px_rgba(255,255,255,0.5)]" />
        </div>

        <Button
          asChild
          className="text-sm px-5 h-10 rounded-full font-semibold bg-white/5 hover:bg-white/20 text-white backdrop-blur-md transition-all duration-300 shadow-lg glow-border"
          disabled={isLoading}
        >
          <Link href={startHref}>시작하기</Link>
        </Button>
      </motion.div>

      {/* 나무: 하단 고정 배경 */}
      <div className="absolute bottom-0 inset-x-0 h-[68vh] sm:h-[72vh] pointer-events-none">
        <TreeAnimation />
      </div>
    </main>
  );
}
