import Link from 'next/link';
import { cn } from '@/lib/utils';

type LogoSize = 'sm' | 'md' | 'lg';

const sizes: Record<LogoSize, { svg: number; text: string; gap: string }> = {
  sm: { svg: 18, text: 'text-base font-bold', gap: 'gap-1.5' },
  md: { svg: 24, text: 'text-xl font-bold', gap: 'gap-2' },
  lg: { svg: 48, text: 'text-5xl font-bold', gap: 'gap-3' },
};

interface LogoProps {
  size?: LogoSize;
  className?: string;
}

export function Logo({ size = 'md', className }: LogoProps) {
  const { svg, text, gap } = sizes[size];

  return (
    <Link href="/" className={cn('group flex items-center', gap, className)}>
      <svg
        width={svg}
        height={svg}
        viewBox="0 0 26 26"
        fill="none"
        aria-hidden="true"
        className="shrink-0 transition-transform duration-200 group-hover:scale-110"
      >
        {/* 줄기 - 흰색, 아래에서 위까지 관통 */}
        <line
          x1="13"
          y1="23"
          x2="13"
          y2="7"
          stroke="white"
          strokeWidth="1.6"
          strokeLinecap="round"
        />
        {/* 중간 가지 - 초록색 */}
        <line
          x1="13"
          y1="16"
          x2="7"
          y2="10"
          stroke="#34d399"
          strokeWidth="1.3"
          strokeLinecap="round"
        />
        <line
          x1="13"
          y1="16"
          x2="19"
          y2="10"
          stroke="#34d399"
          strokeWidth="1.3"
          strokeLinecap="round"
        />
        {/* 중간 가지 갈래 */}
        <line
          x1="7"
          y1="10"
          x2="4"
          y2="6"
          stroke="#34d399"
          strokeWidth="1.0"
          strokeLinecap="round"
        />
        <line
          x1="7"
          y1="10"
          x2="9"
          y2="5"
          stroke="#34d399"
          strokeWidth="1.0"
          strokeLinecap="round"
        />
        <line
          x1="19"
          y1="10"
          x2="17"
          y2="5"
          stroke="#34d399"
          strokeWidth="1.0"
          strokeLinecap="round"
        />
        <line
          x1="19"
          y1="10"
          x2="22"
          y2="6"
          stroke="#34d399"
          strokeWidth="1.0"
          strokeLinecap="round"
        />
        {/* 줄기 꼭대기 갈래 */}
        <line
          x1="13"
          y1="7"
          x2="11"
          y2="4"
          stroke="#34d399"
          strokeWidth="1.0"
          strokeLinecap="round"
        />
        <line
          x1="13"
          y1="7"
          x2="15"
          y2="4"
          stroke="#34d399"
          strokeWidth="1.0"
          strokeLinecap="round"
        />
        {/* 끝 노드 dots */}
        <circle cx="4" cy="6" r="1.3" fill="#34d399" />
        <circle cx="9" cy="5" r="1.3" fill="#34d399" />
        <circle cx="11" cy="4" r="1.3" fill="#34d399" />
        <circle cx="15" cy="4" r="1.3" fill="#34d399" />
        <circle cx="17" cy="5" r="1.3" fill="#34d399" />
        <circle cx="22" cy="6" r="1.3" fill="#34d399" />
      </svg>
      <span
        className={cn('tracking-tight text-white', text)}
        style={{ fontFamily: 'var(--font-space-grotesk)' }}
      >
        VocaTree
      </span>
    </Link>
  );
}
