'use client';

import { useState, useRef, useEffect } from 'react';
import { FlattenedRow, ColumnType } from '@/types/api/wordbookWord';
import { Textarea } from '@/components/ui/textarea';
import {
  useUpdateWord,
  useUpdateSense,
  useUpdateExample,
} from '@/hooks/wordbookWord/queries';
import { getColumnLevel, getFieldName } from '@/lib/wordbookTableUtils';
import { cn } from '@/lib/utils';
import { Loader2, ArrowUp, ArrowDown } from 'lucide-react';
import { PartOfSpeechCombobox } from './PartOfSpeechCombobox';
import { MultiValueEditor } from './MultiValueEditor';
import { getPartOfSpeechAbbreviation } from '@/types/constants/partOfSpeech';

interface TableCellProps {
  row: FlattenedRow;
  columnType: ColumnType;
  wordbookId: number;
  isSelected: boolean;
  isEditing: boolean;
  onEndEdit: () => void;
  onMoveUp: () => void;
  onMoveDown: () => void;
  isReordering: boolean;
}

export function TableCell({
  row,
  columnType,
  wordbookId,
  isSelected,
  isEditing,
  onEndEdit,
  onMoveUp,
  onMoveDown,
  isReordering,
}: TableCellProps) {
  const level = getColumnLevel(columnType);
  const inputRef = useRef<HTMLInputElement | HTMLTextAreaElement>(null);

  // Get field value
  const fieldValue = getCellValue(row, columnType);
  const [editValue, setEditValue] = useState(fieldValue);

  // Mutations
  const updateWord = useUpdateWord();
  const updateSense = useUpdateSense();
  const updateExample = useUpdateExample();

  // Determine which mutation is in progress
  const isSaving =
    updateWord.isPending || updateSense.isPending || updateExample.isPending;

  // Auto-focus input when editing starts
  useEffect(() => {
    if (isEditing && inputRef.current) {
      inputRef.current.focus();
    }
  }, [isEditing]);

  // Update editValue when row data changes
  useEffect(() => {
    if (!isEditing) {
      setEditValue(fieldValue);
    }
  }, [fieldValue, isEditing]);

  // Handle save on blur (with optional value override for immediate save)
  const handleSave = async (valueOverride?: any) => {
    const finalValue = valueOverride !== undefined ? valueOverride : editValue;

    // Check if value changed
    if (finalValue === fieldValue) {
      onEndEdit();
      return;
    }

    const fieldName = getFieldName(columnType);

    try {
      if (level === 1) {
        // Level 1: Word (spelling만 있음)
        // 빈 문자열은 null로 변환
        const spellingValue =
          fieldName === 'spelling'
            ? finalValue === ''
              ? null
              : finalValue
            : row.spelling;

        await updateWord.mutateAsync({
          wordbookId,
          wordId: row.wordId,
          data: {
            spelling: spellingValue as any,
          },
        });
      } else if (level === 2 && row.senseId) {
        // Level 2: Sense (모든 필드 포함)
        await updateSense.mutateAsync({
          wordbookId,
          wordId: row.wordId,
          senseId: row.senseId,
          data: {
            definition:
              fieldName === 'definition' ? finalValue : row.definition,
            engDefinition:
              fieldName === 'engDefinition' ? finalValue : row.engDefinition,
            partOfSpeech:
              fieldName === 'partOfSpeech' ? finalValue : row.partOfSpeech,
            synonyms: fieldName === 'synonyms' ? finalValue : row.synonyms,
            antonyms: fieldName === 'antonyms' ? finalValue : row.antonyms,
            americanPhonetic:
              fieldName === 'americanPhonetic' ? finalValue : row.usPhonetic,
            britishPhonetic:
              fieldName === 'britishPhonetic' ? finalValue : row.ukPhonetic,
          },
        });
      } else if (level === 3 && row.senseId && row.exampleId) {
        // Level 3: Example (모든 필드 포함)
        await updateExample.mutateAsync({
          wordbookId,
          wordId: row.wordId,
          senseId: row.senseId,
          exampleId: row.exampleId,
          data: {
            sentence: fieldName === 'sentence' ? finalValue : row.sentence,
            translation:
              fieldName === 'translation' ? finalValue : row.translation,
          },
        });
      }
    } catch (error) {
      // Error already handled by mutation
    }

    onEndEdit();
  };

  // Handle cancel
  const handleCancel = () => {
    setEditValue(fieldValue);
    onEndEdit();
  };

  // Render cell content
  return (
    <div
      data-row-id={row.rowId}
      data-column-id={columnType}
      className={cn(
        'w-full h-full p-2 outline-none relative break-words',
        !fieldValue && !isEditing && 'text-white/30 italic',
      )}
    >
      {isEditing ? (
        <EditingCell
          columnType={columnType}
          value={editValue}
          onChange={setEditValue}
          onSave={handleSave}
          onCancel={handleCancel}
          inputRef={inputRef}
        />
      ) : (
        <DisplayCell value={fieldValue} columnType={columnType} />
      )}

      {/* Loading indicator */}
      {isSaving && (
        <div className="absolute top-1 right-1">
          <Loader2 className="h-3 w-3 animate-spin text-blue-400" />
        </div>
      )}

      {/* Move up/down buttons - show only on selected cell */}
      {isSelected && !isEditing && !isSaving && (
        <div className="absolute top-1 right-1 flex gap-0.5">
          <button
            onMouseDown={(e) => {
              e.preventDefault();
              e.stopPropagation();
              if (!isReordering) {
                onMoveUp();
              }
            }}
            disabled={isReordering}
            className={cn(
              'p-0.5 bg-zinc-800/90 rounded transition-colors',
              isReordering
                ? 'opacity-50 cursor-not-allowed'
                : 'hover:bg-zinc-700/90',
            )}
            title="위로 이동 (Alt+Up)"
          >
            <ArrowUp
              className={cn(
                'h-3 w-3',
                isReordering
                  ? 'text-zinc-600'
                  : 'text-zinc-400 hover:text-zinc-200',
              )}
            />
          </button>
          <button
            onMouseDown={(e) => {
              e.preventDefault();
              e.stopPropagation();
              if (!isReordering) {
                onMoveDown();
              }
            }}
            disabled={isReordering}
            className={cn(
              'p-0.5 bg-zinc-800/90 rounded transition-colors',
              isReordering
                ? 'opacity-50 cursor-not-allowed'
                : 'hover:bg-zinc-700/90',
            )}
            title="아래로 이동 (Alt+Down)"
          >
            <ArrowDown
              className={cn(
                'h-3 w-3',
                isReordering
                  ? 'text-zinc-600'
                  : 'text-zinc-400 hover:text-zinc-200',
              )}
            />
          </button>
        </div>
      )}
    </div>
  );
}

function EditingCell({
  columnType,
  value,
  onChange,
  onSave,
  onCancel,
  inputRef,
}: {
  columnType: ColumnType;
  value: any;
  onChange: (value: any) => void;
  onSave: () => void;
  onCancel: () => void;
  inputRef: React.RefObject<any>;
}) {
  // Part of Speech: Combobox
  if (columnType === ColumnType.PART_OF_SPEECH) {
    return (
      <PartOfSpeechCombobox
        value={value}
        onChange={onChange}
        onBlur={onSave}
        onCancel={onCancel}
      />
    );
  }

  // Synonyms/Antonyms: Multi-value editor
  if (
    columnType === ColumnType.SYNONYMS ||
    columnType === ColumnType.ANTONYMS
  ) {
    return (
      <MultiValueEditor
        value={value}
        onChange={onChange}
        onBlur={onSave}
        placeholder={
          columnType === ColumnType.SYNONYMS
            ? '유의어 입력...'
            : '반의어 입력...'
        }
      />
    );
  }

  // Auto-resize textarea when value changes
  useEffect(() => {
    if (inputRef.current && inputRef.current instanceof HTMLTextAreaElement) {
      // Reset height to auto to get correct scrollHeight
      inputRef.current.style.height = 'auto';
      // Set height to scrollHeight (content height)
      inputRef.current.style.height = `${inputRef.current.scrollHeight}px`;
    }
  }, [value]);

  // Handle keyboard events
  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Escape') {
      e.preventDefault();
      onCancel();
    }
    // Enter saves (no line break)
    if (e.key === 'Enter') {
      e.preventDefault();
      onSave();
    }
  };

  // Use Textarea with auto-resize for all fields
  return (
    <Textarea
      ref={inputRef as any}
      value={value || ''}
      onChange={(e) => {
        // Remove line breaks (prevent Enter from creating new lines)
        const newValue = e.target.value.replace(/\n/g, ' ');
        onChange(newValue);
      }}
      onBlur={() => onSave()}
      onKeyDown={handleKeyDown}
      rows={1}
      className="w-full bg-zinc-900 border-zinc-700 text-white text-sm resize-none overflow-hidden min-h-[32px]"
      placeholder="입력하세요..."
    />
  );
}

function DisplayCell({
  value,
  columnType,
}: {
  value: any;
  columnType: ColumnType;
}) {
  if (!value) {
    return <span className="text-white/30 italic text-sm">-</span>;
  }

  if (columnType === ColumnType.PART_OF_SPEECH) {
    const abbreviation = getPartOfSpeechAbbreviation(value);
    return (
      <span className="px-2 py-0.5 bg-zinc-700/50 border border-zinc-600/30 rounded text-xs text-zinc-300">
        {abbreviation || value}
      </span>
    );
  }

  if (
    (columnType === ColumnType.SYNONYMS ||
      columnType === ColumnType.ANTONYMS) &&
    Array.isArray(value)
  ) {
    if (value.length === 0) {
      return <span className="text-white/30 italic text-sm">-</span>;
    }
    return (
      <div className="flex flex-wrap gap-1">
        {value.map((item, index) => (
          <span
            key={index}
            className="px-2 py-0.5 bg-zinc-800 rounded text-xs text-white/90"
          >
            {item}
          </span>
        ))}
      </div>
    );
  }

  return <span className="text-sm text-white/90">{value}</span>;
}

function getCellValue(row: FlattenedRow, columnType: ColumnType): any {
  const fieldMap: Record<ColumnType, keyof FlattenedRow> = {
    [ColumnType.SPELLING]: 'spelling',
    [ColumnType.DEFINITION]: 'definition',
    [ColumnType.ENG_DEFINITION]: 'engDefinition',
    [ColumnType.US_PHONETIC]: 'usPhonetic',
    [ColumnType.UK_PHONETIC]: 'ukPhonetic',
    [ColumnType.PART_OF_SPEECH]: 'partOfSpeech',
    [ColumnType.SYNONYMS]: 'synonyms',
    [ColumnType.ANTONYMS]: 'antonyms',
    [ColumnType.SENTENCE]: 'sentence',
    [ColumnType.TRANSLATION]: 'translation',
  };

  return row[fieldMap[columnType]];
}
