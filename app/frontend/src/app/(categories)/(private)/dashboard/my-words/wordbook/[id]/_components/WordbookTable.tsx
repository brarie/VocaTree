'use client';

import { useState, useMemo, useEffect } from 'react';
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
  useCreateWord,
  useCreateSense,
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
import {
  Plus,
  Settings2,
  Keyboard,
  HelpCircle,
  ArrowUp,
  ArrowDown,
} from 'lucide-react';
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
  const createWordMutation = useCreateWord();
  const createSenseMutation = useCreateSense();
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

  // Cell click handler - 바로 editing mode로 진입
  const handleCellClick = (rowId: string, columnType: string) => {
    setSelectedCell({ row: rowId, col: columnType });
    setEditingCell({ row: rowId, col: columnType });
  };

  // Flatten data
  const flattenedRows = useMemo(() => {
    if (!wordListData) return [];
    return flattenWordListToRows(wordListData.wordList);
  }, [wordListData]);

  // Build columns based on active columns
  const columns = useMemo<ColumnDef<FlattenedRow>[]>(() => {
    if (!columnsData) return [];

    const cols: ColumnDef<FlattenedRow>[] = [];

    // Build columns from ColumnType enum
    Object.entries(columnsData.columns).forEach(([key, setting]) => {
      if (!setting.isActive) return;

      const columnType = key as ColumnType;

      cols.push({
        id: columnType,
        header: getColumnHeader(columnType),
        size: setting.width,
        cell: ({ row }) => (
          <TableCell
            row={row.original}
            columnType={columnType}
            wordbookId={wordbookId}
            isSelected={
              selectedCell?.row === row.original.rowId &&
              selectedCell?.col === columnType &&
              !editingCell
            }
            isEditing={
              editingCell?.row === row.original.rowId &&
              editingCell?.col === columnType
            }
            onEndEdit={() => {
              setEditingCell(null);
              // selectedCell은 유지
            }}
            onMoveUp={() => handleMove('up')}
            onMoveDown={() => handleMove('down')}
            isReordering={
              swapWordsMutation.isPending ||
              swapSensesMutation.isPending ||
              swapExamplesMutation.isPending
            }
          />
        ),
      });
    });

    return cols;
  }, [
    columnsData,
    wordbookId,
    selectedCell,
    editingCell,
    swapWordsMutation,
    swapSensesMutation,
    swapExamplesMutation,
  ]);

  const [columnResizeMode] = useState<ColumnResizeMode>('onChange');
  const [resizingColumn, setResizingColumn] = useState<string | null>(null);

  const table = useReactTable({
    data: flattenedRows,
    columns,
    getCoreRowModel: getCoreRowModel(),
    columnResizeMode,
    enableColumnResizing: true,
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

  // Helper: 해당 셀이 실제로 렌더되는지 확인 (병합 여부 포함)
  const isCellRendered = (
    row: FlattenedRow,
    columnType: ColumnType,
  ): boolean => {
    const level = getColumnLevel(columnType);

    // Level 1: 첫 번째 행만 렌더
    if (level === 1) return row.isFirstRowOfWord;

    // Level 2: 첫 번째 Sense 행이고, senseId가 있어야 함
    if (level === 2) return row.isFirstRowOfSense && row.senseId !== null;

    // Level 3: exampleId가 있어야 함
    return row.exampleId !== null;
  };

  // Helper: merge된 칼럼의 대표 행 찾기
  const findRepresentativeRow = (
    currentRow: FlattenedRow,
    columnType: ColumnType,
  ): FlattenedRow | null => {
    const level = getColumnLevel(columnType);

    if (level === 1) {
      // Level 1: 같은 wordId의 isFirstRowOfWord인 행
      return (
        flattenedRows.find(
          (r) => r.wordId === currentRow.wordId && r.isFirstRowOfWord,
        ) || null
      );
    } else if (level === 2) {
      // Level 2: 같은 senseId의 isFirstRowOfSense인 행
      if (!currentRow.senseId) return null;
      return (
        flattenedRows.find(
          (r) =>
            r.wordId === currentRow.wordId &&
            r.senseId === currentRow.senseId &&
            r.isFirstRowOfSense,
        ) || null
      );
    } else {
      // Level 3: merge 없음, exampleId가 있을 때만 반환
      return currentRow.exampleId !== null ? currentRow : null;
    }
  };

  // Keyboard navigation
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      // Editing 중이거나 Dialog 열려있으면 키보드 네비게이션 비활성화
      if (
        editingCell ||
        showDeleteDialog ||
        showKeyboardShortcuts ||
        showHelp ||
        showColumnManager
      )
        return;

      // Ctrl+Shift+Enter: 새 단어 추가 (selectedCell 여부와 무관, 최우선)
      if (e.key === 'Enter' && (e.ctrlKey || e.metaKey) && e.shiftKey) {
        e.preventDefault();

        // 100개 제한 체크
        if (wordListData && wordListData.wordList.length >= 100) {
          toast.error('단어는 최대 100개까지 추가할 수 있습니다.');
          return;
        }

        createWordMutation.mutate(wordbookId, {
          onSuccess: (data) => {
            // 새로 생성된 단어의 첫 번째 셀로 focus 이동
            if (data.wordList.length > 0 && columns.length > 0) {
              const newWord = data.wordList[data.wordList.length - 1];
              const newRowId = `word-${newWord.id}`;
              const firstColumnId = columns[0].id as string;
              setSelectedCell({ row: newRowId, col: firstColumnId });
            }
          },
        });
        return;
      }

      // selectedCell이 없으면 첫 번째 렌더된 셀 선택 (Arrow keys, Tab, plain Enter)
      if (!selectedCell && flattenedRows.length > 0 && columns.length > 0) {
        if (
          e.key === 'ArrowDown' ||
          e.key === 'ArrowUp' ||
          e.key === 'ArrowLeft' ||
          e.key === 'ArrowRight' ||
          e.key === 'Tab' ||
          (e.key === 'Enter' &&
            !e.ctrlKey &&
            !e.shiftKey &&
            !e.altKey &&
            !e.metaKey)
        ) {
          e.preventDefault();
          // Find first rendered cell
          for (let i = 0; i < flattenedRows.length; i++) {
            for (let j = 0; j < columns.length; j++) {
              const row = flattenedRows[i];
              const columnType = columns[j].id as ColumnType;
              if (isCellRendered(row, columnType)) {
                setSelectedCell({
                  row: row.rowId,
                  col: columnType,
                });
                return;
              }
            }
          }
        }
        return;
      }

      if (!selectedCell) return;

      // Ctrl+Enter: 하위 레벨 추가 (현재 칼럼 레벨 기준)
      if (e.key === 'Enter' && (e.ctrlKey || e.metaKey)) {
        e.preventDefault();

        if (!selectedCell) return;

        const currentRow = flattenedRows.find(
          (r) => r.rowId === selectedCell.row,
        );
        // 현재 focus된 칼럼의 level 확인 (row.level이 아님!)
        const currentColumnLevel = getColumnLevel(
          selectedCell.col as ColumnType,
        );

        if (!currentRow) return;

        const prevSelected = selectedCell;

        // Level 1 칼럼: Sense 추가
        if (currentColumnLevel === 1) {
          // Level 2 칼럼이 하나라도 active인지 확인
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

          createSenseMutation.mutate(
            {
              wordbookId,
              wordId: currentRow.wordId,
            },
            {
              onSuccess: (data) => {
                // 새로 생성된 Sense의 첫 번째 행으로 focus 이동
                const word = data.wordList.find(
                  (w) => w.id === currentRow.wordId,
                );
                if (word && word.senses.length > 0) {
                  // displayOrder로 정렬 후 마지막 Sense 찾기
                  const sortedSenses = [...word.senses].sort(
                    (a, b) => b.displayOrder - a.displayOrder,
                  );
                  const newSense = sortedSenses[0];
                  const newRowId = `word-${word.id}-sense-${newSense.id}`;

                  // 새로 생성된 Sense의 첫 번째 Level 2 칼럼으로 focus
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
        }
        // Level 2 칼럼: Example 추가
        else if (currentColumnLevel === 2) {
          if (!currentRow.senseId) {
            toast.error('의미가 없는 행에서는 예문을 추가할 수 없습니다.');
            return;
          }

          // Level 3 칼럼이 하나라도 active인지 확인
          const hasActiveLevel3Column = columnsData
            ? Object.entries(columnsData.columns).some(([key, setting]) => {
                const columnType = key as ColumnType;
                const level = getColumnLevel(columnType);
                return level === 3 && setting.isActive;
              })
            : false;

          if (!hasActiveLevel3Column) {
            toast.error(
              'Level 3 칼럼(예문, 번역 등)이 활성화되어 있지 않습니다.',
            );
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
                // 새로 생성된 Example 행으로 focus 이동
                const word = data.wordList.find(
                  (w) => w.id === currentRow.wordId,
                );
                const sense = word?.senses.find(
                  (s) => s.id === currentRow.senseId,
                );

                if (sense && sense.examples.length > 0) {
                  // displayOrder로 정렬 후 마지막 Example 찾기
                  const sortedExamples = [...sense.examples].sort(
                    (a, b) => b.displayOrder - a.displayOrder,
                  );
                  const newExample = sortedExamples[0];
                  const newRowId = `word-${currentRow.wordId}-sense-${currentRow.senseId}-example-${newExample.id}`;

                  // 첫 번째 Level 3 칼럼으로 focus
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
        return;
      }

      // Enter (modifier 없이): 편집 모드로 전환
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

      // Delete: 셀 내용 지우기
      if (e.key === 'Delete') {
        e.preventDefault();

        const currentRow = flattenedRows.find(
          (r) => r.rowId === selectedCell.row,
        );
        if (!currentRow) return;

        const currentColumnType = selectedCell.col as ColumnType;
        const level = getColumnLevel(currentColumnType);
        const fieldName = getFieldName(currentColumnType);

        // Level 1: 단어 spelling 지우기
        if (level === 1) {
          updateWordMutation.mutate({
            wordbookId,
            wordId: currentRow.wordId,
            data: { spelling: null as any },
          });
        }
        // Level 2: Sense 필드 지우기
        else if (level === 2 && currentRow.senseId) {
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
        }
        // Level 3: Example 필드 지우기
        else if (level === 3 && currentRow.senseId && currentRow.exampleId) {
          const emptyData: any = {
            sentence: fieldName === 'sentence' ? '' : currentRow.sentence,
            translation:
              fieldName === 'translation' ? '' : currentRow.translation,
          };

          updateExampleMutation.mutate({
            wordbookId,
            wordId: currentRow.wordId,
            senseId: currentRow.senseId,
            exampleId: currentRow.exampleId,
            data: emptyData,
          });
        }

        return;
      }

      // Ctrl+Shift+D: 현재 레벨 삭제
      if (e.key === 'D' && (e.ctrlKey || e.metaKey) && e.shiftKey) {
        e.preventDefault();

        const currentRow = flattenedRows.find(
          (r) => r.rowId === selectedCell.row,
        );
        if (!currentRow) return;

        const currentColumnType = selectedCell.col as ColumnType;
        const level = getColumnLevel(currentColumnType);

        // 삭제 대상 설정 후 다이얼로그 열기
        setDeleteTarget({
          level,
          wordId: currentRow.wordId,
          senseId: currentRow.senseId ?? undefined,
          exampleId: currentRow.exampleId ?? undefined,
        });
        setShowDeleteDialog(true);

        return;
      }

      // Alt + 위/아래: 순서 변경 (인접 항목과 교환)
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

      // Arrow keys & Tab: 셀 이동
      if (
        e.key === 'ArrowUp' ||
        e.key === 'ArrowDown' ||
        e.key === 'ArrowLeft' ||
        e.key === 'ArrowRight' ||
        e.key === 'Tab'
      ) {
        e.preventDefault();

        const currentRow = flattenedRows.find(
          (r) => r.rowId === selectedCell.row,
        );
        const currentColIndex = columns.findIndex(
          (c) => c.id === selectedCell.col,
        );

        if (!currentRow || currentColIndex === -1) return;

        const currentColumnType = selectedCell.col as ColumnType;

        // Left / Right / Tab: 같은 rowId에서 칼럼 이동
        if (
          e.key === 'ArrowLeft' ||
          e.key === 'ArrowRight' ||
          e.key === 'Tab'
        ) {
          const isLeft =
            e.key === 'ArrowLeft' || (e.key === 'Tab' && e.shiftKey);
          const nextColIndex = isLeft
            ? currentColIndex - 1
            : currentColIndex + 1;

          // 같은 행에서 이동 가능한 경우
          if (nextColIndex >= 0 && nextColIndex < columns.length) {
            const nextColumnType = columns[nextColIndex].id as ColumnType;

            // 해당 셀이 현재 rowId에서 렌더되는지 확인
            if (isCellRendered(currentRow, nextColumnType)) {
              setSelectedCell({
                row: currentRow.rowId,
                col: nextColumnType,
              });
            } else {
              // Merge되어 있으면 대표 행 찾기
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
              // representativeRow가 null이면 focus 유지 (아무것도 안 함)
            }
            return; // 처리 완료 후 종료
          } else {
            // 행 경계 넘어가기 (Tab만 가능, Arrow는 안됨)
            if (e.key === 'Tab') {
              // 다음/이전 행의 첫/마지막 칼럼
              const currentRowIndex = flattenedRows.findIndex(
                (r) => r.rowId === currentRow.rowId,
              );
              const nextRowIndex = isLeft
                ? currentRowIndex - 1
                : currentRowIndex + 1;

              if (nextRowIndex >= 0 && nextRowIndex < flattenedRows.length) {
                const nextRow = flattenedRows[nextRowIndex];
                const targetColIndex = isLeft ? columns.length - 1 : 0;
                const targetColumnType = columns[targetColIndex]
                  .id as ColumnType;

                // 렌더되는 칼럼 찾기
                let foundColIndex = targetColIndex;
                const colStep = isLeft ? -1 : 1;

                while (foundColIndex >= 0 && foundColIndex < columns.length) {
                  const colType = columns[foundColIndex].id as ColumnType;
                  if (isCellRendered(nextRow, colType)) {
                    setSelectedCell({
                      row: nextRow.rowId,
                      col: colType,
                    });
                    return;
                  } else {
                    const representativeRow = findRepresentativeRow(
                      nextRow,
                      colType,
                    );
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
        }

        // Up / Down: 같은 칼럼에서 행 이동 (실제 렌더되는 셀만)
        if (e.key === 'ArrowUp' || e.key === 'ArrowDown') {
          const isUp = e.key === 'ArrowUp';

          // 현재 칼럼에서 렌더되는 모든 행 찾기
          const navigableRows = flattenedRows.filter((r) =>
            isCellRendered(r, currentColumnType),
          );

          // 현재 위치 찾기
          const currentIndexInNavigable = navigableRows.findIndex(
            (r) => r.rowId === currentRow.rowId,
          );

          if (currentIndexInNavigable === -1) return;

          // 다음 위치 계산
          const nextIndexInNavigable = isUp
            ? currentIndexInNavigable - 1
            : currentIndexInNavigable + 1;

          if (
            nextIndexInNavigable >= 0 &&
            nextIndexInNavigable < navigableRows.length
          ) {
            const nextRow = navigableRows[nextIndexInNavigable];
            setSelectedCell({
              row: nextRow.rowId,
              col: currentColumnType,
            });
          }
        }
      }
    };

    document.addEventListener('keydown', handleKeyDown);
    return () => document.removeEventListener('keydown', handleKeyDown);
  }, [
    selectedCell,
    editingCell,
    flattenedRows,
    columns,
    showDeleteDialog,
    showKeyboardShortcuts,
    showHelp,
    showColumnManager,
  ]);

  const handleAddWord = async () => {
    if (wordListData && wordListData.wordList.length >= 100) {
      alert('최대 100개까지 추가할 수 있습니다.');
      return;
    }
    await createWordMutation.mutateAsync(wordbookId);
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

  // Move row up/down - swap with adjacent item
  const handleMove = (direction: 'up' | 'down') => {
    if (!selectedCell || !wordListData) return;

    // Prevent multiple simultaneous swap requests
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
      const allWords = wordListData.wordList
        .slice()
        .sort((a, b) => a.displayOrder - b.displayOrder);

      const currentIndex = allWords.findIndex(
        (w) => w.id === currentRow.wordId,
      );

      if (currentIndex === -1) return;

      const targetIndex = isUp ? currentIndex - 1 : currentIndex + 1;
      if (targetIndex < 0 || targetIndex >= allWords.length) return;

      const currentWord = allWords[currentIndex];
      const targetWord = allWords[targetIndex];

      swapWordsMutation.mutate({
        wordbookId,
        data: {
          wordId1: currentWord.id,
          wordId2: targetWord.id,
        },
      });
    } else if (level === 2 && currentRow.senseId) {
      // Sense level: 같은 word의 모든 sense
      const word = wordListData.wordList.find(
        (w) => w.id === currentRow.wordId,
      );
      if (!word) return;

      const allSenses = word.senses
        .slice()
        .sort((a, b) => a.displayOrder - b.displayOrder);

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
        data: {
          senseId1: currentSense.id,
          senseId2: targetSense.id,
        },
      });
    } else if (level === 3 && currentRow.senseId && currentRow.exampleId) {
      // Example level: 같은 sense의 모든 example
      const word = wordListData.wordList.find(
        (w) => w.id === currentRow.wordId,
      );
      const sense = word?.senses.find((s) => s.id === currentRow.senseId);
      if (!sense) return;

      const allExamples = sense.examples
        .slice()
        .sort((a, b) => a.displayOrder - b.displayOrder);

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
        data: {
          exampleId1: currentExample.id,
          exampleId2: targetExample.id,
        },
      });
    }
  };

  // 더블클릭으로 칼럼 자동 크기 조정
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

    // 모든 행의 해당 칼럼 값 중 가장 긴 것 찾기
    let maxLength = 0;
    flattenedRows.forEach((row) => {
      const value = row[fieldName];
      if (!value) return;

      let textLength = 0;
      if (Array.isArray(value)) {
        // 배열인 경우 (synonyms, antonyms)
        textLength = value.join(', ').length;
      } else {
        textLength = String(value).length;
      }

      if (textLength > maxLength) {
        maxLength = textLength;
      }
    });

    // 헤더 텍스트 길이도 고려
    const headerText = getColumnHeader(columnType);
    const headerLength = headerText.length;
    maxLength = Math.max(maxLength, headerLength);

    // 문자당 약 8px + padding 40px (양쪽 20px씩)
    const calculatedWidth = maxLength * 8 + 40;

    // 즉시 UI 업데이트 (TanStack Table state)
    table.setColumnSizing((prev) => ({
      ...prev,
      [columnId]: calculatedWidth,
    }));

    // 백엔드에 저장
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
      {/* Toolbar */}
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
              createWordMutation.isPending ||
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

      {/* Table */}
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
                        {/* Resize handle */}
                        <div
                          onDoubleClick={(e) => {
                            e.preventDefault();
                            e.stopPropagation();
                            handleAutoResizeColumn(header.column.id);
                          }}
                          onMouseDown={(e) => {
                            // 더블클릭이 아닐 때만 리사이즈 시작
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
                    {/* 더미 열 - 남은 공간 채우기 */}
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

                        // Level 1: 병합 체크 (첫 번째 행만 렌더)
                        if (level === 1) {
                          return cellRow.isFirstRowOfWord;
                        }

                        // Level 2: 병합 체크 + 칸 생성 체크
                        if (level === 2) {
                          if (!cellRow.isFirstRowOfSense) return false; // 병합된 행
                          if (cellRow.senseId === null) return false; // 칸이 생성되지 않음
                          return true;
                        }

                        // Level 3: 칸 생성 체크만 (병합 없음)
                        if (level === 3) {
                          return cellRow.exampleId !== null;
                        }

                        return true;
                      })
                      .map((cell) => {
                        const columnType = cell.column.id as ColumnType;
                        const level = getColumnLevel(columnType);
                        const cellRow = row.original;

                        // rowspan 계산
                        const rowspan =
                          level === 1
                            ? cellRow.wordRowspan
                            : level === 2
                              ? cellRow.senseRowspan
                              : 1;

                        // isSelected, isEditing 계산 (td에 border styling 적용)
                        const isSelected =
                          selectedCell?.row === cellRow.rowId &&
                          selectedCell?.col === columnType;
                        const isEditing =
                          editingCell?.row === cellRow.rowId &&
                          editingCell?.col === columnType;

                        // td 클릭 핸들러
                        const handleTdClick = (e: React.MouseEvent) => {
                          if (!isEditing) {
                            e.preventDefault();
                            handleCellClick(cellRow.rowId, columnType);
                          }
                        };

                        return (
                          <td
                            key={cell.id}
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
                            {flexRender(
                              cell.column.columnDef.cell,
                              cell.getContext(),
                            )}
                          </td>
                        );
                      })}
                    {/* 더미 셀 - 남은 공간 채우기 */}
                    <td className="border-r-0 w-auto"></td>
                  </tr>
                ))}
              </tbody>
            </table>

            {/* Notion-style add row button */}
            {wordListData.wordList.length < 100 && (
              <div className="border-t border-zinc-800/50">
                <button
                  onClick={handleAddWord}
                  disabled={createWordMutation.isPending}
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

      {/* Help Dialog */}
      <HelpDialog open={showHelp} onClose={() => setShowHelp(false)} />

      {/* Keyboard Shortcuts Dialog */}
      <KeyboardShortcutsDialog
        open={showKeyboardShortcuts}
        onClose={() => setShowKeyboardShortcuts(false)}
      />

      {/* Delete Level Confirm Dialog */}
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

      {/* Column Manager Dialog */}
      <ColumnManager
        wordbookId={wordbookId}
        open={showColumnManager}
        onClose={() => setShowColumnManager(false)}
      />
    </div>
  );
}
