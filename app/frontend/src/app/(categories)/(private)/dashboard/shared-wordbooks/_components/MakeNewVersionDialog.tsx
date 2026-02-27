'use client';

import { useState, useRef, useEffect } from 'react';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useMakeNewVersion } from '@/hooks/library/queries';
import { useSimpleWordbooks } from '@/hooks/wordbook/queries';
import {
  MakeNewVersionRequest,
  MakeNewVersionRequestSchema,
} from '@/types/api/library';
import { SimpleWordbookView } from '@/types/api/wordbook';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import {
  Form,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
  FormControl,
} from '@/components/ui/form';
import {
  Command,
  CommandInput,
  CommandList,
  CommandEmpty,
  CommandGroup,
  CommandItem,
} from '@/components/ui/command';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { ChevronsUpDown, Check } from 'lucide-react';
import { cn } from '@/lib/utils';

// ─────────────────────────────────────────────
// 단어장 선택 필드 (Command + inline dropdown)
// ─────────────────────────────────────────────

const prefixFilter = (value: string, search: string) =>
  value.toLowerCase().startsWith(search.toLowerCase()) ? 1 : 0;

interface WordbookSelectFieldProps {
  value: number;
  onChange: (v: number) => void;
  wordbooks: SimpleWordbookView[];
  error?: string;
}

function WordbookSelectField({
  value,
  onChange,
  wordbooks,
  error,
}: WordbookSelectFieldProps) {
  const [open, setOpen] = useState(false);
  const containerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!open) return;
    const handleClickOutside = (e: MouseEvent) => {
      if (
        containerRef.current &&
        !containerRef.current.contains(e.target as Node)
      ) {
        setOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, [open]);

  const selectedWb = wordbooks.find((w) => w.id === value);
  const selectedLabel = selectedWb
    ? `${selectedWb.title} #${selectedWb.id}`
    : null;

  return (
    <div ref={containerRef} className="relative">
      <button
        type="button"
        onClick={() => setOpen((prev) => !prev)}
        className={cn(
          'flex w-full items-center justify-between rounded-md border px-3 py-2 text-sm transition-colors',
          'bg-zinc-900 border-zinc-700 hover:border-zinc-500',
          selectedLabel ? 'text-white' : 'text-zinc-500',
        )}
      >
        <span className="truncate">{selectedLabel ?? '단어장 선택...'}</span>
        <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 text-zinc-500" />
      </button>
      {open && (
        <div className="absolute top-full left-0 right-0 z-50 mt-1 overflow-hidden rounded-md border border-zinc-700 bg-zinc-900 shadow-xl">
          <Command filter={prefixFilter}>
            <CommandInput
              placeholder="제목으로 검색..."
              className="text-white placeholder:text-zinc-500 border-zinc-700"
            />
            <CommandList>
              <CommandEmpty className="text-zinc-500 text-sm">
                검색 결과가 없습니다.
              </CommandEmpty>
              <CommandGroup>
                {wordbooks.map((wb) => (
                  <CommandItem
                    key={wb.id}
                    value={wb.title}
                    onSelect={() => {
                      onChange(wb.id);
                      setOpen(false);
                    }}
                    onMouseDown={(e) => e.preventDefault()}
                    className="text-white data-[selected=true]:bg-zinc-800 flex items-center justify-between cursor-pointer"
                  >
                    <div className="flex items-center gap-2 min-w-0">
                      <Check
                        className={cn(
                          'h-4 w-4 shrink-0',
                          value === wb.id ? 'opacity-100' : 'opacity-0',
                        )}
                      />
                      <span className="truncate">{wb.title}</span>
                    </div>
                    <span className="text-zinc-500 text-xs ml-2 shrink-0">
                      #{wb.id}
                    </span>
                  </CommandItem>
                ))}
              </CommandGroup>
            </CommandList>
          </Command>
        </div>
      )}
      {error && <p className="text-xs text-red-400 mt-1">{error}</p>}
    </div>
  );
}

// ─────────────────────────────────────────────
// Dialog
// ─────────────────────────────────────────────

interface MakeNewVersionDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  sharedWordbookId: number;
  currentVersion: number;
}

export function MakeNewVersionDialog({
  open,
  onOpenChange,
  sharedWordbookId,
  currentVersion,
}: MakeNewVersionDialogProps) {
  const makeVersion = useMakeNewVersion();
  const { data: simpleWordbooks = [] } = useSimpleWordbooks();

  const form = useForm<MakeNewVersionRequest>({
    resolver: zodResolver(MakeNewVersionRequestSchema),
    defaultValues: {
      myWordbookId: 0,
      versionNote: '',
    },
  });

  const onSubmit = (data: MakeNewVersionRequest) => {
    makeVersion.mutate(
      { id: sharedWordbookId, data },
      {
        onSuccess: () => {
          form.reset();
          onOpenChange(false);
        },
      },
    );
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="bg-zinc-950 border-zinc-800 text-white sm:max-w-md">
        <DialogHeader>
          <DialogTitle className="text-white">
            새 버전 업데이트{' '}
            <span className="text-zinc-500 font-normal text-sm">
              (현재 v{currentVersion} → v{currentVersion + 1})
            </span>
          </DialogTitle>
        </DialogHeader>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            {/* 단어장 선택 */}
            <Controller
              control={form.control}
              name="myWordbookId"
              render={({ field, fieldState }) => (
                <FormItem>
                  <FormLabel className="text-zinc-300">
                    스냅샷을 찍을 단어장
                  </FormLabel>
                  <WordbookSelectField
                    value={field.value}
                    onChange={field.onChange}
                    wordbooks={simpleWordbooks}
                    error={fieldState.error?.message}
                  />
                </FormItem>
              )}
            />

            {/* 버전 메모 */}
            <FormField
              control={form.control}
              name="versionNote"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="text-zinc-300">버전 메모</FormLabel>
                  <FormControl>
                    <Input
                      {...field}
                      placeholder={`v${currentVersion + 1} 변경 사항...`}
                      className="bg-zinc-900 border-zinc-700 text-white placeholder:text-zinc-500"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <div className="flex justify-end gap-2 pt-2">
              <Button
                type="button"
                variant="outline"
                onClick={() => onOpenChange(false)}
                className="bg-transparent border-zinc-700 text-zinc-300 hover:bg-zinc-800 hover:text-white"
              >
                취소
              </Button>
              <Button
                type="submit"
                disabled={makeVersion.isPending}
                className="bg-white text-zinc-900 hover:bg-zinc-200"
              >
                {makeVersion.isPending ? '업데이트 중...' : '버전 업데이트'}
              </Button>
            </div>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}
