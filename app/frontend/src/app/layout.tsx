import type { Metadata } from 'next';
import { Space_Grotesk } from 'next/font/google';
import localFont from 'next/font/local';
import './globals.css';
import { ParticlesBackground } from '@/components/ParticlesBackground';
import { AuthProvider } from '@/contexts/AuthContext';
import { QueryProvider } from '@/components/QueryProvider';
import { Toaster } from 'sonner';

const pretendard = localFont({
  src: '../../fonts/pretendard/PretendardVariable.woff2',
  variable: '--font-pretendard',
  display: 'swap',
});

const spaceGrotesk = Space_Grotesk({
  variable: '--font-space-grotesk',
  subsets: ['latin'],
  weight: ['700'],
});

export const metadata: Metadata = {
  title: 'VocaTree',
  description: 'Memorize Words Efficiently',
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="ko" className="dark">
      <body
        className={`${pretendard.variable} ${spaceGrotesk.variable} font-sans antialiased bg-black`}
        style={{
          fontFamily:
            'var(--font-pretendard), -apple-system, BlinkMacSystemFont, system-ui, sans-serif',
        }}
      >
        <QueryProvider>
          <AuthProvider>
            <ParticlesBackground />
            {children}
            <Toaster position="top-center" theme="dark" closeButton />
          </AuthProvider>
        </QueryProvider>
      </body>
    </html>
  );
}
