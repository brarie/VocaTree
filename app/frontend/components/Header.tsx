'use client';

import * as React from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { Menu, Bell, LogOut, User, LayoutDashboard } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Logo } from '@/components/Logo';
import {
  Sheet,
  SheetContent,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from '@/components/ui/sheet';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { useAuth } from '@/contexts/AuthContext';

const menuItems = [
  { label: '홈', href: '/' },
  { label: '도움마당', href: '/support' },
  { label: '커뮤니티', href: '/community' },
  { label: '라이브러리', href: '/library' },
  { label: '공용사전', href: '/dictionary' },
];

export function Header() {
  const pathname = usePathname();
  const { user, isAuthenticated, isLoading, logout } = useAuth();
  const [isSheetOpen, setIsSheetOpen] = React.useState(false);
  const [mounted, setMounted] = React.useState(false);

  React.useEffect(() => {
    setMounted(true);
  }, []);

  return (
    <header className="border-b border-white/20 px-4 h-16 bg-black">
      <div className="grid grid-cols-3 h-full items-center">
        {/* Left: Logo */}
        <div className="flex justify-start">
          <Logo size="md" />
        </div>

        {/* Center: Navigation */}
        <nav className="hidden xl:flex items-center justify-center gap-8">
          {menuItems.map((item) => {
            const isActive = pathname === item.href;
            return (
              <Link
                key={item.href}
                href={item.href}
                className={`text-sm transition-all relative py-1 ${
                  isActive
                    ? 'text-foreground font-bold'
                    : 'text-muted-foreground hover:text-foreground'
                }`}
              >
                {item.label}
                {isActive && (
                  <span className="absolute -bottom-[5px] left-0 right-0 h-0.5 bg-foreground rounded-full" />
                )}
              </Link>
            );
          })}
        </nav>

        {/* Right: Auth Buttons or User Menu */}
        <div className="flex items-center justify-end gap-3 col-start-3">
          {/* Notification Bell - Always visible when authenticated */}
          {mounted && isAuthenticated && (
            <>
              <Button
                variant="ghost"
                size="icon"
                className="text-white hover:bg-white/10"
              >
                <Bell className="h-5 w-5" />
                <span className="sr-only">알림</span>
              </Button>

              {/* User Menu Dropdown - Always visible when authenticated */}
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Button
                    variant="ghost"
                    size="icon"
                    className="text-white hover:bg-white/10"
                  >
                    <User className="h-5 w-5" />
                    <span className="sr-only">사용자 메뉴</span>
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end" className="w-56">
                  <DropdownMenuLabel className="font-normal">
                    <div className="flex flex-col space-y-1">
                      <p className="text-sm font-medium leading-none">
                        {user?.nickname}
                      </p>
                      <p className="text-xs leading-none text-muted-foreground">
                        @{user?.username}
                      </p>
                      <p className="text-xs leading-none text-muted-foreground">
                        {user?.email}
                      </p>
                    </div>
                  </DropdownMenuLabel>
                  <DropdownMenuSeparator />
                  <DropdownMenuItem asChild>
                    <Link href="/dashboard" className="cursor-pointer">
                      <LayoutDashboard className="h-4 w-4" />
                      대시보드
                    </Link>
                  </DropdownMenuItem>
                  <DropdownMenuSeparator />
                  <DropdownMenuItem
                    onClick={logout}
                    className="cursor-pointer "
                  >
                    <LogOut className="h-4 w-4" />
                    로그아웃
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
            </>
          )}

          {/* Auth buttons - only desktop, only when not loading */}
          {mounted && !isLoading && !isAuthenticated && (
            <>
              <Button
                variant="outline"
                asChild
                className="hidden xl:flex text-xs font-bold border-white bg-transparent hover:bg-white/10 text-white"
              >
                <Link href="/login">로그인</Link>
              </Button>
              <Button
                asChild
                className="hidden xl:flex text-xs font-bold bg-white text-black hover:bg-white/90"
              >
                <Link href="/sign-up">회원가입</Link>
              </Button>
            </>
          )}

          {/* Mobile Navigation */}
          {mounted && (
            <Sheet open={isSheetOpen} onOpenChange={setIsSheetOpen}>
              <SheetTrigger className="flex xl:hidden" asChild>
                <Button variant="ghost" size="icon">
                  <Menu className="h-5 w-5" />
                  <span className="sr-only">메뉴 열기</span>
                </Button>
              </SheetTrigger>
              <SheetContent side="right" className="w-[300px]">
                <SheetHeader>
                  <SheetTitle>메뉴</SheetTitle>
                </SheetHeader>
                <div className="flex flex-col gap-4 p-4 pt-0">
                  {menuItems.map((item) => {
                    const isActive = pathname === item.href;
                    return (
                      <Link
                        key={item.href}
                        href={item.href}
                        onClick={() => setIsSheetOpen(false)}
                        className={`text-base font-medium transition-colors py-2 ${
                          isActive
                            ? 'text-foreground font-semibold'
                            : 'text-muted-foreground hover:text-foreground'
                        }`}
                      >
                        {item.label}
                      </Link>
                    );
                  })}

                  {isAuthenticated && (
                    <>
                      <Button
                        variant="outline"
                        onClick={() => {
                          setIsSheetOpen(false);
                          logout();
                        }}
                        className="text-xs font-bold border-white bg-transparent hover:bg-white/10 text-white"
                      >
                        로그아웃
                      </Button>
                    </>
                  )}

                  {!isLoading && !isAuthenticated && (
                    <div className="flex flex-col gap-4">
                      <Button
                        variant="outline"
                        asChild
                        className="text-xs font-bold border-white bg-transparent hover:bg-white/10 text-white"
                      >
                        <Link href="/login">로그인</Link>
                      </Button>
                      <Button
                        asChild
                        className="text-xs font-bold bg-white text-black hover:bg-white/90"
                      >
                        <Link href="/sign-up">회원가입</Link>
                      </Button>
                    </div>
                  )}
                </div>
              </SheetContent>
            </Sheet>
          )}
        </div>
      </div>
    </header>
  );
}
