'use client';

import { useState, useEffect } from 'react';
import { PublicWordSense, PublicWordExample } from '@/types/api/dictionary';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Checkbox } from '@/components/ui/checkbox';
import { ArrowLeft } from 'lucide-react';

interface ExampleSelectorDialogProps {
  isOpen: boolean;
  onClose: () => void;
  sense: PublicWordSense;
  selectedExamples: PublicWordExample[];
  onApply: (examples: PublicWordExample[]) => void;
}

export function ExampleSelectorDialog({
  isOpen,
  onClose,
  sense,
  selectedExamples,
  onApply,
}: ExampleSelectorDialogProps) {
  const [selectedIds, setSelectedIds] = useState<Set<number>>(new Set());

  useEffect(() => {
    setSelectedIds(new Set(selectedExamples.map((ex) => ex.id)));
  }, [selectedExamples]);

  const handleToggle = (exampleId: number) => {
    setSelectedIds((prev) => {
      const newSet = new Set(prev);
      if (newSet.has(exampleId)) {
        newSet.delete(exampleId);
      } else {
        newSet.add(exampleId);
      }
      return newSet;
    });
  };

  const handleApply = () => {
    const selected = sense.examples.filter((ex) => selectedIds.has(ex.id));
    onApply(selected);
  };

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="max-w-2xl max-h-[80vh] overflow-y-auto bg-zinc-900 text-white border-zinc-700">
        <DialogHeader>
          <DialogTitle className="text-xl flex items-center gap-2">
            <Button
              variant="ghost"
              size="sm"
              onClick={onClose}
              className="p-1 h-auto"
            >
              <ArrowLeft className="h-4 w-4" />
            </Button>
            &quot;{sense.sense.definition}&quot;의 예문 선택
          </DialogTitle>
        </DialogHeader>

        <div className="space-y-3">
          {sense.examples.map((example) => (
            <div
              key={example.id}
              className="border border-zinc-700 rounded-lg p-4 bg-zinc-800/50"
            >
              <div className="flex items-start gap-3">
                <Checkbox
                  id={`example-${example.id}`}
                  checked={selectedIds.has(example.id)}
                  onCheckedChange={() => handleToggle(example.id)}
                  className="mt-1"
                />
                <div className="flex-1 space-y-1">
                  <label
                    htmlFor={`example-${example.id}`}
                    className="block text-white cursor-pointer"
                  >
                    {example.example.sentence}
                  </label>
                  {example.example.translation && (
                    <div className="text-sm text-zinc-400">
                      → {example.example.translation}
                    </div>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>

        <DialogFooter>
          <Button onClick={handleApply} className="bg-blue-600 hover:bg-blue-700">
            확인
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
