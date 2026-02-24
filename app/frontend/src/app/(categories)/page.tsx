'use client';

import Link from 'next/link';
import { motion } from 'framer-motion';
import { Button } from '@/components/ui/button';
import { useAuth } from '@/contexts/AuthContext';

export default function Home() {
  const { isAuthenticated, isLoading } = useAuth();

  const startHref = isAuthenticated ? '/dashboard' : '/login';

  return (
    <main className="flex flex-col grow items-center justify-center overflow-hidden relative">
      <div className="absolute inset-0 pointer-events-none">
        <div
          className="absolute inset-0"
          style={{
            background:
              'radial-gradient(circle at 50% 50%, rgba(139, 92, 246, 0.15) 0%, rgba(59, 130, 246, 0.1) 25%, transparent 50%)',
          }}
        />
      </div>

      <motion.div
        className="absolute top-1/4 left-1/4 w-80 h-80 rounded-full blur-3xl opacity-20 pointer-events-none"
        style={{
          background: 'radial-gradient(circle, #8b5cf6 0%, transparent 70%)',
        }}
        animate={{
          x: [0, 30, 0],
          y: [0, -20, 0],
          scale: [1, 1.1, 1],
        }}
        transition={{
          duration: 20,
          repeat: Infinity,
          ease: 'easeInOut',
        }}
      />

      <motion.div
        className="absolute bottom-1/3 right-1/4 w-[400px] h-[400px] rounded-full blur-3xl opacity-15 pointer-events-none"
        style={{
          background: 'radial-gradient(circle, #3b82f6 0%, transparent 70%)',
        }}
        animate={{
          x: [0, -40, 0],
          y: [0, 30, 0],
          scale: [1, 1.15, 1],
        }}
        transition={{
          duration: 25,
          repeat: Infinity,
          ease: 'easeInOut',
        }}
      />

      <motion.div
        className="absolute top-1/2 right-1/3 w-60 h-60 rounded-full blur-3xl opacity-10 pointer-events-none"
        style={{
          background: 'radial-gradient(circle, #6366f1 0%, transparent 70%)',
        }}
        animate={{
          x: [0, 20, 0],
          y: [0, -30, 0],
          scale: [1, 1.2, 1],
        }}
        transition={{
          duration: 18,
          repeat: Infinity,
          ease: 'easeInOut',
        }}
      />

      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.8, ease: 'easeOut' }}
        className="flex flex-col items-center justify-center text-center relative z-10"
      >
        <h1
          className="text-[min(100px,10vw)] font-black italic text-white"
          style={{ fontFamily: 'var(--font-exo-2)' }}
        >
          VocaTree
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
