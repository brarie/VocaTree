'use client';

import { useState, useRef, useEffect } from 'react';
import { X } from 'lucide-react';
import { Input } from '@/components/ui/input';
import { cn } from '@/lib/utils';

interface MultiValueEditorProps {
  value: string[] | null;
  onChange: (value: string[] | null) => void;
  onBlur?: (value?: string[] | null) => void;
  placeholder?: string;
}

export function MultiValueEditor({
  value,
  onChange,
  onBlur,
  placeholder = '입력하세요...',
}: MultiValueEditorProps) {
  const [inputValue, setInputValue] = useState('');
  const [selectedIndex, setSelectedIndex] = useState<number | null>(null);
  const inputRef = useRef<HTMLInputElement>(null);
  const containerRef = useRef<HTMLDivElement>(null);

  const items = value || [];

  // Auto-focus input
  useEffect(() => {
    inputRef.current?.focus();
  }, []);

  const handleAdd = () => {
    const trimmed = inputValue.trim();
    if (!trimmed) return;

    // Prevent duplicates
    if (items.includes(trimmed)) {
      setInputValue('');
      return;
    }

    const newItems = [...items, trimmed];
    onChange(newItems.length > 0 ? newItems : null);
    setInputValue('');
    setSelectedIndex(null);
  };

  const handleRemove = (index: number) => {
    const newItems = items.filter((_, i) => i !== index);
    onChange(newItems.length > 0 ? newItems : null);
    setSelectedIndex(null);
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      if (selectedIndex !== null) {
        // Chip이 선택되어 있으면 삭제
        handleRemove(selectedIndex);
      } else {
        // Input에 포커스되어 있으면 추가
        handleAdd();
      }
    } else if (e.key === 'Escape') {
      e.preventDefault();
      setSelectedIndex(null);
      onBlur?.();
    } else if (e.key === 'ArrowUp') {
      e.preventDefault();
      if (items.length === 0) return;

      if (selectedIndex === null) {
        // 처음 위키 누르면 마지막 chip 선택
        setSelectedIndex(items.length - 1);
      } else if (selectedIndex > 0) {
        // 위로 이동
        setSelectedIndex(selectedIndex - 1);
      } else {
        // 첫 번째에서 위키 누르면 input으로
        setSelectedIndex(null);
        inputRef.current?.focus();
      }
    } else if (e.key === 'ArrowDown') {
      e.preventDefault();
      if (items.length === 0) return;

      if (selectedIndex === null) {
        // 처음 아래키 누르면 첫 번째 chip 선택
        setSelectedIndex(0);
      } else if (selectedIndex < items.length - 1) {
        // 아래로 이동
        setSelectedIndex(selectedIndex + 1);
      } else {
        // 마지막에서 아래키 누르면 input으로
        setSelectedIndex(null);
        inputRef.current?.focus();
      }
    } else if (e.key === 'Delete' && selectedIndex !== null) {
      e.preventDefault();
      handleRemove(selectedIndex);
    } else if (e.key === 'Backspace') {
      if (!inputValue && items.length > 0 && selectedIndex === null) {
        // Backspace on empty input: select last item
        e.preventDefault();
        setSelectedIndex(items.length - 1);
      } else if (selectedIndex !== null) {
        // Backspace on selected item: remove it
        e.preventDefault();
        handleRemove(selectedIndex);
      }
    }
  };

  return (
    <div
      ref={containerRef}
      className="min-h-[60px] bg-zinc-900 border border-zinc-700 rounded-md p-2 space-y-2"
    >
      {/* Chips */}
      {items.length > 0 && (
        <div className="flex flex-wrap gap-1">
          {items.map((item, index) => (
            <div
              key={index}
              className={cn(
                'inline-flex items-center gap-1 px-2 py-1 rounded text-xs transition-colors',
                selectedIndex === index
                  ? 'bg-blue-600 text-white ring-2 ring-blue-400'
                  : 'bg-zinc-800 text-white/90',
              )}
            >
              <span>{item}</span>
              <button
                type="button"
                onClick={(e) => {
                  e.preventDefault();
                  e.stopPropagation();
                  handleRemove(index);
                }}
                onMouseDown={(e) => {
                  e.preventDefault();
                  e.stopPropagation();
                }}
                className="hover:bg-zinc-700 rounded-sm p-0.5 transition-colors"
              >
                <X className="h-3 w-3" />
              </button>
            </div>
          ))}
        </div>
      )}

      {/* Input */}
      <Input
        ref={inputRef}
        value={inputValue}
        onChange={(e) => setInputValue(e.target.value)}
        onKeyDown={handleKeyDown}
        onFocus={() => setSelectedIndex(null)}
        onBlur={() => {
          // Add current input if not empty
          let finalValue = items;
          if (inputValue.trim()) {
            const trimmed = inputValue.trim();
            if (!items.includes(trimmed)) {
              finalValue = [...items, trimmed];
            }
          }
          // 최종 값을 전달해서 저장
          onBlur?.(finalValue.length > 0 ? finalValue : null);
        }}
        placeholder={placeholder}
        className="bg-zinc-950 border-zinc-700 text-white text-sm h-8"
      />
    </div>
  );
}
