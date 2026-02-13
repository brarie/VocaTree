'use client';

import { useState, useEffect, useCallback } from 'react';
import { useWord } from '@/hooks/dictionary/queries';
import { PublicWordSense, PublicWordExample } from '@/types/api/dictionary';
import { ColumnType, WordbookColumnResponse } from '@/types/api/wordbookWord';
import { getColumnLevel } from '@/lib/wordbookTableUtils';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Checkbox } from '@/components/ui/checkbox';
import { Badge } from '@/components/ui/badge';
import { Loader2, ChevronDown, ChevronRight } from 'lucide-react';
import { getPartOfSpeechAbbreviation } from '@/types/constants/partOfSpeech';

interface SelectedSenseData {
  sense: PublicWordSense;
  selectedExamples: PublicWordExample[];
}

interface DictionarySenseModalProps {
  isOpen: boolean;
  onClose: () => void;
  slug: string;
  columnsData: WordbookColumnResponse | undefined;
  onApply: (dictionaryWordId: number, selectedSenses: SelectedSenseData[]) => void;
}

export function DictionarySenseModal({
  isOpen,
  onClose,
  slug,
  columnsData,
  onApply,
}: DictionarySenseModalProps) {
  const { data: wordData, isLoading } = useWord(slug);
  const [selectedSenseIds, setSelectedSenseIds] = useState<Set<number>>(
    new Set(),
  );
  const [senseExamplesMap, setSenseExamplesMap] = useState<
    Map<number, PublicWordExample[]>
  >(new Map());
  const [expandedSenseIds, setExpandedSenseIds] = useState<Set<number>>(
    new Set(),
  );

  const hasActiveLevel3 = columnsData
    ? Object.entries(columnsData.columns).some(([key, setting]) => {
        const columnType = key as ColumnType;
        const level = getColumnLevel(columnType);
        return level === 3 && setting.isActive;
      })
    : false;

  useEffect(() => {
    if (!isOpen) {
      setSelectedSenseIds(new Set());
      setSenseExamplesMap(new Map());
      setExpandedSenseIds(new Set());
    }
  }, [isOpen]);

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (!isOpen) return;

      if ((e.ctrlKey || e.metaKey) && e.key === 'Enter') {
        e.preventDefault();
        if (selectedSenseIds.size > 0 && wordData) {
          const selectedSenses: SelectedSenseData[] = Array.from(
            selectedSenseIds,
          ).map((senseId) => {
            const sense = wordData.senses.find((s) => s.id === senseId);
            if (!sense) throw new Error('Sense not found');

            return {
              sense,
              selectedExamples: senseExamplesMap.get(senseId) || [],
            };
          });

          onApply(wordData.id, selectedSenses);
          onClose();
        }
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isOpen, selectedSenseIds, senseExamplesMap, wordData, onApply, onClose]);

  const handleSenseToggle = (senseId: number) => {
    setSelectedSenseIds((prev) => {
      const newSet = new Set(prev);
      if (newSet.has(senseId)) {
        newSet.delete(senseId);
        setSenseExamplesMap((map) => {
          const newMap = new Map(map);
          newMap.delete(senseId);
          return newMap;
        });
      } else {
        newSet.add(senseId);
      }
      return newSet;
    });
  };

  const toggleExpandSense = (senseId: number) => {
    setExpandedSenseIds((prev) => {
      const newSet = new Set(prev);
      if (newSet.has(senseId)) {
        newSet.delete(senseId);
      } else {
        newSet.add(senseId);
      }
      return newSet;
    });
  };

  const handleExampleToggle = (senseId: number, example: PublicWordExample) => {
    setSenseExamplesMap((prev) => {
      const newMap = new Map(prev);
      const current = newMap.get(senseId) || [];
      const exists = current.some((ex) => ex.id === example.id);

      if (exists) {
        newMap.set(
          senseId,
          current.filter((ex) => ex.id !== example.id),
        );
      } else {
        newMap.set(senseId, [...current, example]);
      }

      return newMap;
    });
  };

  const handleApply = useCallback(() => {
    if (!wordData) return;

    const selectedSenses: SelectedSenseData[] = Array.from(
      selectedSenseIds,
    ).map((senseId) => {
      const sense = wordData.senses.find((s) => s.id === senseId);
      if (!sense) throw new Error('Sense not found');

      return {
        sense,
        selectedExamples: senseExamplesMap.get(senseId) || [],
      };
    });

    onApply(wordData.id, selectedSenses);
    onClose();
  }, [wordData, selectedSenseIds, senseExamplesMap, onApply, onClose]);

  return (
    <>
      <Dialog open={isOpen} onOpenChange={onClose}>
        <DialogContent className="max-w-2xl max-h-[80vh] overflow-y-auto bg-zinc-900 text-white border-zinc-700">
          <DialogHeader>
            <DialogTitle className="text-xl">
              &quot;{slug}&quot; - 뜻과 예문 선택
            </DialogTitle>
          </DialogHeader>

          {isLoading ? (
            <div className="flex items-center justify-center p-8">
              <Loader2 className="h-6 w-6 animate-spin text-blue-400" />
            </div>
          ) : wordData ? (
            <div className="space-y-4">
              <div className="text-sm text-zinc-400">
                📖 원하는 뜻을 선택하세요 (복수 선택 가능)
              </div>

              <div className="space-y-3">
                {wordData.senses.map((sense) => {
                  const isExpanded = expandedSenseIds.has(sense.id);
                  const selectedExamples = senseExamplesMap.get(sense.id) || [];

                  return (
                    <div
                      key={sense.id}
                      className="border border-zinc-700 rounded-lg p-4 bg-zinc-800/50"
                    >
                      <div className="flex items-start gap-3">
                        <Checkbox
                          id={`sense-${sense.id}`}
                          checked={selectedSenseIds.has(sense.id)}
                          onCheckedChange={() => handleSenseToggle(sense.id)}
                          className="mt-1"
                        />
                        <div className="flex-1 space-y-2">
                          <label
                            htmlFor={`sense-${sense.id}`}
                            className="flex items-center gap-2 cursor-pointer"
                          >
                            <span className="font-medium text-white">
                              {sense.sense.definition}
                            </span>
                            {sense.sense.partOfSpeech && (
                              <Badge
                                variant="secondary"
                                className="text-xs bg-zinc-700"
                              >
                                {getPartOfSpeechAbbreviation(
                                  sense.sense.partOfSpeech,
                                )}
                              </Badge>
                            )}
                          </label>

                          {sense.sense.engDefinition && (
                            <div className="text-sm text-zinc-400">
                              • {sense.sense.engDefinition}
                            </div>
                          )}

                          {(sense.sense.britishPhonetic ||
                            sense.sense.americanPhonetic) && (
                            <div className="flex items-center gap-4 text-xs text-zinc-400">
                              {sense.sense.americanPhonetic && (
                                <div className="flex items-center gap-1">
                                  <span className="font-medium">미국</span>
                                  <span>[{sense.sense.americanPhonetic}]</span>
                                </div>
                              )}
                              {sense.sense.britishPhonetic && (
                                <div className="flex items-center gap-1">
                                  <span className="font-medium">영국</span>
                                  <span>[{sense.sense.britishPhonetic}]</span>
                                </div>
                              )}
                            </div>
                          )}

                          {(sense.sense.synonyms.length > 0 ||
                            sense.sense.antonyms.length > 0) && (
                            <div className="text-xs space-y-1">
                              {sense.sense.synonyms.length > 0 && (
                                <div className="text-zinc-400">
                                  <span className="text-zinc-500">유의어:</span>{' '}
                                  {sense.sense.synonyms.join(', ')}
                                </div>
                              )}
                              {sense.sense.antonyms.length > 0 && (
                                <div className="text-zinc-400">
                                  <span className="text-zinc-500">반의어:</span>{' '}
                                  {sense.sense.antonyms.join(', ')}
                                </div>
                              )}
                            </div>
                          )}

                          {hasActiveLevel3 &&
                            selectedSenseIds.has(sense.id) &&
                            sense.examples.length > 0 && (
                              <div className="mt-2">
                                <button
                                  onClick={() => toggleExpandSense(sense.id)}
                                  className="flex items-center gap-1 text-sm text-blue-400 hover:text-blue-300"
                                >
                                  {isExpanded ? (
                                    <ChevronDown className="h-4 w-4" />
                                  ) : (
                                    <ChevronRight className="h-4 w-4" />
                                  )}
                                  📝 예문 선택 ({sense.examples.length}개)
                                  {selectedExamples.length > 0 && (
                                    <span className="text-green-400">
                                      - {selectedExamples.length}개 선택됨
                                    </span>
                                  )}
                                </button>

                                {isExpanded && (
                                  <div className="mt-2 space-y-2 pl-5 border-l-2 border-zinc-700">
                                    {sense.examples.map((example) => (
                                      <div
                                        key={example.id}
                                        className="flex items-start gap-2 p-2 rounded bg-zinc-900/50"
                                      >
                                        <Checkbox
                                          id={`example-${example.id}`}
                                          checked={selectedExamples.some(
                                            (ex) => ex.id === example.id,
                                          )}
                                          onCheckedChange={() =>
                                            handleExampleToggle(
                                              sense.id,
                                              example,
                                            )
                                          }
                                          className="mt-1"
                                        />
                                        <label
                                          htmlFor={`example-${example.id}`}
                                          className="flex-1 cursor-pointer text-sm"
                                        >
                                          <div className="text-white">
                                            {example.example.sentence}
                                          </div>
                                          {example.example.translation && (
                                            <div className="text-zinc-400 text-xs mt-0.5">
                                              → {example.example.translation}
                                            </div>
                                          )}
                                        </label>
                                      </div>
                                    ))}
                                  </div>
                                )}
                              </div>
                            )}
                        </div>
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          ) : (
            <div className="text-center text-zinc-400 p-8">
              단어를 찾을 수 없습니다.
            </div>
          )}

          <DialogFooter>
            <Button variant="ghost" onClick={onClose}>
              취소
            </Button>
            <Button
              onClick={handleApply}
              disabled={selectedSenseIds.size === 0}
            >
              적용하기 (Ctrl+Enter)
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  );
}
