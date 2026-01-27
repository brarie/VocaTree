import { SupportCategoryResponse } from '@/types/api/supportBoard';
import { cn } from '@/lib/utils';

interface SubCategoryTabsProps {
  parentCategory: SupportCategoryResponse;
  subCategories: SupportCategoryResponse[];
  selectedCategoryId: number | null;
  onSelectCategory: (categoryId: number) => void;
}

export function SubCategoryTabs({
  parentCategory,
  subCategories,
  selectedCategoryId,
  onSelectCategory,
}: SubCategoryTabsProps) {
  return (
    <div className="flex gap-2 overflow-x-auto pb-2">
      <button
        onClick={() => onSelectCategory(parentCategory.id)}
        className={cn(
          'px-4 py-2 rounded-md whitespace-nowrap transition-colors',
          selectedCategoryId === parentCategory.id
            ? 'bg-white text-black'
            : 'bg-white/5 text-gray-300 hover:bg-white/10'
        )}
      >
        전체
      </button>
      {subCategories.map((sub) => (
        <button
          key={sub.id}
          onClick={() => onSelectCategory(sub.id)}
          className={cn(
            'px-4 py-2 rounded-md whitespace-nowrap transition-colors',
            selectedCategoryId === sub.id
              ? 'bg-white text-black'
              : 'bg-white/5 text-gray-300 hover:bg-white/10'
          )}
        >
          {sub.categoryName}
        </button>
      ))}
    </div>
  );
}
