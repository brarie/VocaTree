'use client';

import { useState, useEffect, useRef } from 'react';
import { Check, ChevronsUpDown } from 'lucide-react';
import { cn } from '@/lib/utils';
import { Button } from '@/components/ui/button';
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from '@/components/ui/command';
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from '@/components/ui/popover';
import { PART_OF_SPEECH_OPTIONS } from '@/types/constants/partOfSpeech';

interface PartOfSpeechComboboxProps {
  value: string | null;
  onChange: (value: string | null) => void;
  onBlur?: (value?: string | null) => void;
  onCancel?: () => void;
}

export function PartOfSpeechCombobox({
  value,
  onChange,
  onBlur,
  onCancel,
}: PartOfSpeechComboboxProps) {
  const [open, setOpen] = useState(true); // 자동으로 열기
  const [search, setSearch] = useState('');
  const inputRef = useRef<HTMLInputElement>(null);

  const selectedOption = PART_OF_SPEECH_OPTIONS.find(
    (option) => option.value === value,
  );

  // 검색어에 맞는 옵션들 필터링
  const filteredOptions = PART_OF_SPEECH_OPTIONS.filter((option) =>
    option.label.toLowerCase().includes(search.toLowerCase()),
  );

  // 컴포넌트 마운트 시 포커스
  useEffect(() => {
    if (open) {
      // Popover가 열린 후 input에 포커스
      setTimeout(() => {
        const input = document.querySelector(
          '[cmdk-input]',
        ) as HTMLInputElement;
        input?.focus();
      }, 0);
    }
  }, [open]);

  const handleSelect = (selectedValue: string) => {
    const newValue = selectedValue === value ? null : selectedValue;
    onChange(newValue);
    setOpen(false);

    // 값을 직접 전달해서 즉시 저장
    onBlur?.(newValue);
  };

  const handleCancel = () => {
    setOpen(false);
    onCancel?.();
  };

  return (
    <Popover open={open} onOpenChange={setOpen}>
      <PopoverTrigger asChild>
        <Button
          variant="outline"
          role="combobox"
          aria-expanded={open}
          className="w-full justify-between bg-zinc-900 border-zinc-700 text-white text-sm h-8"
        >
          {selectedOption ? selectedOption.label : '품사 선택...'}
          <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
        </Button>
      </PopoverTrigger>
      <PopoverContent
        className="w-[300px] p-0 bg-zinc-900 border-zinc-700"
        onEscapeKeyDown={handleCancel}
      >
        <Command className="bg-zinc-900" shouldFilter={false}>
          <CommandInput
            ref={inputRef}
            placeholder="품사 검색..."
            className="h-9 bg-zinc-900 text-white"
            value={search}
            onValueChange={setSearch}
            onKeyDown={(e) => {
              if (e.key === 'Escape') {
                e.preventDefault();
                handleCancel();
              }
              // Enter는 Command 컴포넌트가 자동으로 처리 (위/아래 키로 선택된 항목)
            }}
          />
          <CommandList>
            <CommandEmpty className="text-white/60 text-sm py-6 text-center">
              검색 결과가 없습니다.
            </CommandEmpty>
            <CommandGroup>
              {filteredOptions.map((option) => (
                <CommandItem
                  key={option.value}
                  value={option.label}
                  onSelect={() => handleSelect(option.value)}
                  className="text-white cursor-pointer hover:bg-zinc-800"
                >
                  <Check
                    className={cn(
                      'mr-2 h-4 w-4',
                      value === option.value ? 'opacity-100' : 'opacity-0',
                    )}
                  />
                  {option.label}
                </CommandItem>
              ))}
            </CommandGroup>
          </CommandList>
        </Command>
      </PopoverContent>
    </Popover>
  );
}
