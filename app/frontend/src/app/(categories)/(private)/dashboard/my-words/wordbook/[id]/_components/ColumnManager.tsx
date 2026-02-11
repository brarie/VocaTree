'use client';

import { useState, useEffect } from 'react';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Switch } from '@/components/ui/switch';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import {
  useColumns,
  useToggleColumnActive,
  useUpdateColumnWidth,
} from '@/hooks/wordbookWord/queries';
import { ColumnType } from '@/types/api/wordbookWord';
import {
  getColumnHeader,
  getColumnLevel,
  canActivateColumn,
  canDeactivateColumn,
} from '@/lib/wordbookTableUtils';
import { toast } from 'sonner';
import { Info } from 'lucide-react';

interface ColumnManagerProps {
  wordbookId: number;
  open: boolean;
  onClose: () => void;
}

export function ColumnManager({ wordbookId, open, onClose }: ColumnManagerProps) {
  const { data: columnsData } = useColumns(wordbookId);
  const toggleColumnMutation = useToggleColumnActive();
  const updateWidthMutation = useUpdateColumnWidth();

  const [widthInputs, setWidthInputs] = useState<Record<string, string>>({});

  // Initialize width inputs when data loads
  useEffect(() => {
    if (columnsData) {
      const initialWidths: Record<string, string> = {};
      Object.entries(columnsData.columns).forEach(([key, setting]) => {
        initialWidths[key] = setting.width.toString();
      });
      setWidthInputs(initialWidths);
    }
  }, [columnsData]);

  if (!columnsData) return null;

  const handleToggle = async (columnType: ColumnType, currentlyActive: boolean) => {
    // Get active columns
    const activeColumns = new Set(
      Object.entries(columnsData.columns)
        .filter(([_, setting]) => setting.isActive)
        .map(([key]) => key),
    );

    // Check rules
    if (currentlyActive) {
      // Trying to deactivate
      const validation = canDeactivateColumn(columnType, activeColumns);
      if (!validation.canDeactivate) {
        toast.error(validation.reason || '비활성화할 수 없습니다.');
        return;
      }
    } else {
      // Trying to activate
      const validation = canActivateColumn(columnType, activeColumns);
      if (!validation.canActivate) {
        toast.error(validation.reason || '활성화할 수 없습니다.');
        return;
      }
    }

    await toggleColumnMutation.mutateAsync({ wordbookId, columnType });
  };

  const handleWidthChange = (columnType: ColumnType, value: string) => {
    setWidthInputs((prev) => ({ ...prev, [columnType]: value }));
  };

  const handleWidthBlur = async (columnType: ColumnType) => {
    const value = parseInt(widthInputs[columnType], 10);
    if (isNaN(value) || value < 50 || value > 1000) {
      toast.error('너비는 50~1000 사이여야 합니다.');
      // Reset to original value
      setWidthInputs((prev) => ({
        ...prev,
        [columnType]: columnsData.columns[columnType].width.toString(),
      }));
      return;
    }

    // Only update if changed
    if (value !== columnsData.columns[columnType].width) {
      await updateWidthMutation.mutateAsync({
        wordbookId,
        columnType,
        data: { width: value },
      });
    }
  };

  // Group columns by level
  const level1Columns = Object.keys(ColumnType).filter(
    (key) => getColumnLevel(key as ColumnType) === 1,
  );
  const level2Columns = Object.keys(ColumnType).filter(
    (key) => getColumnLevel(key as ColumnType) === 2,
  );
  const level3Columns = Object.keys(ColumnType).filter(
    (key) => getColumnLevel(key as ColumnType) === 3,
  );

  return (
    <Dialog open={open} onOpenChange={onClose}>
      <DialogContent className="bg-zinc-900 border-zinc-800 max-h-[90vh] overflow-y-auto max-w-2xl">
        <DialogHeader>
          <DialogTitle className="text-white">칼럼 관리</DialogTitle>
          <DialogDescription className="text-white/60">
            표시할 칼럼을 선택하고 너비를 조정하세요.
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-6 mt-4">
          {/* Rules info */}
          <div className="flex items-start gap-2 p-3 bg-blue-500/10 border border-blue-500/20 rounded-lg">
            <Info className="h-5 w-5 text-blue-400 flex-shrink-0 mt-0.5" />
            <div className="text-sm text-blue-200">
              <p className="font-medium mb-1">칼럼 규칙</p>
              <ul className="list-disc list-inside space-y-0.5 text-blue-200/80">
                <li>단어(SPELLING)는 필수 칼럼입니다.</li>
                <li>
                  Level 3 칼럼(예문, 번역)을 활성화하려면 Level 2 칼럼이 하나 이상
                  활성화되어야 합니다.
                </li>
              </ul>
            </div>
          </div>

          {/* Level 1 */}
          <div>
            <h3 className="text-sm font-semibold text-white mb-3">
              Level 1: 단어
            </h3>
            <div className="space-y-2">
              {level1Columns.map((key) => {
                const columnType = key as ColumnType;
                const setting = columnsData.columns[columnType];
                return (
                  <ColumnRow
                    key={key}
                    columnType={columnType}
                    setting={setting}
                    widthInput={widthInputs[columnType] || ''}
                    onToggle={handleToggle}
                    onWidthChange={handleWidthChange}
                    onWidthBlur={handleWidthBlur}
                    disabled={columnType === ColumnType.SPELLING}
                  />
                );
              })}
            </div>
          </div>

          {/* Level 2 */}
          <div>
            <h3 className="text-sm font-semibold text-white mb-3">
              Level 2: 의미
            </h3>
            <div className="space-y-2">
              {level2Columns.map((key) => {
                const columnType = key as ColumnType;
                const setting = columnsData.columns[columnType];
                return (
                  <ColumnRow
                    key={key}
                    columnType={columnType}
                    setting={setting}
                    widthInput={widthInputs[columnType] || ''}
                    onToggle={handleToggle}
                    onWidthChange={handleWidthChange}
                    onWidthBlur={handleWidthBlur}
                  />
                );
              })}
            </div>
          </div>

          {/* Level 3 */}
          <div>
            <h3 className="text-sm font-semibold text-white mb-3">
              Level 3: 예문
            </h3>
            <div className="space-y-2">
              {level3Columns.map((key) => {
                const columnType = key as ColumnType;
                const setting = columnsData.columns[columnType];
                return (
                  <ColumnRow
                    key={key}
                    columnType={columnType}
                    setting={setting}
                    widthInput={widthInputs[columnType] || ''}
                    onToggle={handleToggle}
                    onWidthChange={handleWidthChange}
                    onWidthBlur={handleWidthBlur}
                  />
                );
              })}
            </div>
          </div>
        </div>

        <div className="flex justify-end mt-6">
          <Button onClick={onClose} className="bg-white text-black hover:bg-gray-200">
            닫기
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
}

interface ColumnRowProps {
  columnType: ColumnType;
  setting: { isActive: boolean; width: number };
  widthInput: string;
  onToggle: (columnType: ColumnType, currentlyActive: boolean) => void;
  onWidthChange: (columnType: ColumnType, value: string) => void;
  onWidthBlur: (columnType: ColumnType) => void;
  disabled?: boolean;
}

function ColumnRow({
  columnType,
  setting,
  widthInput,
  onToggle,
  onWidthChange,
  onWidthBlur,
  disabled = false,
}: ColumnRowProps) {
  return (
    <div className="flex items-center justify-between p-3 bg-zinc-800/50 rounded-lg">
      <div className="flex items-center gap-3 flex-1">
        <Switch
          checked={setting.isActive}
          onCheckedChange={() => onToggle(columnType, setting.isActive)}
          disabled={disabled}
          className="data-[state=checked]:bg-blue-600"
        />
        <Label className="text-white cursor-pointer">
          {getColumnHeader(columnType)}
        </Label>
        {disabled && (
          <span className="text-xs text-white/40">(필수)</span>
        )}
      </div>
      <div className="flex items-center gap-2">
        <Label className="text-sm text-white/60">너비:</Label>
        <Input
          type="number"
          value={widthInput}
          onChange={(e) => onWidthChange(columnType, e.target.value)}
          onBlur={() => onWidthBlur(columnType)}
          disabled={!setting.isActive}
          className="w-20 h-8 bg-zinc-900 border-zinc-700 text-white text-sm"
          min={50}
          max={1000}
        />
        <span className="text-sm text-white/40">px</span>
      </div>
    </div>
  );
}
