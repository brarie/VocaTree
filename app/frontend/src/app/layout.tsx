import type { Metadata } from 'next';
import { Exo_2 } from 'next/font/google';
import localFont from 'next/font/local';
import './globals.css';
import { ParticlesBackground } from '@/components/ParticlesBackground';

const pretendard = localFont({
  src: '../../fonts/pretendard/PretendardVariable.woff2',
  variable: '--font-pretendard',
  display: 'swap',
});

const exo2 = Exo_2({
  variable: '--font-exo-2',
  subsets: ['latin'],
  weight: ['900'],
  style: ['italic'],
});

export const metadata: Metadata = {
  title: 'MonoVoca',
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
        className={`${pretendard.variable} ${exo2.variable} font-sans antialiased bg-black`}
        style={{ fontFamily: 'var(--font-pretendard), -apple-system, BlinkMacSystemFont, system-ui, sans-serif' }}
      >
        {children}
        <ParticlesBackground />
      </body>
    </html>
  );
}
