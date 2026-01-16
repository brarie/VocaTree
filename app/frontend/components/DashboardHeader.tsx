'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { BookOpen, BarChart3, LayoutDashboard } from 'lucide-react';

const menuItems = [
  { label: '대시보드', href: '/dashboard', icon: LayoutDashboard },
  { label: '내 단어장', href: '/dashboard/my-words', icon: BookOpen },
  { label: '통계', href: '/dashboard/stats', icon: BarChart3 },
];

export function DashboardHeader() {
  const pathname = usePathname();

  return (
    <header className="bg-black">
      <nav className="px-6 border-b border-white/20">
        <div className="flex items-center gap-8 overflow-x-auto scrollbar-hide">
          {menuItems.map((item) => {
            const isActive =
              item.href === '/dashboard'
                ? pathname === '/dashboard'
                : pathname.startsWith(item.href);
            const Icon = item.icon;
            return (
              <Link
                key={item.href}
                href={item.href}
                className={`flex items-center gap-2 text-sm whitespace-nowrap py-3 transition-all relative ${
                  isActive
                    ? 'text-white font-bold'
                    : 'text-gray-400 hover:text-white'
                }`}
              >
                <Icon className="w-4 h-4" />
                {item.label}
                {isActive && (
                  <span className="absolute bottom-0 left-0 right-0 h-0.5 bg-white" />
                )}
              </Link>
            );
          })}
        </div>
      </nav>
    </header>
  );
}
