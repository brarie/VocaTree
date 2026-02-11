'use client';

import { Skeleton } from '@/components/ui/skeleton';

export function TableSkeleton() {
  return (
    <div className="space-y-4">
      {/* Toolbar skeleton */}
      <div className="flex flex-wrap items-center justify-between gap-2">
        <Skeleton className="h-5 w-24 bg-zinc-800" />
        <div className="flex gap-2 flex-wrap">
          <Skeleton className="h-8 w-20 bg-zinc-800" />
          <Skeleton className="h-8 w-20 bg-zinc-800" />
          <Skeleton className="h-8 w-24 bg-zinc-800" />
          <Skeleton className="h-8 w-28 bg-zinc-800" />
        </div>
      </div>

      {/* Table skeleton */}
      <div className="border border-zinc-800 rounded-lg overflow-hidden bg-zinc-950">
        <div className="space-y-0">
          {/* Header */}
          <div className="flex gap-0 border-b border-zinc-800 bg-zinc-900">
            <Skeleton className="h-10 flex-1 rounded-none bg-zinc-800" />
            <Skeleton className="h-10 flex-1 rounded-none bg-zinc-800" />
            <Skeleton className="h-10 flex-1 rounded-none bg-zinc-800" />
            <Skeleton className="h-10 flex-1 rounded-none bg-zinc-800" />
          </div>

          {/* Rows */}
          {[...Array(5)].map((_, i) => (
            <div key={i} className="flex gap-0 border-b border-zinc-800">
              <div className="flex-1 p-3">
                <Skeleton className="h-5 w-full bg-zinc-800" />
              </div>
              <div className="flex-1 p-3">
                <Skeleton className="h-5 w-3/4 bg-zinc-800" />
              </div>
              <div className="flex-1 p-3">
                <Skeleton className="h-5 w-1/2 bg-zinc-800" />
              </div>
              <div className="flex-1 p-3">
                <Skeleton className="h-5 w-2/3 bg-zinc-800" />
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
