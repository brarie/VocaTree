'use client';

import { useState, useEffect, useRef, useCallback } from 'react';
import { createPortal } from 'react-dom';
import { useDebounce } from 'use-debounce';
import { useAutocomplete } from '@/hooks/dictionary/queries';
import { PublicWordSimpleView } from '@/types/api/dictionary';
import { Loader2 } from 'lucide-react';

interface AutocompleteInputProps {
  value: string;
  onChange: (value: string) => void;
  onSelectWord: (word: PublicWordSimpleView) => void;
  onSave: () => void;
  onCancel: () => void;
  inputRef: React.RefObject<HTMLInputElement>;
}

export function AutocompleteInput({
  value,
  onChange,
  onSelectWord,
  onSave,
  onCancel,
  inputRef,
}: AutocompleteInputProps) {
  const [isOpen, setIsOpen] = useState(false);
  const [selectedIndex, setSelectedIndex] = useState(0);
  const [dropdownPosition, setDropdownPosition] = useState({
    top: 0,
    left: 0,
    width: 0,
    openUpward: false
  });
  const [debouncedQuery] = useDebounce(value, 300);
  const isSelectingWord = useRef(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  const { data: suggestions = [], isLoading } = useAutocomplete(
    debouncedQuery,
    10,
  );

  useEffect(() => {
    setIsOpen(suggestions.length > 0 && !!debouncedQuery);
    setSelectedIndex(-1);
  }, [suggestions, debouncedQuery]);

  const updateDropdownPosition = useCallback(() => {
    if (!inputRef.current) return;

    const rect = inputRef.current.getBoundingClientRect();
    const maxDropdownHeight = 240;
    const spaceBelow = window.innerHeight - rect.bottom;
    const spaceAbove = rect.top;

    // 아래 공간이 충분하면 아래로, 아니면 위로
    const openUpward = spaceBelow < 200 && spaceAbove > spaceBelow;

    if (openUpward) {
      // 위로 열 때: input 바로 위에 붙이기
      // dropdownRef가 있으면 실제 높이 사용, 없으면 예상 높이 사용
      const dropdownHeight = dropdownRef.current?.offsetHeight || 200;
      setDropdownPosition({
        top: rect.top - dropdownHeight - 4,
        left: rect.left,
        width: rect.width,
        openUpward,
      });
    } else {
      // 아래로 열 때
      setDropdownPosition({
        top: rect.bottom + 4,
        left: rect.left,
        width: rect.width,
        openUpward,
      });
    }
  }, [inputRef]);

  useEffect(() => {
    if (isOpen) {
      updateDropdownPosition();

      // 드롭다운이 렌더링된 후 정확한 높이로 재계산
      const timeoutId = setTimeout(() => {
        updateDropdownPosition();
      }, 10);

      const handleScroll = () => {
        updateDropdownPosition();
      };

      const handleResize = () => {
        updateDropdownPosition();
      };

      window.addEventListener('scroll', handleScroll, true);
      window.addEventListener('resize', handleResize);

      return () => {
        clearTimeout(timeoutId);
        window.removeEventListener('scroll', handleScroll, true);
        window.removeEventListener('resize', handleResize);
      };
    }
  }, [isOpen, updateDropdownPosition]);

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Escape') {
      e.preventDefault();
      setIsOpen(false);
      onCancel();
      return;
    }

    if (e.key === 'Enter') {
      e.preventDefault();
      if (isOpen && suggestions.length > 0) {
        if (selectedIndex === -1) {
          // "자동완성 안함" 선택
          setIsOpen(false);
          onSave();
        } else {
          isSelectingWord.current = true;
          onSelectWord(suggestions[selectedIndex]);
          setIsOpen(false);
        }
      } else {
        onSave();
      }
      return;
    }

    if (e.key === 'ArrowDown') {
      e.preventDefault();
      setSelectedIndex((prev) =>
        prev < suggestions.length - 1 ? prev + 1 : prev,
      );
      return;
    }

    if (e.key === 'ArrowUp') {
      e.preventDefault();
      setSelectedIndex((prev) => (prev > -1 ? prev - 1 : -1));
      return;
    }
  };

  return (
    <>
      <input
        ref={inputRef}
        type="text"
        value={value || ''}
        onChange={(e) => onChange(e.target.value)}
        onKeyDown={handleKeyDown}
        onBlur={() => {
          setTimeout(() => {
            if (isOpen) {
              setIsOpen(false);
            }
            if (!isSelectingWord.current) {
              onSave();
            }
            isSelectingWord.current = false;
          }, 200);
        }}
        className="w-full bg-zinc-900 border border-zinc-700 rounded px-2 py-1 text-white text-sm outline-none focus:border-blue-500"
        placeholder="단어 입력..."
        autoComplete="off"
      />

      {isOpen &&
        typeof window !== 'undefined' &&
        createPortal(
          <div
            ref={dropdownRef}
            className="fixed bg-zinc-900 border border-zinc-700 rounded shadow-lg z-[9999] max-h-60 overflow-auto"
            style={{
              top: `${dropdownPosition.top}px`,
              left: `${dropdownPosition.left}px`,
              width: `${dropdownPosition.width}px`,
            }}
          >
            {isLoading ? (
              <div className="flex items-center justify-center p-4">
                <Loader2 className="h-4 w-4 animate-spin text-blue-400" />
              </div>
            ) : (
              <div>
                <div
                  onMouseDown={(e) => {
                    e.preventDefault();
                    e.stopPropagation();
                    setIsOpen(false);
                    onSave();
                  }}
                  onMouseEnter={() => setSelectedIndex(-1)}
                  className={`px-2 py-1.5 cursor-pointer transition-colors border-b border-zinc-700 ${
                    selectedIndex === -1
                      ? 'bg-blue-500/20 text-white'
                      : 'text-zinc-400 hover:bg-zinc-800'
                  }`}
                >
                  <div className="text-xs">자동완성 안함</div>
                </div>
                {suggestions.map((word, index) => (
                  <div
                    key={word.id}
                    onMouseDown={(e) => {
                      e.preventDefault();
                      e.stopPropagation();
                      isSelectingWord.current = true;
                      onSelectWord(word);
                      setIsOpen(false);
                    }}
                    onMouseEnter={() => setSelectedIndex(index)}
                    className={`px-2 py-1.5 cursor-pointer transition-colors ${
                      index === selectedIndex
                        ? 'bg-blue-500/20 text-white'
                        : 'text-zinc-300 hover:bg-zinc-800'
                    }`}
                  >
                    <div className="text-sm">{word.spelling}</div>
                    {word.topDefinitions.length > 0 && (
                      <div className="text-xs text-zinc-400 mt-0.5 truncate">
                        {word.topDefinitions[0]}
                      </div>
                    )}
                  </div>
                ))}
              </div>
            )}
          </div>,
          document.body,
        )}
    </>
  );
}
