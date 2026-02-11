'use client';

import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';

interface KeyboardShortcutsDialogProps {
  open: boolean;
  onClose: () => void;
}

export function KeyboardShortcutsDialog({
  open,
  onClose,
}: KeyboardShortcutsDialogProps) {
  const shortcuts = [
    {
      category: '네비게이션',
      items: [
        { keys: ['↑', '↓', '←', '→'], description: '셀 이동' },
        { keys: ['Tab'], description: '다음 셀로 이동' },
        { keys: ['Shift', 'Tab'], description: '이전 셀로 이동' },
      ],
    },
    {
      category: '편집',
      items: [
        { keys: ['Enter'], description: '편집 모드 시작' },
        { keys: ['ESC'], description: '편집 취소' },
        { keys: ['Delete'], description: '셀 내용 지우기' },
      ],
    },
    {
      category: '추가',
      items: [
        {
          keys: ['Ctrl', 'Shift', 'Enter'],
          description: '새 단어 추가',
        },
        {
          keys: ['Ctrl', 'Enter'],
          description: '하위 레벨 추가 (현재 칼럼 기준)',
        },
      ],
    },
    {
      category: '삭제',
      items: [
        {
          keys: ['Ctrl', 'Shift', 'D'],
          description: '현재 레벨 삭제',
        },
      ],
    },
    {
      category: '순서 변경',
      items: [
        {
          keys: ['Alt', '↑'],
          description: '위로 이동 (인접 항목과 교환)',
        },
        {
          keys: ['Alt', '↓'],
          description: '아래로 이동 (인접 항목과 교환)',
        },
      ],
    },
  ];

  return (
    <Dialog open={open} onOpenChange={onClose}>
      <DialogContent className="max-w-2xl max-h-[80vh] bg-zinc-900 border-zinc-800 text-white">
        <DialogHeader>
          <DialogTitle className="text-xl font-bold">키보드 단축키</DialogTitle>
          <DialogDescription className="text-zinc-400">
            단어장 테이블을 빠르게 편집하기 위한 단축키 모음
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-6 mt-4 overflow-y-auto max-h-[60vh] pr-2">
          {shortcuts.map((section) => (
            <div key={section.category}>
              <h3 className="text-sm font-semibold text-zinc-300 mb-3">
                {section.category}
              </h3>
              <div className="space-y-2">
                {section.items.map((item, idx) => (
                  <div
                    key={idx}
                    className="flex items-center justify-between py-2 px-3 rounded bg-zinc-800/50 hover:bg-zinc-800 transition-colors"
                  >
                    <span className="text-sm text-zinc-300">
                      {item.description}
                    </span>
                    <div className="flex items-center gap-1">
                      {item.keys.map((key, keyIdx) => (
                        <span key={keyIdx} className="flex items-center gap-1">
                          <kbd className="px-2 py-1 text-xs font-semibold text-white bg-zinc-700 border border-zinc-600 rounded shadow-sm">
                            {key}
                          </kbd>
                          {keyIdx < item.keys.length - 1 && (
                            <span className="text-zinc-500">+</span>
                          )}
                        </span>
                      ))}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          ))}

          {/* 팁을 스크롤 영역 안에 포함 */}
          <div className="mt-6 pt-4 border-t border-zinc-800 space-y-2">
            <p className="text-xs text-zinc-500">
              💡 팁: 단어장 테이블에서 클릭 없이 키보드만으로 빠르게 작업할 수
              있습니다.
            </p>
            <p className="text-xs text-zinc-500">
              🖱️ 팁: Shift + 마우스 휠로 테이블을 좌우로 스크롤할 수 있습니다.
            </p>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
}
