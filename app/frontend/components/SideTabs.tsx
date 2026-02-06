'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';
import { cn } from '@/lib/utils';
import { LucideIcon, ChevronRight, ChevronLeft } from 'lucide-react';
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from '@/components/ui/tooltip';

export interface SideTabItem {
  id: string;
  label: string;
  icon?: LucideIcon;
}

interface SideTabsProps {
  items: SideTabItem[];
  activeTab: string;
  className?: string;
}

export function SideTabs({ items, activeTab, className }: SideTabsProps) {
  const [expanded, setExpanded] = useState(false);

  useEffect(() => {
    const handleResize = () => {
      if (window.innerWidth >= 768) {
        setExpanded(false);
      }
    };

    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  return (
    <TooltipProvider>
      {/* Desktop: Normal sidebar */}
      <div className="hidden md:block shrink-0 border-r border-white/10 w-48 pr-6">
        <nav className="space-y-1">
          {items.map((item) => {
            const Icon = item.icon;
            const isActive = activeTab === item.id;

            return (
              <div key={item.id}>
                <Link
                  href={item.id}
                  className={cn(
                    'flex items-center gap-3 px-3 py-2 text-sm rounded-md transition-colors',
                    isActive
                      ? 'bg-white/10 text-white font-medium'
                      : 'text-gray-400 hover:text-white hover:bg-white/5',
                  )}
                >
                  {Icon && <Icon className="w-4 h-4 shrink-0" />}
                  <span className="truncate">{item.label}</span>
                </Link>
              </div>
            );
          })}
        </nav>
      </div>

      {/* Mobile: Placeholder + Overlay */}
      <div className="md:hidden relative">
        {/* Placeholder - always takes w-12 space */}
        <div className="shrink-0 w-12 pr-4" />

        {/* Actual sidebar - absolute overlay */}
        <div
          className={cn(
            'absolute left-0 top-0 border-r border-white/10 bg-background transition-all duration-300 ease-in-out z-40',
            expanded ? 'w-48 pr-6 shadow-xl' : 'w-12 pr-4',
          )}
        >
          {/* Toggle Button */}
          <button
            onClick={() => setExpanded(!expanded)}
            className="mb-2 p-2 rounded-md text-gray-400 hover:text-white hover:bg-white/5 transition-colors"
            aria-label={expanded ? '사이드바 접기' : '사이드바 펼치기'}
          >
            {expanded ? (
              <ChevronLeft className="w-4 h-4" />
            ) : (
              <ChevronRight className="w-4 h-4" />
            )}
          </button>

          <nav className="space-y-1">
            {items.map((item) => {
              const Icon = item.icon;
              const isActive = activeTab === item.id;
              const showTooltip = !expanded;

              const linkContent = (
                <Link
                  href={item.id}
                  className={cn(
                    'flex items-center gap-3 px-2 py-2 text-sm rounded-md transition-colors',
                    isActive
                      ? 'bg-white/10 text-white font-medium'
                      : 'text-gray-400 hover:text-white hover:bg-white/5',
                  )}
                  onClick={() => setExpanded(false)}
                >
                  {Icon && <Icon className="w-4 h-4 shrink-0" />}
                  <span
                    className={cn(
                      'truncate transition-opacity duration-300',
                      expanded ? 'opacity-100 block' : 'opacity-0 hidden',
                    )}
                  >
                    {item.label}
                  </span>
                </Link>
              );

              return (
                <div key={item.id}>
                  {showTooltip ? (
                    <Tooltip>
                      <TooltipTrigger asChild>{linkContent}</TooltipTrigger>
                      <TooltipContent
                        side="right"
                        className="bg-white text-black"
                      >
                        <p>{item.label}</p>
                      </TooltipContent>
                    </Tooltip>
                  ) : (
                    linkContent
                  )}
                </div>
              );
            })}
          </nav>
        </div>
      </div>
    </TooltipProvider>
  );
}
