'use client';

import { useState, useMemo, useEffect, useCallback, useRef } from 'react';
import {
  useReactTable,
  getCoreRowModel,
  ColumnDef,
  flexRender,
  ColumnResizeMode,
} from '@tanstack/react-table';
import {
  useWordList,
  useColumns,
  useCreateWordSeries,
  useCreateSenseSeries,
  useCreateExample,
  useUpdateWord,
  useUpdateSense,
  useUpdateExample,
  useDeleteWord,
  useDeleteSense,
  useDeleteExample,
  useUpdateColumnWidth,
  useSwapWordsOrder,
  useSwapSensesOrder,
  useSwapExamplesOrder,
} from '@/hooks/wordbookWord/queries';
import {
  flattenWordListToRows,
  getColumnHeader,
  getColumnLevel,
  getFieldName,
} from '@/lib/wordbookTableUtils';
import { FlattenedRow, ColumnType } from '@/types/api/wordbookWord';
import { ColumnManager } from './ColumnManager';
import { KeyboardShortcutsDialog } from './KeyboardShortcutsDialog';
import { HelpDialog } from './HelpDialog';
import { DeleteLevelConfirmDialog } from './DeleteLevelConfirmDialog';
import { TableCell } from './TableCell';
import { TableSkeleton } from './TableSkeleton';
import { Button } from '@/components/ui/button';
import { Plus, Settings2, Keyboard, HelpCircle } from 'lucide-react';
import { cn } from '@/lib/utils';
import { toast } from 'sonner';

interface WordbookTableProps {
  wordbookId: number;
}

export function WordbookTable({ wordbookId }: WordbookTableProps) {
  const { data: wordListData, isLoading: isLoadingWords } =
    useWordList(wordbookId);
  const { data: columnsData, isLoading: isLoadingColumns } =
    useColumns(wordbookId);
  const createWordSeriesMutation = useCreateWordSeries();
  const createSenseSeriesMutation = useCreateSenseSeries();
  const createExampleMutation = useCreateExample();
  const updateWordMutation = useUpdateWord();
  const updateSenseMutation = useUpdateSense();
  const updateExampleMutation = useUpdateExample();
  const deleteWordMutation = useDeleteWord();
  const deleteSenseMutation = useDeleteSense();
  const deleteExampleMutation = useDeleteExample();
  const updateColumnWidthMutation = useUpdateColumnWidth();
  const swapWordsMutation = useSwapWordsOrder();
  const swapSensesMutation = useSwapSensesOrder();
  const swapExamplesMutation = useSwapExamplesOrder();

  const [showColumnManager, setShowColumnManager] = useState(false);
  const [showKeyboardShortcuts, setShowKeyboardShortcuts] = useState(false);
  const [showHelp, setShowHelp] = useState(false);
  const [showDeleteDialog, setShowDeleteDialog] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState<{
    level: 1 | 2 | 3;
    wordId: number;
    senseId?: number;
    exampleId?: number;
  } | null>(null);
  const [selectedCell, setSelectedCell] = useState<{
    row: string;
    col: string;
  } | null>(null);

  const [editingCell, setEditingCell] = useState<{
    row: string;
    col: string;
  } | null>(null);

  const [pendingAutoCompleteFocus, setPendingAutoCompleteFocus] = useState<{
    wordId: number;
    timestamp: number;
  } | null>(null);

  const handleMoveRef = useRef<(direction: 'up' | 'down') => void>(() => {});
  const handleCreateSubLevelRef = useRef<() => void>(() => {});
  const handleDeleteLevelRef = useRef<() => void>(() => {});
  const keydownHandlerRef = useRef<(e: KeyboardEvent) => void>(() => {});

  const isReordering =
    swapWordsMutation.isPending ||
    swapSensesMutation.isPending ||
    swapExamplesMutation.isPending;

  // Stable callbacks for TableCell (prevent unnecessary re-renders via React.memo)
  const stableHandleEndEdit = useCallback(() => {
    setEditingCell(null);
  }, []);

  const stableHandleMoveUp = useCallback(() => {
    handleMoveRef.current('up');
  }, []);

  const stableHandleMoveDown = useCallback(() => {
    handleMoveRef.current('down');
  }, []);

  const stableHandleAddSubLevel = useCallback(() => {
    handleCreateSubLevelRef.current();
  }, []);

  const stableHandleDelete = useCallback(() => {
    handleDeleteLevelRef.current();
  }, []);

  const stableHandleAutoCompleteSuccess = useCallback((wordId: number) => {
    setPendingAutoCompleteFocus({ wordId, timestamp: Date.now() });
  }, []);

  // Cell click handler - 바로 editing mode로 진입
  const handleCellClick = (rowId: string, columnType: string) => {
    setSelectedCell({ row: rowId, col: columnType });
    setEditingCell({ row: rowId, col: columnType });
  };

  // 데이터 갱신 후 focus 재설정
  useEffect(() => {
    if (!pendingAutoCompleteFocus || !wordListData || !columnsData) return;

    const { wordId } = pendingAutoCompleteFocus;
    const newRows = flattenWordListToRows(wordListData.wordList);
    const firstRowOfWord = newRows.find((r) => r.wordId === wordId);

    if (firstRowOfWord) {
      // 첫 번째 active column 찾기
      const firstActiveColumn = Object.entries(columnsData.columns).find(
        ([_, setting]) => setting.isActive,
      );

      if (firstActiveColumn) {
        const firstColumnType = firstActiveColumn[0] as ColumnType;

        // selected 모드만 설정 (editing 모드 아님)
        setSelectedCell({ row: firstRowOfWord.rowId, col: firstColumnType });
        setEditingCell(null);

        setPendingAutoCompleteFocus(null);
      }
    }
  }, [wordListData, pendingAutoCompleteFocus, columnsData]);

  // Auto-scroll to selected cell
  useEffect(() => {
    if (!selectedCell) return;
    const td = document.querySelector(
      `td[data-row-id="${selectedCell.row}"][data-col-id="${selectedCell.col}"]`,
    );
    if (td) {
      td.scrollIntoView({ block: 'nearest', inline: 'nearest' });
    }
  }, [selectedCell]);

  // Flatten data
  const flattenedRows = useMemo(() => {
    if (!wordListData) return [];
    return flattenWordListToRows(wordListData.wordList);
  }, [wordListData]);

  // Build columns based on active columns (no dynamic state in deps)
  const columns = useMemo<ColumnDef<FlattenedRow>[]>(() => {
    if (!columnsData) return [];

    const cols: ColumnDef<FlattenedRow>[] = [];

    Object.entries(columnsData.columns).forEach(([key, setting]) => {
      if (!setting.isActive) return;

      const columnType = key as ColumnType;

      cols.push({
        id: columnType,
        header: getColumnHeader(columnType),
        size: setting.width,
      });
    });

    return cols;
  }, [columnsData]);

  const [columnResizeMode] = useState<ColumnResizeMode>('onChange');
  const [resizingColumn, setResizingColumn] = useState<string | null>(null);

  const table = useReactTable({
    data: flattenedRows,
    columns,
    getCoreRowModel: getCoreRowModel(),
    columnResizeMode,
    enableColumnResizing: true,
    getRowId: (row) => row.rowId,
  });

  // Save column width on resize end
  useEffect(() => {
    const handleMouseUp = () => {
      if (resizingColumn) {
        const size = table.getState().columnSizing[resizingColumn];
        if (size) {
          const columnType = resizingColumn as ColumnType;
          const currentSize = columnsData?.columns[columnType]?.width;

          if (currentSize && currentSize !== size) {
            updateColumnWidthMutation.mutate({
              wordbookId,
              columnType,
              data: { width: size as number },
            });
          }
        }
        setResizingColumn(null);
      }
    };

    document.addEventListener('mouseup', handleMouseUp);
    document.addEventListener('touchend', handleMouseUp);

    return () => {
      document.removeEventListener('mouseup', handleMouseUp);
      document.removeEventListener('touchend', handleMouseUp);
    };
  }, [
    resizingColumn,
    table,
    columnsData,
    wordbookId,
    updateColumnWidthMutation,
  ]);

  const isCellRendered = (
    row: FlattenedRow,
    columnType: ColumnType,
  ): boolean => {
    const level = getColumnLevel(columnType);
    if (level === 1) return row.isFirstRowOfWord;
    if (level === 2) return row.isFirstRowOfSense && row.senseId !== null;
    return row.exampleId !== null;
  };

  const findRepresentativeRow = (
    currentRow: FlattenedRow,
    columnType: ColumnType,
  ): FlattenedRow | null => {
    const level = getColumnLevel(columnType);

    if (level === 1) {
      return (
        flattenedRows.find(
          (r) => r.wordId === currentRow.wordId && r.isFirstRowOfWord,
        ) || null
      );
    }

    if (level === 2) {
      if (!currentRow.senseId) return null;
      return (
        flattenedRows.find(
          (r) =>
            r.wordId === currentRow.wordId &&
            r.senseId === currentRow.senseId &&
            r.isFirstRowOfSense,
        ) || null
      );
    }

    return currentRow.exampleId !== null ? currentRow : null;
  };

  const selectFirstCell = () => {
    if (flattenedRows.length === 0 || columns.length === 0) return false;
    setSelectedCell({ row: flattenedRows[0].rowId, col: ColumnType.SPELLING });
    return false;
  };

  const navigateHorizontal = (
    currentRow: FlattenedRow,
    currentColIndex: number,
    isLeft: boolean,
  ): boolean => {
    const nextColIndex = isLeft ? currentColIndex - 1 : currentColIndex + 1;

    if (nextColIndex >= 0 && nextColIndex < columns.length) {
      const nextColumnType = columns[nextColIndex].id as ColumnType;

      if (isCellRendered(currentRow, nextColumnType)) {
        setSelectedCell({ row: currentRow.rowId, col: nextColumnType });
      } else {
        const representativeRow = findRepresentativeRow(
          currentRow,
          nextColumnType,
        );
        if (representativeRow) {
          setSelectedCell({
            row: representativeRow.rowId,
            col: nextColumnType,
          });
        }
      }
      return true;
    }

    return false;
  };

  const navigateVertical = (
    currentRow: FlattenedRow,
    currentColumnType: ColumnType,
    isUp: boolean,
  ): void => {
    const navigableRows = flattenedRows.filter((r) =>
      isCellRendered(r, currentColumnType),
    );

    const currentIndexInNavigable = navigableRows.findIndex(
      (r) => r.rowId === currentRow.rowId,
    );

    if (currentIndexInNavigable === -1) return;

    const nextIndexInNavigable = isUp
      ? currentIndexInNavigable - 1
      : currentIndexInNavigable + 1;

    if (
      nextIndexInNavigable >= 0 &&
      nextIndexInNavigable < navigableRows.length
    ) {
      const nextRow = navigableRows[nextIndexInNavigable];
      setSelectedCell({ row: nextRow.rowId, col: currentColumnType });
    }
  };

  const handleCreateWord = () => {
    if (wordListData && wordListData.wordList.length >= 100) {
      toast.error('단어는 최대 100개까지 추가할 수 있습니다.');
      return;
    }

    const hadFocus = selectedCell !== null;

    createWordSeriesMutation.mutate(wordbookId, {
      onSuccess: (data) => {
        if (!hadFocus && data.wordList.length > 0 && columns.length > 0) {
          const newWord = data.wordList[data.wordList.length - 1];
          const newSense = newWord.senses[0];
          const newExample = newSense.examples[0];
          const newRowId = `word-${newWord.id}-sense-${newSense.id}-example-${newExample.id}`;
          setSelectedCell({ row: newRowId, col: ColumnType.SPELLING });
        }
      },
    });
  };

  const handleCreateSubLevel = () => {
    if (!selectedCell) return;

    const currentRow = flattenedRows.find((r) => r.rowId === selectedCell.row);
    if (!currentRow) return;

    const currentColumnLevel = getColumnLevel(selectedCell.col as ColumnType);
    const prevSelected = selectedCell;

    if (currentColumnLevel === 1) {
      const hasActiveLevel2Column = columnsData
        ? Object.entries(columnsData.columns).some(([key, setting]) => {
            const columnType = key as ColumnType;
            const level = getColumnLevel(columnType);
            return level === 2 && setting.isActive;
          })
        : false;

      if (!hasActiveLevel2Column) {
        toast.error(
          'Level 2 칼럼(한글 뜻, 품사 등)이 활성화되어 있지 않습니다.',
        );
        return;
      }

      createSenseSeriesMutation.mutate(
        { wordbookId, wordId: currentRow.wordId },
        {
          onSuccess: (data) => {
            const word = data.wordList.find((w) => w.id === currentRow.wordId);
            if (word && word.senses.length > 0) {
              const newSense = word.senses[word.senses.length - 1];
              const newExample = newSense.examples[0];
              const newRowId = `word-${word.id}-sense-${newSense.id}-example-${newExample.id}`;

              const firstLevel2Column = columns.find(
                (c) => getColumnLevel(c.id as ColumnType) === 2,
              );

              if (firstLevel2Column) {
                setSelectedCell({
                  row: newRowId,
                  col: firstLevel2Column.id as string,
                });
              } else {
                setSelectedCell(prevSelected);
              }
            } else {
              setSelectedCell(prevSelected);
            }
          },
        },
      );
    } else if (currentColumnLevel === 2) {
      if (!currentRow.senseId) {
        toast.error('의미가 없는 행에서는 예문을 추가할 수 없습니다.');
        return;
      }

      const hasActiveLevel3Column = columnsData
        ? Object.entries(columnsData.columns).some(([key, setting]) => {
            const columnType = key as ColumnType;
            const level = getColumnLevel(columnType);
            return level === 3 && setting.isActive;
          })
        : false;

      if (!hasActiveLevel3Column) {
        toast.error('Level 3 칼럼(예문, 번역 등)이 활성화되어 있지 않습니다.');
        return;
      }

      createExampleMutation.mutate(
        {
          wordbookId,
          wordId: currentRow.wordId,
          senseId: currentRow.senseId,
        },
        {
          onSuccess: (data) => {
            const word = data.wordList.find((w) => w.id === currentRow.wordId);
            const sense = word?.senses.find((s) => s.id === currentRow.senseId);

            if (sense && sense.examples.length > 0) {
              const newExample = sense.examples[sense.examples.length - 1];
              const newRowId = `word-${currentRow.wordId}-sense-${currentRow.senseId}-example-${newExample.id}`;

              const firstLevel3Column = columns.find(
                (c) => getColumnLevel(c.id as ColumnType) === 3,
              );

              if (firstLevel3Column) {
                setSelectedCell({
                  row: newRowId,
                  col: firstLevel3Column.id as string,
                });
              } else {
                setSelectedCell(prevSelected);
              }
            } else {
              setSelectedCell(prevSelected);
            }
          },
        },
      );
    }
  };

  const handleDeleteCellContent = () => {
    if (!selectedCell) return;

    const currentRow = flattenedRows.find((r) => r.rowId === selectedCell.row);
    if (!currentRow) return;

    const currentColumnType = selectedCell.col as ColumnType;
    const level = getColumnLevel(currentColumnType);
    const fieldName = getFieldName(currentColumnType);

    if (level === 1) {
      updateWordMutation.mutate({
        wordbookId,
        wordId: currentRow.wordId,
        data: { spelling: null as any },
      });
    } else if (level === 2 && currentRow.senseId) {
      const emptyData: any = {
        definition: fieldName === 'definition' ? '' : currentRow.definition,
        engDefinition:
          fieldName === 'engDefinition' ? '' : currentRow.engDefinition,
        partOfSpeech:
          fieldName === 'partOfSpeech' ? null : currentRow.partOfSpeech,
        synonyms: fieldName === 'synonyms' ? null : currentRow.synonyms,
        antonyms: fieldName === 'antonyms' ? null : currentRow.antonyms,
        americanPhonetic:
          fieldName === 'americanPhonetic' ? '' : currentRow.usPhonetic,
        britishPhonetic:
          fieldName === 'britishPhonetic' ? '' : currentRow.ukPhonetic,
      };

      updateSenseMutation.mutate({
        wordbookId,
        wordId: currentRow.wordId,
        senseId: currentRow.senseId,
        data: emptyData,
      });
    } else if (level === 3 && currentRow.senseId && currentRow.exampleId) {
      const emptyData: any = {
        sentence: fieldName === 'sentence' ? '' : currentRow.sentence,
        translation: fieldName === 'translation' ? '' : currentRow.translation,
      };

      updateExampleMutation.mutate({
        wordbookId,
        wordId: currentRow.wordId,
        senseId: currentRow.senseId,
        exampleId: currentRow.exampleId,
        data: emptyData,
      });
    }
  };

  const handleDeleteLevel = () => {
    if (!selectedCell) return;

    const currentRow = flattenedRows.find((r) => r.rowId === selectedCell.row);
    if (!currentRow) return;

    const currentColumnType = selectedCell.col as ColumnType;
    const level = getColumnLevel(currentColumnType);

    setDeleteTarget({
      level,
      wordId: currentRow.wordId,
      senseId: currentRow.senseId ?? undefined,
      exampleId: currentRow.exampleId ?? undefined,
    });
    setShowDeleteDialog(true);
  };

  const handleCellNavigation = (e: KeyboardEvent) => {
    if (!selectedCell) return;

    const currentRow = flattenedRows.find((r) => r.rowId === selectedCell.row);
    const currentColIndex = columns.findIndex((c) => c.id === selectedCell.col);

    if (!currentRow || currentColIndex === -1) return;

    const currentColumnType = selectedCell.col as ColumnType;

    if (e.key === 'ArrowLeft' || e.key === 'ArrowRight' || e.key === 'Tab') {
      const isLeft = e.key === 'ArrowLeft' || (e.key === 'Tab' && e.shiftKey);

      if (navigateHorizontal(currentRow, currentColIndex, isLeft)) {
        return;
      }

      if (e.key === 'Tab') {
        const currentRowIndex = flattenedRows.findIndex(
          (r) => r.rowId === currentRow.rowId,
        );
        const nextRowIndex = isLeft ? currentRowIndex - 1 : currentRowIndex + 1;

        if (nextRowIndex >= 0 && nextRowIndex < flattenedRows.length) {
          const nextRow = flattenedRows[nextRowIndex];
          const targetColIndex = isLeft ? columns.length - 1 : 0;
          const colStep = isLeft ? -1 : 1;

          let foundColIndex = targetColIndex;

          while (foundColIndex >= 0 && foundColIndex < columns.length) {
            const colType = columns[foundColIndex].id as ColumnType;
            if (isCellRendered(nextRow, colType)) {
              setSelectedCell({ row: nextRow.rowId, col: colType });
              return;
            } else {
              const representativeRow = findRepresentativeRow(nextRow, colType);
              if (representativeRow) {
                setSelectedCell({
                  row: representativeRow.rowId,
                  col: colType,
                });
                return;
              }
            }
            foundColIndex += colStep;
          }
        }
      }
    }

    if (e.key === 'ArrowUp' || e.key === 'ArrowDown') {
      navigateVertical(currentRow, currentColumnType, e.key === 'ArrowUp');
    }
  };

  // Keyboard handler via ref (stable effect, always uses latest closures)
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      keydownHandlerRef.current(e);
    };

    document.addEventListener('keydown', handleKeyDown);
    return () => document.removeEventListener('keydown', handleKeyDown);
  }, []);

  const handleAddWord = async () => {
    if (wordListData && wordListData.wordList.length >= 100) {
      alert('최대 100개까지 추가할 수 있습니다.');
      return;
    }
    await createWordSeriesMutation.mutateAsync(wordbookId);
  };

  const handleDeleteConfirm = () => {
    if (!deleteTarget) return;

    const { level, wordId, senseId, exampleId } = deleteTarget;

    // Level 1: 단어 삭제
    if (level === 1) {
      deleteWordMutation.mutate({
        wordbookId,
        wordId,
      });
    }
    // Level 2: Sense 삭제
    else if (level === 2 && senseId) {
      deleteSenseMutation.mutate({
        wordbookId,
        wordId,
        senseId,
      });
    }
    // Level 3: Example 삭제
    else if (level === 3 && senseId && exampleId) {
      deleteExampleMutation.mutate({
        wordbookId,
        wordId,
        senseId,
        exampleId,
      });
    }

    // 초기화
    setDeleteTarget(null);
    // 삭제된 셀이 선택되어 있을 수 있으므로 포커스 초기화
    setSelectedCell(null);
    setEditingCell(null);
  };

  const swapWords = (currentRow: FlattenedRow, isUp: boolean) => {
    if (!wordListData) return;

    const allWords = wordListData.wordList;
    const currentIndex = allWords.findIndex((w) => w.id === currentRow.wordId);

    if (currentIndex === -1) return;

    const targetIndex = isUp ? currentIndex - 1 : currentIndex + 1;
    if (targetIndex < 0 || targetIndex >= allWords.length) return;

    const currentWord = allWords[currentIndex];
    const targetWord = allWords[targetIndex];

    swapWordsMutation.mutate({
      wordbookId,
      data: { wordId1: currentWord.id, wordId2: targetWord.id },
    });
  };

  const swapSenses = (currentRow: FlattenedRow, isUp: boolean) => {
    if (!wordListData || !currentRow.senseId) return;

    const word = wordListData.wordList.find((w) => w.id === currentRow.wordId);
    if (!word) return;

    const allSenses = word.senses;
    const currentIndex = allSenses.findIndex(
      (s) => s.id === currentRow.senseId,
    );
    if (currentIndex === -1) return;

    const targetIndex = isUp ? currentIndex - 1 : currentIndex + 1;
    if (targetIndex < 0 || targetIndex >= allSenses.length) return;

    const currentSense = allSenses[currentIndex];
    const targetSense = allSenses[targetIndex];

    swapSensesMutation.mutate({
      wordbookId,
      wordId: currentRow.wordId,
      data: { senseId1: currentSense.id, senseId2: targetSense.id },
    });
  };

  const swapExamples = (currentRow: FlattenedRow, isUp: boolean) => {
    if (!wordListData || !currentRow.senseId || !currentRow.exampleId) return;

    const word = wordListData.wordList.find((w) => w.id === currentRow.wordId);
    const sense = word?.senses.find((s) => s.id === currentRow.senseId);
    if (!sense) return;

    const allExamples = sense.examples;
    const currentIndex = allExamples.findIndex(
      (e) => e.id === currentRow.exampleId,
    );
    if (currentIndex === -1) return;

    const targetIndex = isUp ? currentIndex - 1 : currentIndex + 1;
    if (targetIndex < 0 || targetIndex >= allExamples.length) return;

    const currentExample = allExamples[currentIndex];
    const targetExample = allExamples[targetIndex];

    swapExamplesMutation.mutate({
      wordbookId,
      wordId: currentRow.wordId,
      senseId: currentRow.senseId,
      data: { exampleId1: currentExample.id, exampleId2: targetExample.id },
    });
  };

  const handleMove = (direction: 'up' | 'down') => {
    if (!selectedCell || !wordListData) return;

    if (
      swapWordsMutation.isPending ||
      swapSensesMutation.isPending ||
      swapExamplesMutation.isPending
    ) {
      return;
    }

    const currentRow = flattenedRows.find((r) => r.rowId === selectedCell.row);
    if (!currentRow) return;

    const currentColumnType = selectedCell.col as ColumnType;
    const level = getColumnLevel(currentColumnType);
    const isUp = direction === 'up';

    if (level === 1) {
      swapWords(currentRow, isUp);
    } else if (level === 2) {
      swapSenses(currentRow, isUp);
    } else if (level === 3) {
      swapExamples(currentRow, isUp);
    }
  };

  // Update refs with latest closures (runs every render, before any events fire)
  handleMoveRef.current = handleMove;
  handleCreateSubLevelRef.current = handleCreateSubLevel;
  handleDeleteLevelRef.current = handleDeleteLevel;

  keydownHandlerRef.current = (e: KeyboardEvent) => {
    if (
      editingCell ||
      showDeleteDialog ||
      showKeyboardShortcuts ||
      showHelp ||
      showColumnManager
    )
      return;

    if (e.key === 'Enter' && (e.ctrlKey || e.metaKey) && e.shiftKey) {
      e.preventDefault();
      handleCreateWord();
      return;
    }

    if (!selectedCell) {
      const shouldSelectFirst =
        e.key === 'ArrowDown' ||
        e.key === 'ArrowUp' ||
        e.key === 'ArrowLeft' ||
        e.key === 'ArrowRight' ||
        e.key === 'Tab' ||
        (e.key === 'Enter' &&
          !e.ctrlKey &&
          !e.shiftKey &&
          !e.altKey &&
          !e.metaKey);

      if (shouldSelectFirst) {
        e.preventDefault();
        selectFirstCell();
      }
      return;
    }

    if (e.key === 'Enter' && (e.ctrlKey || e.metaKey)) {
      e.preventDefault();
      handleCreateSubLevel();
      return;
    }

    if (
      e.key === 'Enter' &&
      !e.ctrlKey &&
      !e.shiftKey &&
      !e.altKey &&
      !e.metaKey
    ) {
      e.preventDefault();
      setEditingCell(selectedCell);
      return;
    }

    if (e.key === 'Delete') {
      e.preventDefault();
      handleDeleteCellContent();
      return;
    }

    if (e.key === 'D' && (e.ctrlKey || e.metaKey) && e.shiftKey) {
      e.preventDefault();
      handleDeleteLevel();
      return;
    }

    if (
      (e.key === 'ArrowUp' || e.key === 'ArrowDown') &&
      e.altKey &&
      !e.ctrlKey &&
      !e.shiftKey &&
      !e.metaKey
    ) {
      e.preventDefault();
      handleMove(e.key === 'ArrowUp' ? 'up' : 'down');
      return;
    }

    if (
      e.key === 'ArrowUp' ||
      e.key === 'ArrowDown' ||
      e.key === 'ArrowLeft' ||
      e.key === 'ArrowRight' ||
      e.key === 'Tab'
    ) {
      e.preventDefault();
      handleCellNavigation(e);
    }
  };

  const handleAutoResizeColumn = (columnId: string) => {
    const columnType = columnId as ColumnType;
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

    const fieldName = fieldMap[columnType];
    if (!fieldName) return;

    let maxLength = 0;
    flattenedRows.forEach((row) => {
      const value = row[fieldName];
      if (!value) return;

      const textLength = Array.isArray(value)
        ? value.join(', ').length
        : String(value).length;

      if (textLength > maxLength) {
        maxLength = textLength;
      }
    });

    const headerText = getColumnHeader(columnType);
    maxLength = Math.max(maxLength, headerText.length);

    const calculatedWidth = maxLength * 8 + 40;

    table.setColumnSizing((prev) => ({
      ...prev,
      [columnId]: calculatedWidth,
    }));

    updateColumnWidthMutation.mutate({
      wordbookId,
      columnType,
      data: { width: calculatedWidth },
    });
  };

  if (isLoadingWords || isLoadingColumns) {
    return <TableSkeleton />;
  }

  if (!wordListData || !columnsData) {
    return (
      <div className="text-center py-12 text-white/60">
        <p className="text-lg mb-2">데이터를 불러올 수 없습니다.</p>
        <p className="text-sm text-white/40">페이지를 새로고침해주세요.</p>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <div className="flex flex-wrap items-center justify-between gap-2">
        <div className="text-sm text-white/60">
          {wordListData.wordList.length} / 100 단어
        </div>
        <div className="flex gap-2 flex-wrap">
          <Button
            onClick={() => setShowHelp(true)}
            variant="outline"
            size="sm"
            className="bg-zinc-800 border-zinc-700 hover:bg-zinc-700"
          >
            <HelpCircle className="h-4 w-4" />
            <span className="hidden sm:inline ml-1">도움말</span>
          </Button>
          <Button
            onClick={() => setShowKeyboardShortcuts(true)}
            variant="outline"
            size="sm"
            className="bg-zinc-800 border-zinc-700 hover:bg-zinc-700"
          >
            <Keyboard className="h-4 w-4" />
            <span className="hidden sm:inline ml-1">단축키</span>
          </Button>
          <Button
            onClick={() => setShowColumnManager(true)}
            variant="outline"
            size="sm"
            className="bg-zinc-800 border-zinc-700 hover:bg-zinc-700"
          >
            <Settings2 className="h-4 w-4" />
            <span className="hidden sm:inline ml-1">칼럼 관리</span>
          </Button>
          <Button
            onClick={handleAddWord}
            disabled={
              createWordSeriesMutation.isPending ||
              (wordListData && wordListData.wordList.length >= 100)
            }
            size="sm"
            className="bg-zinc-800 border-zinc-700 hover:bg-zinc-700"
            variant="outline"
          >
            <Plus className="h-4 w-4" />
            <span className="hidden sm:inline ml-1">새 단어 추가</span>
          </Button>
        </div>
      </div>

      <div className="border border-zinc-800 rounded-lg overflow-x-auto bg-zinc-950">
        {wordListData.wordList.length === 0 ? (
          <div className="text-center py-16 px-4">
            <div className="inline-flex items-center justify-center w-16 h-16 rounded-full bg-zinc-800/50 mb-4">
              <Plus className="h-8 w-8 text-zinc-400" />
            </div>
            <h3 className="text-lg font-semibold text-white mb-2">
              단어를 추가해보세요
            </h3>
            <p className="text-sm text-white/60 mb-6 max-w-md mx-auto">
              단어장이 비어있습니다. 첫 번째 단어를 추가하고 의미와 예문을
              기록해보세요.
            </p>
            <Button
              onClick={handleAddWord}
              size="sm"
              className="bg-blue-600 hover:bg-blue-700 text-white"
            >
              <Plus className="h-4 w-4 mr-2" />첫 번째 단어 추가하기
            </Button>
            <p className="text-xs text-white/40 mt-4">
              또는{' '}
              <kbd className="px-1.5 py-0.5 bg-zinc-800 border border-zinc-700 rounded text-xs">
                Ctrl
              </kbd>{' '}
              +{' '}
              <kbd className="px-1.5 py-0.5 bg-zinc-800 border border-zinc-700 rounded text-xs">
                Shift
              </kbd>{' '}
              +{' '}
              <kbd className="px-1.5 py-0.5 bg-zinc-800 border border-zinc-700 rounded text-xs">
                Enter
              </kbd>{' '}
              를 눌러보세요
            </p>
          </div>
        ) : (
          <>
            <table
              className="border-collapse"
              style={{
                minWidth: '100%',
                width: 'max-content',
              }}
            >
              <thead>
                {table.getHeaderGroups().map((headerGroup) => (
                  <tr key={headerGroup.id} className="border-b border-zinc-800">
                    {headerGroup.headers.map((header) => (
                      <th
                        key={header.id}
                        style={{
                          width: header.getSize(),
                          position: 'relative',
                        }}
                        className="px-3 py-2 text-left text-sm font-semibold bg-zinc-800 text-white border-r border-zinc-700 last:border-r-0"
                      >
                        {flexRender(
                          header.column.columnDef.header,
                          header.getContext(),
                        )}
                        <div
                          onDoubleClick={(e) => {
                            e.preventDefault();
                            e.stopPropagation();
                            handleAutoResizeColumn(header.column.id);
                          }}
                          onMouseDown={(e) => {
                            if (e.detail !== 2) {
                              setResizingColumn(header.column.id);
                              header.getResizeHandler()(e);
                            }
                          }}
                          onTouchStart={(e) => {
                            setResizingColumn(header.column.id);
                            header.getResizeHandler()(e);
                          }}
                          className={cn(
                            'absolute right-0 top-0 h-full w-1 cursor-col-resize select-none touch-none',
                            'hover:bg-blue-500 hover:w-1.5',
                            header.column.getIsResizing() &&
                              'bg-blue-500 w-1.5',
                          )}
                        />
                      </th>
                    ))}
                    <th className="bg-zinc-800 border-r-0 w-auto"></th>
                  </tr>
                ))}
              </thead>
              <tbody>
                {table.getRowModel().rows.map((row) => (
                  <tr
                    key={row.id}
                    className="border-b border-zinc-800 hover:bg-zinc-900/50 transition-colors"
                  >
                    {row
                      .getVisibleCells()
                      .filter((cell) => {
                        const columnType = cell.column.id as ColumnType;
                        const level = getColumnLevel(columnType);
                        const cellRow = row.original;

                        if (level === 1) {
                          return cellRow.isFirstRowOfWord;
                        }

                        if (level === 2) {
                          if (!cellRow.isFirstRowOfSense) return false;
                          if (cellRow.senseId === null) return false;
                          return true;
                        }

                        if (level === 3) {
                          return cellRow.exampleId !== null;
                        }

                        return true;
                      })
                      .map((cell) => {
                        const columnType = cell.column.id as ColumnType;
                        const level = getColumnLevel(columnType);
                        const cellRow = row.original;

                        const rowspan =
                          level === 1
                            ? cellRow.wordRowspan
                            : level === 2
                              ? cellRow.senseRowspan
                              : 1;

                        const isSelected =
                          selectedCell?.row === cellRow.rowId &&
                          selectedCell?.col === columnType;
                        const isEditing =
                          editingCell?.row === cellRow.rowId &&
                          editingCell?.col === columnType;

                        const handleTdClick = (e: React.MouseEvent) => {
                          if (!isEditing) {
                            e.preventDefault();
                            handleCellClick(cellRow.rowId, columnType);
                          }
                        };

                        return (
                          <td
                            key={cell.id}
                            data-row-id={cellRow.rowId}
                            data-col-id={columnType}
                            rowSpan={rowspan}
                            onMouseDown={handleTdClick}
                            className={cn(
                              'border-r border-zinc-800 last:border-r-0 p-0 cursor-pointer align-top bg-zinc-900/30',
                            )}
                            style={{
                              width: cell.column.getSize(),
                              minWidth: cell.column.getSize(),
                              maxWidth: cell.column.getSize(),
                              ...(isSelected && {
                                outline: '2px solid rgb(59 130 246)',
                                outlineOffset: '-2px',
                              }),
                              ...(isEditing && {
                                outline: '2px solid rgb(34 197 94)',
                                outlineOffset: '-2px',
                              }),
                            }}
                          >
                            <TableCell
                              row={cellRow}
                              columnType={columnType}
                              wordbookId={wordbookId}
                              columnsData={columnsData}
                              isSelected={isSelected && !editingCell}
                              isEditing={isEditing}
                              onEndEdit={stableHandleEndEdit}
                              onMoveUp={stableHandleMoveUp}
                              onMoveDown={stableHandleMoveDown}
                              onAddSubLevel={stableHandleAddSubLevel}
                              onDelete={stableHandleDelete}
                              isReordering={isReordering}
                              onAutoCompleteSuccess={
                                stableHandleAutoCompleteSuccess
                              }
                            />
                          </td>
                        );
                      })}
                    <td className="border-r-0 w-auto"></td>
                  </tr>
                ))}
              </tbody>
            </table>

            {wordListData.wordList.length < 100 && (
              <div className="border-t border-zinc-800/50">
                <button
                  onClick={handleAddWord}
                  disabled={createWordSeriesMutation.isPending}
                  className="flex items-center gap-2 px-3 py-2 text-sm text-white/50 hover:text-white hover:bg-zinc-800/30 transition-colors w-full text-left disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  <Plus className="h-4 w-4" />
                  <span>새로운 단어</span>
                </button>
              </div>
            )}
          </>
        )}
      </div>

      <HelpDialog open={showHelp} onClose={() => setShowHelp(false)} />

      <KeyboardShortcutsDialog
        open={showKeyboardShortcuts}
        onClose={() => setShowKeyboardShortcuts(false)}
      />

      {deleteTarget && (
        <DeleteLevelConfirmDialog
          open={showDeleteDialog}
          onOpenChange={setShowDeleteDialog}
          level={deleteTarget.level}
          onConfirm={handleDeleteConfirm}
          isPending={
            deleteWordMutation.isPending ||
            deleteSenseMutation.isPending ||
            deleteExampleMutation.isPending
          }
        />
      )}

      <ColumnManager
        wordbookId={wordbookId}
        open={showColumnManager}
        onClose={() => setShowColumnManager(false)}
      />
    </div>
  );
}
