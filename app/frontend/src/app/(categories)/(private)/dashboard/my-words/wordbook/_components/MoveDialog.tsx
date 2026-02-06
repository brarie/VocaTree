'use client';

import { useState } from 'react';
import {
  useAllBundles,
  useMoveWordbookToRoot,
  useMoveWordbookToBundle,
} from '@/hooks/wordbook/queries';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Folder } from 'lucide-react';

interface MoveDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  wordbookId: number;
  currentTitle: string;
}

export function MoveDialog({
  open,
  onOpenChange,
  wordbookId,
  currentTitle,
}: MoveDialogProps) {
  const { data: bundles } = useAllBundles();
  const moveToRoot = useMoveWordbookToRoot();
  const moveToBundle = useMoveWordbookToBundle();

  const [selectedBundleId, setSelectedBundleId] = useState<string>('root');

  const handleMove = () => {
    if (selectedBundleId === 'root') {
      moveToRoot.mutate(wordbookId, {
        onSuccess: () => {
          onOpenChange(false);
          setSelectedBundleId('root');
        },
      });
    } else {
      moveToBundle.mutate(
        { wordbookId, bundleId: parseInt(selectedBundleId) },
        {
          onSuccess: () => {
            onOpenChange(false);
            setSelectedBundleId('root');
          },
        },
      );
    }
  };

  const handleOpenChange = (newOpen: boolean) => {
    if (!newOpen) {
      setSelectedBundleId('root');
    }
    onOpenChange(newOpen);
  };

  return (
    <Dialog open={open} onOpenChange={handleOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>단어장 이동</DialogTitle>
          <DialogDescription>
            <span className="font-semibold">{currentTitle}</span> 단어장을
            이동할 위치를 선택하세요.
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-4">
          <div>
            <label className="text-sm font-medium mb-2 block">
              이동할 위치
            </label>
            <Select value={selectedBundleId} onValueChange={setSelectedBundleId}>
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="root">
                  <div className="flex items-center gap-2">
                    <Folder className="w-4 h-4" />
                    루트 (최상위)
                  </div>
                </SelectItem>
                {bundles?.map((bundle) => (
                  <SelectItem key={bundle.id} value={String(bundle.id)}>
                    <div className="flex items-center gap-2">
                      <Folder className="w-4 h-4" />
                      {bundle.title}
                    </div>
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
        </div>

        <DialogFooter>
          <Button
            type="button"
            variant="outline"
            onClick={() => handleOpenChange(false)}
          >
            취소
          </Button>
          <Button
            onClick={handleMove}
            disabled={moveToRoot.isPending || moveToBundle.isPending}
          >
            {moveToRoot.isPending || moveToBundle.isPending
              ? '이동 중...'
              : '이동'}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
