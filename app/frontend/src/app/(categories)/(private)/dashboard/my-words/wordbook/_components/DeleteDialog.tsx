'use client';

import { NodeResponseWithCountView, NodeType } from '@/types/api/wordbook';
import { useDeleteWordbook, useDeleteBundle } from '@/hooks/wordbook/queries';
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

interface DeleteDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  node: NodeResponseWithCountView;
}

export function DeleteDialog({ open, onOpenChange, node }: DeleteDialogProps) {
  const deleteWordbook = useDeleteWordbook();
  const deleteBundle = useDeleteBundle();

  const isBundle = node.nodeType === NodeType.BUNDLE;

  const handleDelete = () => {
    if (isBundle) {
      deleteBundle.mutate(node.id, {
        onSuccess: () => {
          onOpenChange(false);
        },
      });
    } else {
      deleteWordbook.mutate(node.id, {
        onSuccess: () => {
          onOpenChange(false);
        },
      });
    }
  };

  return (
    <AlertDialog open={open} onOpenChange={onOpenChange}>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>정말 삭제하시겠습니까?</AlertDialogTitle>
          <AlertDialogDescription>
            {isBundle ? (
              <>
                <span className="font-semibold">{node.title}</span> 단어장
                모음을 삭제하면 복구할 수 없습니다.
                <br />
                <span className="text-destructive font-medium">
                  모음 안에 단어장이 하나라도 있을 경우 삭제되지 않습니다.
                </span>
              </>
            ) : (
              <>
                <span className="font-semibold">{node.title}</span> 단어장을
                삭제하면 복구할 수 없습니다.
                <br />
                <span className="text-destructive font-medium">
                  단어장 안의 모든 단어도 함께 삭제됩니다.
                </span>
              </>
            )}
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel>취소</AlertDialogCancel>
          <AlertDialogAction
            onClick={handleDelete}
            className="bg-destructive hover:bg-destructive/90"
            disabled={deleteWordbook.isPending || deleteBundle.isPending}
          >
            {deleteWordbook.isPending || deleteBundle.isPending
              ? '삭제 중...'
              : '삭제'}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
}
