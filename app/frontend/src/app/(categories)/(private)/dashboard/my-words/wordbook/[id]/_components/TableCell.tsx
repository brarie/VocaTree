'use client';

import { useState, useRef, useEffect, memo } from 'react';
import {
  FlattenedRow,
  ColumnType,
  WordbookColumnResponse,
} from '@/types/api/wordbookWord';
import { Textarea } from '@/components/ui/textarea';
import {
  useUpdateWord,
  useUpdateSense,
  useUpdateExample,
  useAutoCompleteFromDictionary,
} from '@/hooks/wordbookWord/queries';
import { getColumnLevel, getFieldName } from '@/lib/wordbookTableUtils';
import { cn } from '@/lib/utils';
import { Loader2, ArrowUp, ArrowDown, Plus, Trash2 } from 'lucide-react';
import { PartOfSpeechCombobox } from './PartOfSpeechCombobox';
import { MultiValueEditor } from './MultiValueEditor';
import { getPartOfSpeechAbbreviation } from '@/types/constants/partOfSpeech';
import { AutocompleteInput } from './AutocompleteInput';
import { DictionarySenseModal } from './DictionarySenseModal';
import { PublicWordSimpleView } from '@/types/api/dictionary';
import { toast } from 'sonner';

interface TableCellProps {
  row: FlattenedRow;
  columnType: ColumnType;
  wordbookId: number;
  columnsData: WordbookColumnResponse | undefined;
  isSelected: boolean;
  isEditing: boolean;
  onEndEdit: () => void;
  onMoveUp: () => void;
  onMoveDown: () => void;
  onAddSubLevel: () => void;
  onDelete: () => void;
  isReordering: boolean;
  onAutoCompleteSuccess?: (wordId: number) => void;
}

export const TableCell = memo(
  function TableCell({
    row,
    columnType,
    wordbookId,
    columnsData,
    isSelected,
    isEditing,
    onEndEdit,
    onMoveUp,
    onMoveDown,
    onAddSubLevel,
    onDelete,
    isReordering,
    onAutoCompleteSuccess,
  }: TableCellProps) {
    const level = getColumnLevel(columnType);
    const inputRef = useRef<HTMLInputElement | HTMLTextAreaElement>(null);

    const fieldValue = getCellValue(row, columnType);
    const [editValue, setEditValue] = useState(fieldValue);
    const [senseModalState, setSenseModalState] = useState<{
      isOpen: boolean;
      slug: string;
    }>({ isOpen: false, slug: '' });

    const updateWord = useUpdateWord();
    const updateSense = useUpdateSense();
    const updateExample = useUpdateExample();
    const autoComplete = useAutoCompleteFromDictionary();

    const isSaving =
      updateWord.isPending ||
      updateSense.isPending ||
      updateExample.isPending ||
      autoComplete.isPending;

    useEffect(() => {
      if (isEditing && inputRef.current) {
        inputRef.current.focus();
      }
    }, [isEditing]);

    useEffect(() => {
      if (!isEditing) {
        setEditValue(fieldValue);
      }
    }, [fieldValue, isEditing]);

    const saveWordLevel = async (finalValue: any, fieldName: string) => {
      const spellingValue =
        fieldName === 'spelling'
          ? finalValue === ''
            ? null
            : finalValue
          : row.spelling;

      await updateWord.mutateAsync({
        wordbookId,
        wordId: row.wordId,
        data: { spelling: spellingValue as any },
      });
    };

    const saveSenseLevel = async (finalValue: any, fieldName: string) => {
      if (!row.senseId) return;

      await updateSense.mutateAsync({
        wordbookId,
        wordId: row.wordId,
        senseId: row.senseId,
        data: {
          definition: fieldName === 'definition' ? finalValue : row.definition,
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
    };

    const saveExampleLevel = async (finalValue: any, fieldName: string) => {
      if (!row.senseId || !row.exampleId) return;

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
    };

    const handleSave = async (valueOverride?: any) => {
      const finalValue =
        valueOverride !== undefined ? valueOverride : editValue;

      if (finalValue === fieldValue) {
        onEndEdit();
        return;
      }

      const fieldName = getFieldName(columnType);

      try {
        if (level === 1) {
          await saveWordLevel(finalValue, fieldName);
        } else if (level === 2) {
          await saveSenseLevel(finalValue, fieldName);
        } else if (level === 3) {
          await saveExampleLevel(finalValue, fieldName);
        }
      } catch (error) {
        // Error handled by mutation
      }

      onEndEdit();
    };

    const handleCancel = () => {
      setEditValue(fieldValue);
      onEndEdit();
    };

    const handleSelectWord = (word: PublicWordSimpleView) => {
      setSenseModalState({ isOpen: true, slug: word.slug });
    };

    const handleCloseSenseModal = () => {
      setSenseModalState({ isOpen: false, slug: '' });
      onEndEdit();
    };

    const handleApplyAutoComplete = async (
      dictionaryWordId: number,
      selectedSenses: Array<{
        sense: any;
        selectedExamples: any[];
      }>,
    ) => {
      if (selectedSenses.length === 0) {
        toast.error('선택된 뜻이 없습니다.');
        return;
      }

      try {
        await autoComplete.mutateAsync({
          wordbookId,
          wordId: row.wordId,
          data: {
            dictionaryWordId,
            selectedSenses: selectedSenses.map((s) => ({
              dictionarySenseId: s.sense.id,
              dictionaryExampleIds: s.selectedExamples.map((e) => e.id),
            })),
          },
        });

        setSenseModalState({ isOpen: false, slug: '' });

        // 자동완성 성공 후 focus 재설정을 위해 wordId 전달
        if (onAutoCompleteSuccess) {
          onAutoCompleteSuccess(row.wordId);
        }
      } catch (error) {
        // 에러는 mutation에서 처리됨
      }
    };

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
            onSelectWord={handleSelectWord}
            inputRef={inputRef}
          />
        ) : (
          <DisplayCell value={fieldValue} columnType={columnType} />
        )}

        {isSaving && (
          <div className="absolute top-1 right-1">
            <Loader2 className="h-3 w-3 animate-spin text-blue-400" />
          </div>
        )}

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
            {(level === 1 || level === 2) && (
              <button
                onMouseDown={(e) => {
                  e.preventDefault();
                  e.stopPropagation();
                  onAddSubLevel();
                }}
                className="p-0.5 bg-zinc-800/90 rounded transition-colors hover:bg-green-900/90"
                title={
                  level === 1
                    ? '의미 추가 (Ctrl+Enter)'
                    : '예문 추가 (Ctrl+Enter)'
                }
              >
                <Plus className="h-3 w-3 text-green-400 hover:text-green-200" />
              </button>
            )}
            <button
              onMouseDown={(e) => {
                e.preventDefault();
                e.stopPropagation();
                onDelete();
              }}
              className="p-0.5 bg-zinc-800/90 rounded transition-colors hover:bg-red-900/90"
              title="삭제 (Ctrl+Shift+D)"
            >
              <Trash2 className="h-3 w-3 text-red-400 hover:text-red-200" />
            </button>
          </div>
        )}

        {senseModalState.isOpen && (
          <DictionarySenseModal
            isOpen={senseModalState.isOpen}
            onClose={handleCloseSenseModal}
            slug={senseModalState.slug}
            columnsData={columnsData}
            onApply={handleApplyAutoComplete}
          />
        )}
      </div>
    );
  },
  (prevProps, nextProps) => {
    if (prevProps.isSelected !== nextProps.isSelected) return false;
    if (prevProps.isEditing !== nextProps.isEditing) return false;
    if (prevProps.isReordering !== nextProps.isReordering) return false;
    if (prevProps.wordbookId !== nextProps.wordbookId) return false;
    if (prevProps.row.rowId !== nextProps.row.rowId) return false;
    if (prevProps.columnType !== nextProps.columnType) return false;

    const prevValue = getCellValue(prevProps.row, prevProps.columnType);
    const nextValue = getCellValue(nextProps.row, nextProps.columnType);

    if (Array.isArray(prevValue) || Array.isArray(nextValue)) {
      if (!Array.isArray(prevValue) || !Array.isArray(nextValue)) return false;
      if (prevValue.length !== nextValue.length) return false;
      for (let i = 0; i < prevValue.length; i++) {
        if (prevValue[i] !== nextValue[i]) return false;
      }
      return true;
    }

    return prevValue === nextValue;
  },
);

function EditingCell({
  columnType,
  value,
  onChange,
  onSave,
  onCancel,
  onSelectWord,
  inputRef,
}: {
  columnType: ColumnType;
  value: any;
  onChange: (value: any) => void;
  onSave: () => void;
  onCancel: () => void;
  onSelectWord: (word: PublicWordSimpleView) => void;
  inputRef: React.RefObject<any>;
}) {
  if (columnType === ColumnType.SPELLING) {
    return (
      <AutocompleteInput
        value={value}
        onChange={onChange}
        onSelectWord={onSelectWord}
        onSave={onSave}
        onCancel={onCancel}
        inputRef={inputRef as React.RefObject<HTMLInputElement>}
      />
    );
  }

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

  useEffect(() => {
    if (inputRef.current && inputRef.current instanceof HTMLTextAreaElement) {
      inputRef.current.style.height = 'auto';
      inputRef.current.style.height = `${inputRef.current.scrollHeight}px`;
    }
  }, [value]);

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Escape') {
      e.preventDefault();
      onCancel();
    }
    if (e.key === 'Enter') {
      e.preventDefault();
      onSave();
    }
  };

  return (
    <Textarea
      ref={inputRef as any}
      value={value || ''}
      onChange={(e) => {
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
