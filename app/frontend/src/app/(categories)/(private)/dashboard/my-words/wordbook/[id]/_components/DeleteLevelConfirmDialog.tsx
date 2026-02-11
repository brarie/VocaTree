'use client';

import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog';

interface DeleteLevelConfirmDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  level: 1 | 2 | 3;
  onConfirm: () => void;
  isPending?: boolean;
}

export function DeleteLevelConfirmDialog({
  open,
  onOpenChange,
  level,
  onConfirm,
  isPending = false,
}: DeleteLevelConfirmDialogProps) {
  const getTitle = () => {
    if (level === 1) return '단어를 삭제하시겠습니까?';
    if (level === 2) return '의미를 삭제하시겠습니까?';
    return '예문을 삭제하시겠습니까?';
  };

  const getDescription = () => {
    if (level === 1) {
      return (
        <>
          이 단어를 삭제하면 복구할 수 없습니다.
          <br />
          <span className="text-destructive font-medium">
            모든 의미와 예문도 함께 삭제됩니다.
          </span>
        </>
      );
    }
    if (level === 2) {
      return (
        <>
          이 의미를 삭제하면 복구할 수 없습니다.
          <br />
          <span className="text-destructive font-medium">
            모든 예문도 함께 삭제됩니다.
          </span>
        </>
      );
    }
    return '이 예문을 삭제하면 복구할 수 없습니다.';
  };

  const handleConfirm = () => {
    onConfirm();
    onOpenChange(false);
  };

  return (
    <AlertDialog open={open} onOpenChange={onOpenChange}>
      <AlertDialogContent className="bg-zinc-900 border-zinc-800 text-white">
        <AlertDialogHeader>
          <AlertDialogTitle>{getTitle()}</AlertDialogTitle>
          <AlertDialogDescription className="text-zinc-400">
            {getDescription()}
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel className="bg-zinc-800 border-zinc-700 hover:bg-zinc-700 text-white">
            취소
          </AlertDialogCancel>
          <AlertDialogAction
            onClick={handleConfirm}
            className="bg-red-600 hover:bg-red-700 text-white"
            disabled={isPending}
          >
            {isPending ? '삭제 중...' : '삭제'}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
}
