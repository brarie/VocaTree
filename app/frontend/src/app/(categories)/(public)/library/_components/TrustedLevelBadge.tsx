'use client';

import { BadgeCheck, CheckCircle } from 'lucide-react';
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from '@/components/ui/tooltip';
import { TrustedLevel } from '@/types/api/library';
import { cn } from '@/lib/utils';

interface TrustedLevelBadgeProps {
  level: TrustedLevel;
  className?: string;
}

const config = {
  [TrustedLevel.OFFICIAL]: {
    icon: BadgeCheck,
    color: 'text-blue-400',
    label: '공식',
    description: '공식적으로 제공되는 자료입니다.',
  },
  [TrustedLevel.VERIFIED]: {
    icon: CheckCircle,
    color: 'text-green-400',
    label: '인증됨',
    description: '검토를 거쳐 인증된 자료입니다.',
  },
} as const;

export function TrustedLevelBadge({ level, className }: TrustedLevelBadgeProps) {
  if (level === TrustedLevel.BASIC) return null;

  const { icon: Icon, color, label, description } = config[level];

  return (
    <Tooltip>
      <TooltipTrigger asChild>
        <span
          className={cn('inline-flex items-center cursor-default shrink-0', className)}
        >
          <Icon className={cn('w-4 h-4', color)} />
        </span>
      </TooltipTrigger>
      <TooltipContent side="top">
        <p className="font-semibold">{label}</p>
        <p className="text-xs opacity-80">{description}</p>
      </TooltipContent>
    </Tooltip>
  );
}
