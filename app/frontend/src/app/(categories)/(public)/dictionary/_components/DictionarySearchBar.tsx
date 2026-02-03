'use client';

import { useState, useRef, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { Search, Clock, X } from 'lucide-react';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import {
  useAutocomplete,
  useSearchHistory,
  useDeleteSearchHistory,
  useDeleteAllSearchHistory,
} from '@/hooks/dictionary/queries';
import { useAuth } from '@/contexts/AuthContext';
import { useDebounce } from 'use-debounce';

interface DictionarySearchBarProps {
  initialValue?: string;
  showHistory?: boolean;
  onSearch?: (query: string) => void;
}

export function DictionarySearchBar({
  initialValue = '',
  showHistory = false,
  onSearch,
}: DictionarySearchBarProps) {
  const router = useRouter();
  const { isAuthenticated } = useAuth();
  const [inputValue, setInputValue] = useState(initialValue);
  const [debouncedInput] = useDebounce(inputValue, 300);
  const [showAutocomplete, setShowAutocomplete] = useState(false);
  const [isFocused, setIsFocused] = useState(false);
  const [selectedIndex, setSelectedIndex] = useState(-1);
  const inputRef = useRef<HTMLInputElement>(null);

  const { data: autocompleteResults } = useAutocomplete(debouncedInput, 10);
  const { data: searchHistory } = useSearchHistory();
  const deleteHistoryMutation = useDeleteSearchHistory();
  const deleteAllMutation = useDeleteAllSearchHistory();

  const shouldShowHistory =
    showHistory &&
    isFocused &&
    !inputValue &&
    isAuthenticated &&
    searchHistory &&
    searchHistory.length > 0;

  const shouldShowResults =
    isFocused &&
    inputValue &&
    autocompleteResults &&
    autocompleteResults.length > 0;

  const historyItems = shouldShowHistory
    ? searchHistory.slice(0, 5).map((item) => ({
        type: 'history' as const,
        id: item.id,
        spelling: item.spelling,
      }))
    : [];

  const resultItems = shouldShowResults
    ? autocompleteResults.map((word) => ({
        type: 'result' as const,
        id: word.id,
        spelling: word.spelling,
        slug: word.slug,
        topDefinitions: word.topDefinitions,
      }))
    : [];

  const allItems = shouldShowHistory ? historyItems : resultItems;

  useEffect(() => {
    if (!showAutocomplete) {
      setSelectedIndex(-1);
    }
  }, [showAutocomplete]);

  useEffect(() => {
    setInputValue(initialValue);
  }, [initialValue]);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (inputValue.trim()) {
      if (onSearch) {
        onSearch(inputValue);
      } else {
        router.push(
          `/dictionary/search?query=${encodeURIComponent(inputValue)}`,
        );
      }
      setShowAutocomplete(false);
    }
  };

  const handleWordSelect = (slug: string) => {
    router.push(`/dictionary/${slug}`);
    setShowAutocomplete(false);
  };

  const handleHistorySelect = (spelling: string) => {
    setInputValue(spelling);
    if (onSearch) {
      onSearch(spelling);
    } else {
      router.push(`/dictionary/search?query=${encodeURIComponent(spelling)}`);
    }
    setShowAutocomplete(false);
  };

  const handleDeleteHistory = (e: React.MouseEvent, historyId: number) => {
    e.stopPropagation();
    e.preventDefault();
    deleteHistoryMutation.mutate(historyId);
  };

  const handleDeleteAllHistory = (e: React.MouseEvent) => {
    e.stopPropagation();
    e.preventDefault();
    deleteAllMutation.mutate();
    setShowAutocomplete(false);
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (!showAutocomplete || allItems.length === 0) return;

    switch (e.key) {
      case 'ArrowDown':
        e.preventDefault();
        setSelectedIndex((prev) =>
          prev < allItems.length - 1 ? prev + 1 : prev,
        );
        break;
      case 'ArrowUp':
        e.preventDefault();
        setSelectedIndex((prev) => (prev > 0 ? prev - 1 : -1));
        break;
      case 'Enter':
        if (selectedIndex >= 0) {
          e.preventDefault();
          const selected = allItems[selectedIndex];
          if (selected.type === 'history') {
            handleHistorySelect(selected.spelling);
          } else {
            handleWordSelect(selected.slug);
          }
        }
        break;
      case 'Escape':
        setShowAutocomplete(false);
        inputRef.current?.blur();
        break;
    }
  };

  return (
    <form onSubmit={handleSearch} className="relative">
      <div className="flex gap-2">
        <div className="flex-1 relative">
          <Input
            ref={inputRef}
            type="text"
            value={inputValue}
            onChange={(e) => {
              setInputValue(e.target.value);
            }}
            onKeyDown={handleKeyDown}
            onFocus={() => {
              setIsFocused(true);
              setShowAutocomplete(true);
              if (inputRef.current && inputRef.current.value !== inputValue) {
                setInputValue(inputRef.current.value);
              }
            }}
            onBlur={() => {
              setTimeout(() => {
                setIsFocused(false);
                setShowAutocomplete(false);
              }, 50);
            }}
            placeholder="단어를 입력하세요 (예: exist, minute)"
            className="w-full bg-zinc-900 border-zinc-800 text-white placeholder:text-gray-500 h-12"
          />
          {showAutocomplete && (shouldShowHistory || shouldShowResults) && (
            <div className="absolute top-full left-0 right-0 mt-1 bg-zinc-900 border border-zinc-800 rounded-md shadow-lg z-10 max-h-96 overflow-y-auto">
              {shouldShowHistory && (
                <>
                  <div className="px-4 py-2 text-xs text-gray-500 font-semibold flex items-center justify-between border-b border-zinc-800">
                    <div className="flex items-center gap-2">
                      <Clock className="h-3 w-3" />
                      최근 검색
                    </div>
                    <button
                      type="button"
                      onMouseDown={(e) => e.preventDefault()}
                      onClick={handleDeleteAllHistory}
                      className="text-xs text-gray-400 hover:text-white transition-colors"
                    >
                      모두 지우기
                    </button>
                  </div>
                  {historyItems.map((item, idx) => (
                    <div
                      key={item.id}
                      className={`flex items-center justify-between px-4 py-3 transition-colors border-b border-zinc-800 last:border-b-0 ${
                        selectedIndex === idx
                          ? 'bg-zinc-700'
                          : 'hover:bg-zinc-800'
                      }`}
                    >
                      <button
                        type="button"
                        onMouseDown={(e) => e.preventDefault()}
                        onClick={() => handleHistorySelect(item.spelling)}
                        className="flex-1 text-left font-medium text-white flex items-center gap-2"
                      >
                        <Clock className="h-4 w-4 text-gray-500" />
                        {item.spelling}
                      </button>
                      <button
                        type="button"
                        onMouseDown={(e) => e.preventDefault()}
                        onClick={(e) => handleDeleteHistory(e, item.id)}
                        className="p-1 hover:bg-zinc-700 rounded transition-colors"
                      >
                        <X className="h-4 w-4 text-gray-500 hover:text-white" />
                      </button>
                    </div>
                  ))}
                </>
              )}
              {shouldShowResults && (
                <>
                  {shouldShowHistory && (
                    <div className="px-4 py-2 text-xs text-gray-500 font-semibold flex items-center gap-2 border-b border-zinc-800">
                      <Search className="h-3 w-3" />
                      검색 결과
                    </div>
                  )}
                  {resultItems.map((word, idx) => (
                    <button
                      key={word.id}
                      type="button"
                      onMouseDown={(e) => e.preventDefault()}
                      onClick={() => handleWordSelect(word.slug)}
                      className={`w-full text-left px-4 py-3 transition-colors border-b border-zinc-800 last:border-b-0 ${
                        selectedIndex === idx
                          ? 'bg-zinc-700'
                          : 'hover:bg-zinc-800'
                      }`}
                    >
                      <div className="font-medium text-white">
                        {word.spelling}
                      </div>
                      {word.topDefinitions.length > 0 && (
                        <div className="text-sm text-gray-400 mt-1">
                          {word.topDefinitions.join(', ')}
                        </div>
                      )}
                    </button>
                  ))}
                </>
              )}
            </div>
          )}
        </div>
        <Button
          type="submit"
          className="h-12 w-12 px-6 bg-white text-black hover:bg-gray-200"
        >
          <Search className="size-5" />
        </Button>
      </div>
    </form>
  );
}
