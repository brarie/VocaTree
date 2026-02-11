'use client';

import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Book, Lightbulb, MessageSquare } from 'lucide-react';

interface HelpDialogProps {
  open: boolean;
  onClose: () => void;
}

export function HelpDialog({ open, onClose }: HelpDialogProps) {
  return (
    <Dialog open={open} onOpenChange={onClose}>
      <DialogContent className="max-w-3xl max-h-[85vh] bg-zinc-900 border-zinc-800 text-white">
        <DialogHeader>
          <DialogTitle className="text-xl font-bold">
            단어장 사용 가이드
          </DialogTitle>
          <DialogDescription className="text-zinc-400">
            단어장의 구조와 사용 방법을 알아보세요
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-6 overflow-y-auto max-h-[65vh] pr-2">
          {/* 3레벨 구조 설명 */}
          <section>
            <h3 className="text-lg font-semibold text-white mb-3 flex items-center gap-2">
              <Book className="h-5 w-5 text-blue-400" />
              3단계 계층 구조
            </h3>
            <div className="space-y-4">
              <div className="bg-zinc-800/50 p-4 rounded-lg border border-zinc-700">
                <div className="flex items-start gap-3">
                  <div className="flex-shrink-0 w-8 h-8 bg-zinc-700 rounded flex items-center justify-center text-sm font-bold">
                    1
                  </div>
                  <div>
                    <h4 className="font-semibold text-white mb-1">
                      단어 (Word)
                    </h4>
                    <p className="text-sm text-zinc-300 mb-2">
                      단어의 철자를 담고 있어요.
                    </p>
                    <div className="text-xs text-zinc-400 bg-zinc-900 px-2 py-1 rounded inline-block">
                      예: minute (철자)
                    </div>
                  </div>
                </div>
              </div>

              <div className="bg-zinc-800/50 p-4 rounded-lg border border-zinc-700">
                <div className="flex items-start gap-3">
                  <div className="flex-shrink-0 w-8 h-8 bg-zinc-700 rounded flex items-center justify-center text-sm font-bold">
                    2
                  </div>
                  <div>
                    <h4 className="font-semibold text-white mb-1">
                      의미 (Sense)
                    </h4>
                    <p className="text-sm text-zinc-300 mb-2">
                      단어의 각 의미를 나타냅니다. 하나의 단어는 여러 의미를
                      가질 수 있습니다. 또한 의미마다 다른 발음을 가질 수도
                      있어요.
                    </p>
                    <div className="text-xs text-zinc-400 bg-zinc-900 px-2 py-1 rounded inline-block">
                      예: &quot;minute&quot; = &quot;분&quot; (명사, ˈmɪnɪt) /
                      &quot;작은&quot; (형용사, maɪˈnjuːt)
                    </div>
                  </div>
                </div>
              </div>

              <div className="bg-zinc-800/50 p-4 rounded-lg border border-zinc-700">
                <div className="flex items-start gap-3">
                  <div className="flex-shrink-0 w-8 h-8 bg-zinc-700 rounded flex items-center justify-center text-sm font-bold">
                    3
                  </div>
                  <div>
                    <h4 className="font-semibold text-white mb-1">
                      예문 (Example)
                    </h4>
                    <p className="text-sm text-zinc-300 mb-2">
                      각 의미에 대한 예문과 번역입니다. 하나의 의미는 여러
                      예문을 가질 수 있어요.
                    </p>
                    <div className="text-xs text-zinc-400 bg-zinc-900 px-2 py-1 rounded inline-block">
                      예: &quot;The difference is really minute.&quot; -&gt;
                      &quot;차이가 정말 미세해.&quot;
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </section>

          <section>
            <h3 className="text-lg font-semibold text-white mb-3 flex items-center gap-2">
              <MessageSquare className="h-5 w-5 text-green-400" />
              사용 방법
            </h3>
            <div className="space-y-3">
              <div className="bg-zinc-800/50 p-3 rounded-lg border border-zinc-700">
                <h4 className="text-sm font-semibold text-white mb-2">
                  🖱️ 마우스로 편집하기
                </h4>
                <ul className="text-sm text-zinc-300 space-y-1 ml-4 list-disc">
                  <li>셀을 클릭하면 바로 편집 모드로 진입해요.</li>
                  <li>
                    입력 후 다른 셀을 클릭하거나 Enter를 누르면 자동 저장돼요.
                  </li>
                  <li>ESC를 누르면 편집 모드에서 나갈 수 있어요.</li>
                </ul>
              </div>

              <div className="bg-zinc-800/50 p-3 rounded-lg border border-zinc-700">
                <h4 className="text-sm font-semibold text-white mb-2">
                  ⌨️ 키보드로 빠르게 작업하기
                </h4>
                <ul className="text-sm text-zinc-300 space-y-1 ml-4 list-disc">
                  <li>
                    <kbd className="px-1 py-0.5 bg-zinc-700 rounded text-xs">
                      ↑↓←→
                    </kbd>{' '}
                    방향키로 셀 이동
                  </li>
                  <li>
                    <kbd className="px-1 py-0.5 bg-zinc-700 rounded text-xs">
                      Tab
                    </kbd>{' '}
                    다음 셀,{' '}
                    <kbd className="px-1 py-0.5 bg-zinc-700 rounded text-xs">
                      Shift+Tab
                    </kbd>{' '}
                    이전 셀
                  </li>
                  <li>
                    <kbd className="px-1 py-0.5 bg-zinc-700 rounded text-xs">
                      Ctrl+Shift+Enter
                    </kbd>{' '}
                    새 단어 추가
                  </li>
                  <li>
                    <kbd className="px-1 py-0.5 bg-zinc-700 rounded text-xs">
                      Ctrl+Enter
                    </kbd>{' '}
                    <strong>하위 레벨 추가 (의미/예문)</strong>
                  </li>
                  <li>
                    <kbd className="px-1 py-0.5 bg-zinc-700 rounded text-xs">
                      Delete
                    </kbd>{' '}
                    셀 내용 지우기
                  </li>
                  <li>
                    <kbd className="px-1 py-0.5 bg-zinc-700 rounded text-xs">
                      Ctrl+Shift+D
                    </kbd>{' '}
                    현재 레벨 삭제
                  </li>
                </ul>
                <p className="text-xs text-zinc-500 mt-2">
                  💡 더 자세한 단축키는 &quot;단축키&quot; 버튼을 눌러보세요!
                </p>
              </div>

              <div className="bg-zinc-800/50 p-3 rounded-lg border border-zinc-700">
                <h4 className="text-sm font-semibold text-white mb-2">
                  ⚙️ 칼럼 관리
                </h4>
                <ul className="text-sm text-zinc-300 space-y-1 ml-4 list-disc">
                  <li>
                    &quot;칼럼 관리&quot; 버튼을 클릭하여 원하는 칼럼만 표시할
                    수 있어요.
                  </li>
                  <li>
                    칼럼 헤더의 오른쪽 경계를 드래그하여 너비를 조정할 수
                    있어요.
                  </li>
                  <li>
                    칼럼 헤더의 오른쪽 경계를 더블클릭하면 자동으로 내용에 맞게
                    조정돼요.
                  </li>
                  <li>
                    칼럼을 비활성화한다고 해서 내용이 사라지는 건 아니에요.
                  </li>
                </ul>
              </div>

              <div className="bg-zinc-800/50 p-3 rounded-lg border border-zinc-700">
                <h4 className="text-sm font-semibold text-white mb-2">
                  📏 제한 사항
                </h4>
                <ul className="text-sm text-zinc-300 space-y-1 ml-4 list-disc">
                  <li>단어장당 최대 100개의 단어를 추가할 수 있어요.</li>
                  <li>각 단어마다 여러 의미를 추가할 수 있어요.</li>
                  <li>각 의미마다 여러 예문을 추가할 수 있어요.</li>
                </ul>
              </div>
            </div>
          </section>
        </div>
      </DialogContent>
    </Dialog>
  );
}
