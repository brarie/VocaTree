import { PageHeader } from '@/components/PageHeader';
import { Construction } from 'lucide-react';

export default function CommunityPage() {
  return (
    <div>
      <PageHeader
        title="커뮤니티"
        description="다른 사용자들과 함께 학습 경험을 공유하고 소통하세요."
      />

      <div className="flex flex-col items-center justify-center min-h-[400px] text-center space-y-6">
        <div className="relative">
          <div className="absolute inset-0 bg-blue-500/20 blur-xl rounded-full" />
          <div className="relative bg-white/5 p-8 rounded-full ring-1 ring-white/10 backdrop-blur-sm">
            <Construction className="w-16 h-16 text-blue-400" />
          </div>
        </div>

        <div className="space-y-3 max-w-lg">
          <h2 className="text-2xl font-bold text-white">
            서비스 준비 중입니다
          </h2>
          <p className="text-gray-400 leading-relaxed">
            더 나은 커뮤니티 서비스를 제공하기 위해 현재 페이지를 준비하고
            있습니다.
            <br />
            빠른 시일 내에 새로운 모습으로 찾아뵙겠습니다.
          </p>
        </div>
      </div>
    </div>
  );
}
