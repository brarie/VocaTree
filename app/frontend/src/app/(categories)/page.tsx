'use client';

import Link from 'next/link';
import { motion } from 'framer-motion';
import { Button } from '@/components/ui/button';
import { useAuth } from '@/contexts/AuthContext';

export default function Home() {
  const { isAuthenticated, isLoading } = useAuth();

  const startHref = isAuthenticated ? '/dashboard' : '/login';

  return (
    <main className="flex flex-col grow items-center justify-center overflow-hidden">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.8, ease: 'easeOut' }}
        className="flex flex-col items-center justify-center text-center"
      >
        <h1
          className="text-[min(100px,10vw)] font-black italic text-white"
          style={{ fontFamily: 'var(--font-exo-2)' }}
        >
          MonoVoca
        </h1>

        <p className="text-[min(20px,2.5vw)] text-gray-300 mb-6 font-medium">
          당신을 위한 영어 공부 플랫폼
        </p>

        <Button
          asChild
          className="text-sm rounded-full font-bold text-white bg-black glow-border hover:bg-neutral-800"
          disabled={isLoading}
        >
          <Link href={startHref}>시작하기</Link>
        </Button>
      </motion.div>
    </main>
  );
}
