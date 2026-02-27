'use client';

import { useState, useEffect, useRef, useMemo } from 'react';
import { SharedWordSnapshotResponse } from '@/types/api/library';
import {
  flattenSharedWordsToRows,
  SharedFlattenedRow,
} from '@/lib/sharedWordbookUtils';
import { getColumnHeader, getColumnLevel } from '@/lib/wordbookTableUtils';
import { ColumnType } from '@/types/api/wordbookWord';
import { Button } from '@/components/ui/button';
import { Settings2, Check } from 'lucide-react';
import { cn } from '@/lib/utils';
import { getPartOfSpeechAbbreviation } from '@/types/constants/partOfSpeech';

// ==========================================
// Column visibility — persisted in localStorage
// ==========================================

const COLUMN_VISIBILITY_KEY = 'lib-shared-wb-cols';

const ALL_COLUMNS: ColumnType[] = [
  ColumnType.SPELLING,
  ColumnType.DEFINITION,
  ColumnType.ENG_DEFINITION,
  ColumnType.PART_OF_SPEECH,
  ColumnType.US_PHONETIC,
  ColumnType.UK_PHONETIC,
  ColumnType.SYNONYMS,
  ColumnType.ANTONYMS,
  ColumnType.SENTENCE,
  ColumnType.TRANSLATION,
];

const DEFAULT_VISIBILITY: Record<ColumnType, boolean> = {
  [ColumnType.SPELLING]: true,
  [ColumnType.DEFINITION]: true,
  [ColumnType.ENG_DEFINITION]: true,
  [ColumnType.PART_OF_SPEECH]: false,
  [ColumnType.US_PHONETIC]: false,
  [ColumnType.UK_PHONETIC]: false,
  [ColumnType.SYNONYMS]: false,
  [ColumnType.ANTONYMS]: false,
  [ColumnType.SENTENCE]: true,
  [ColumnType.TRANSLATION]: true,
};

function loadVisibility(): Record<ColumnType, boolean> {
  try {
    const stored = localStorage.getItem(COLUMN_VISIBILITY_KEY);
    if (stored) return { ...DEFAULT_VISIBILITY, ...JSON.parse(stored) };
  } catch {
    // ignore
  }
  return { ...DEFAULT_VISIBILITY };
}

function saveVisibility(v: Record<ColumnType, boolean>) {
  try {
    localStorage.setItem(COLUMN_VISIBILITY_KEY, JSON.stringify(v));
  } catch {
    // ignore
  }
}

// ==========================================
// Cell helpers
// ==========================================

function isCellRendered(row: SharedFlattenedRow, col: ColumnType): boolean {
  const level = getColumnLevel(col);
  if (level === 1) return row.isFirstRowOfWord;
  if (level === 2) return row.isFirstRowOfSense && row.senseIndex !== null;
  return row.exampleIndex !== null;
}

function getCellContent(
  row: SharedFlattenedRow,
  col: ColumnType,
): React.ReactNode {
  switch (col) {
    case ColumnType.SPELLING:
      return row.spelling ?? '';
    case ColumnType.DEFINITION:
      return row.definition ?? '';
    case ColumnType.ENG_DEFINITION:
      return row.engDefinition ?? '';
    case ColumnType.PART_OF_SPEECH: {
      const abbr = getPartOfSpeechAbbreviation(row.partOfSpeech);
      if (!abbr) return null;
      return (
        <span className="px-2 py-0.5 bg-zinc-700/50 border border-zinc-600/30 rounded text-xs text-zinc-300">
          {abbr}
        </span>
      );
    }
    case ColumnType.US_PHONETIC:
      return row.americanPhonetic ? `[${row.americanPhonetic}]` : '';
    case ColumnType.UK_PHONETIC:
      return row.britishPhonetic ? `[${row.britishPhonetic}]` : '';
    case ColumnType.SYNONYMS:
      if (row.synonyms.length === 0) return null;
      return (
        <div className="flex flex-wrap gap-1">
          {row.synonyms.map((item, i) => (
            <span
              key={i}
              className="px-2 py-0.5 bg-zinc-800 rounded text-xs text-white/90"
            >
              {item}
            </span>
          ))}
        </div>
      );
    case ColumnType.ANTONYMS:
      if (row.antonyms.length === 0) return null;
      return (
        <div className="flex flex-wrap gap-1">
          {row.antonyms.map((item, i) => (
            <span
              key={i}
              className="px-2 py-0.5 bg-zinc-800 rounded text-xs text-white/90"
            >
              {item}
            </span>
          ))}
        </div>
      );
    case ColumnType.SENTENCE:
      return row.sentence ?? '';
    case ColumnType.TRANSLATION:
      return row.translation ?? '';
    default:
      return '';
  }
}

// ==========================================
// Component
// ==========================================

interface SharedWordbookWordsTableProps {
  words: SharedWordSnapshotResponse[];
}

export function SharedWordbookWordsTable({
  words,
}: SharedWordbookWordsTableProps) {
  const [visibility, setVisibility] = useState<Record<ColumnType, boolean>>(
    () => (typeof window !== 'undefined' ? loadVisibility() : DEFAULT_VISIBILITY),
  );
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  // Close dropdown on outside click
  useEffect(() => {
    if (!dropdownOpen) return;
    const handler = (e: MouseEvent) => {
      if (!dropdownRef.current?.contains(e.target as Node)) {
        setDropdownOpen(false);
      }
    };
    document.addEventListener('mousedown', handler);
    return () => document.removeEventListener('mousedown', handler);
  }, [dropdownOpen]);

  const activeColumns = useMemo(
    () => ALL_COLUMNS.filter((col) => visibility[col]),
    [visibility],
  );

  const rows = useMemo(() => flattenSharedWordsToRows(words), [words]);

  // Effective rowspans: recompute based on which rows actually render.
  // Pre-computed wordRowspan/senseRowspan assume all sub-rows exist, but
  // toggling columns skips rows (hasVisible=false), so the naive spans
  // overflow into the next word/sense.
  const { wordEffectiveRowspan, senseEffectiveRowspan } = useMemo(() => {
    const visibleRows = rows.filter((row) =>
      activeColumns.some((col) => isCellRendered(row, col)),
    );
    const wordMap = new Map<number, number>();
    const senseMap = new Map<string, number>();
    for (const row of visibleRows) {
      wordMap.set(row.wordId, (wordMap.get(row.wordId) ?? 0) + 1);
      if (row.senseIndex !== null) {
        const key = `${row.wordId}-${row.senseIndex}`;
        senseMap.set(key, (senseMap.get(key) ?? 0) + 1);
      }
    }
    return { wordEffectiveRowspan: wordMap, senseEffectiveRowspan: senseMap };
  }, [rows, activeColumns]);

  const toggleColumn = (col: ColumnType) => {
    if (col === ColumnType.SPELLING) return;
    const next = { ...visibility, [col]: !visibility[col] };
    setVisibility(next);
    saveVisibility(next);
  };

  if (words.length === 0) {
    return (
      <div className="flex items-center justify-center py-16 bg-white/5 rounded-lg border border-white/10">
        <p className="text-sm text-gray-400">단어가 없습니다.</p>
      </div>
    );
  }

  return (
    <div className="space-y-3">
      {/* Toolbar */}
      <div className="flex items-center justify-between">
        <p className="text-xs text-zinc-500">{words.length}개 단어</p>
        <div ref={dropdownRef} className="relative">
          <Button
            variant="outline"
            size="sm"
            onClick={() => setDropdownOpen((prev) => !prev)}
            className="bg-white/5 border-white/10 text-white hover:bg-white/10"
          >
            <Settings2 className="w-4 h-4 mr-2" />
            칼럼 관리
          </Button>

          {dropdownOpen && (
            <div className="absolute right-0 top-full mt-1 z-50 w-44 bg-zinc-900 border border-zinc-700 rounded-lg shadow-xl p-1.5">
              <p className="text-xs text-zinc-500 px-2 py-1 mb-0.5">
                표시할 칼럼 선택
              </p>
              {ALL_COLUMNS.map((col) => {
                const isSpelling = col === ColumnType.SPELLING;
                return (
                  <button
                    key={col}
                    onClick={() => toggleColumn(col)}
                    disabled={isSpelling}
                    className={cn(
                      'flex items-center gap-2 w-full px-2 py-1.5 rounded text-sm text-left transition-colors',
                      isSpelling
                        ? 'text-zinc-600 cursor-not-allowed'
                        : 'hover:bg-zinc-800 text-gray-300',
                    )}
                  >
                    <span
                      className={cn(
                        'w-3.5 h-3.5 rounded border flex items-center justify-center shrink-0',
                        visibility[col]
                          ? 'bg-white border-white'
                          : 'border-zinc-600',
                      )}
                    >
                      {visibility[col] && (
                        <Check className="w-2.5 h-2.5 text-zinc-900" />
                      )}
                    </span>
                    {getColumnHeader(col)}
                  </button>
                );
              })}
            </div>
          )}
        </div>
      </div>

      {/* Table */}
      <div className="border border-zinc-800 rounded-lg overflow-x-auto bg-zinc-950">
        <table
          className="border-collapse"
          style={{ minWidth: '100%', width: 'max-content' }}
        >
          <thead>
            <tr className="border-b border-zinc-800">
              {activeColumns.map((col) => (
                <th
                  key={col}
                  className="px-3 py-2 text-left text-xs font-semibold bg-zinc-800 text-zinc-300 border-r border-zinc-700 last:border-r-0 whitespace-nowrap"
                >
                  {getColumnHeader(col)}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {rows.map((row) => {
              const hasVisible = activeColumns.some((col) =>
                isCellRendered(row, col),
              );
              if (!hasVisible) return null;

              return (
                <tr
                  key={row.rowId}
                  className="border-b border-zinc-800 last:border-b-0"
                >
                  {activeColumns.map((col) => {
                    if (!isCellRendered(row, col)) return null;
                    const level = getColumnLevel(col);
                    const rowspan =
                      level === 1
                        ? (wordEffectiveRowspan.get(row.wordId) ?? 1)
                        : level === 2
                          ? (senseEffectiveRowspan.get(
                              `${row.wordId}-${row.senseIndex}`,
                            ) ?? 1)
                          : 1;
                    const content = getCellContent(row, col);

                    const isEmpty =
                      content === null ||
                      content === undefined ||
                      content === '';

                    return (
                      <td
                        key={col}
                        rowSpan={rowspan > 1 ? rowspan : undefined}
                        className="px-3 py-2 text-sm text-gray-300 border-r border-zinc-800 last:border-r-0 align-top"
                      >
                        {isEmpty ? (
                          <span className="text-zinc-700">—</span>
                        ) : (
                          content
                        )}
                      </td>
                    );
                  })}
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
}
