'use client';

import { usePathname, useRouter, useSearchParams } from 'next/navigation';
import { useCallback } from 'react';
import { Checkbox } from '@/components/ui/checkbox';
import { Label } from '@/components/ui/label';
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group';
import {
  TrustedLevel,
  SharedWordbookSortType,
  SharedWordbookBundleSortType,
} from '@/types/api/library';

interface LibraryFilterSidebarProps {
  className?: string;
  type: 'wordbook' | 'bundle';
}

export function LibraryFilterSidebar({
  className,
  type,
}: LibraryFilterSidebarProps) {
  const router = useRouter();
  const pathname = usePathname();
  const searchParams = useSearchParams();

  // URL을 단일 진실 소스로 사용 (state 없이 직접 derive)
  const sortType =
    searchParams.get('sortType') ||
    (type === 'wordbook'
      ? SharedWordbookSortType.CREATED_AT_DESC
      : SharedWordbookBundleSortType.CREATED_AT_DESC);
  const selectedLevels =
    searchParams.get('trustedLevels')?.split(',').filter(Boolean) || [];

  const updateQueryString = useCallback(
    (newParams: Record<string, string | null>) => {
      const current = new URLSearchParams(searchParams.toString());
      Object.entries(newParams).forEach(([key, value]) => {
        if (value === null) {
          current.delete(key);
        } else {
          current.set(key, value);
        }
      });
      current.delete('page');
      return current.toString();
    },
    [searchParams],
  );

  const handleLevelChange = (level: string, checked: boolean) => {
    const nextLevels = checked
      ? [...selectedLevels, level]
      : selectedLevels.filter((l) => l !== level);

    const levelParam = nextLevels.length > 0 ? nextLevels.join(',') : null;
    router.push(
      pathname + '?' + updateQueryString({ trustedLevels: levelParam }),
    );
  };

  const handleSortChange = (value: string) => {
    router.push(pathname + '?' + updateQueryString({ sortType: value }));
  };

  const handleTypeChange = (value: 'wordbook' | 'bundle') => {
    const defaultSort =
      value === 'wordbook'
        ? SharedWordbookSortType.CREATED_AT_DESC
        : SharedWordbookBundleSortType.CREATED_AT_DESC;

    const current = new URLSearchParams(searchParams.toString());
    current.set('type', value);
    current.set('sortType', defaultSort);
    current.delete('page');

    router.push(pathname + '?' + current.toString());
  };

  return (
    <div className={`space-y-5 ${className}`}>
      {/* 1. 자료 유형 */}
      <div className="space-y-2.5">
        <h3 className="text-sm font-semibold text-white">자료 유형</h3>
        <RadioGroup
          value={type}
          onValueChange={(val) =>
            handleTypeChange(val as 'wordbook' | 'bundle')
          }
          className="flex flex-col gap-2"
        >
          <div className="flex items-center space-x-2">
            <RadioGroupItem value="bundle" id="type-bundle" />
            <Label
              htmlFor="type-bundle"
              className="text-sm text-gray-300 cursor-pointer select-none"
            >
              번들
            </Label>
          </div>
          <div className="flex items-center space-x-2">
            <RadioGroupItem value="wordbook" id="type-wordbook" />
            <Label
              htmlFor="type-wordbook"
              className="text-sm text-gray-300 cursor-pointer select-none"
            >
              단어장
            </Label>
          </div>
        </RadioGroup>
      </div>

      <div className="border-t border-white/10" />

      {/* 2. 신뢰 등급 */}
      <div className="space-y-2.5">
        <h3 className="text-sm font-semibold text-white">신뢰 등급</h3>
        <div className="flex flex-col gap-2">
          {[
            { value: TrustedLevel.OFFICIAL, label: '공식' },
            { value: TrustedLevel.VERIFIED, label: '인증됨' },
            { value: TrustedLevel.BASIC, label: '일반' },
          ].map((item) => (
            <div key={item.value} className="flex items-center space-x-2">
              <Checkbox
                id={`level-${item.value}`}
                checked={selectedLevels.includes(item.value)}
                onCheckedChange={(checked) =>
                  handleLevelChange(item.value, checked as boolean)
                }
              />
              <Label
                htmlFor={`level-${item.value}`}
                className="text-sm text-gray-300 cursor-pointer select-none"
              >
                {item.label}
              </Label>
            </div>
          ))}
        </div>
      </div>

      <div className="border-t border-white/10" />

      {/* 3. 정렬 기준 */}
      <div className="space-y-2.5">
        <h3 className="text-sm font-semibold text-white">정렬 기준</h3>
        <RadioGroup
          value={sortType}
          onValueChange={handleSortChange}
          className="flex flex-col gap-2"
        >
          {type === 'wordbook' ? (
            <>
              <div className="flex items-center space-x-2">
                <RadioGroupItem
                  value={SharedWordbookSortType.CREATED_AT_DESC}
                  id="sort-wb-new"
                />
                <Label
                  htmlFor="sort-wb-new"
                  className="text-sm text-gray-300 cursor-pointer select-none"
                >
                  최신순
                </Label>
              </div>
              <div className="flex items-center space-x-2">
                <RadioGroupItem
                  value={SharedWordbookSortType.SUBSCRIPTION_COUNT_DESC}
                  id="sort-wb-pop"
                />
                <Label
                  htmlFor="sort-wb-pop"
                  className="text-sm text-gray-300 cursor-pointer select-none"
                >
                  인기순
                </Label>
              </div>
            </>
          ) : (
            <>
              <div className="flex items-center space-x-2">
                <RadioGroupItem
                  value={SharedWordbookBundleSortType.CREATED_AT_DESC}
                  id="sort-bd-new"
                />
                <Label
                  htmlFor="sort-bd-new"
                  className="text-sm text-gray-300 cursor-pointer select-none"
                >
                  최신순
                </Label>
              </div>
              <div className="flex items-center space-x-2">
                <RadioGroupItem
                  value={SharedWordbookBundleSortType.SUBSCRIPTION_COUNT_DESC}
                  id="sort-bd-pop"
                />
                <Label
                  htmlFor="sort-bd-pop"
                  className="text-sm text-gray-300 cursor-pointer select-none"
                >
                  인기순
                </Label>
              </div>
            </>
          )}
        </RadioGroup>
      </div>
    </div>
  );
}
