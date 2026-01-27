import { SupportCategoryResponse } from '@/types/api/supportBoard';
import { cn } from '@/lib/utils';

interface CategorySidebarProps {
  categories: SupportCategoryResponse[];
  selectedCategoryId: number | null;
  parentCategoryId: number | null;
  onSelectCategory: (categoryId: number) => void;
}

export function CategorySidebar({
  categories,
  selectedCategoryId,
  parentCategoryId,
  onSelectCategory,
}: CategorySidebarProps) {
  return (
    <div className="w-full md:w-64 md:self-start bg-white/5 border border-white/10 rounded-lg p-4">
      <h2 className="text-lg font-semibold text-white mb-4">카테고리</h2>
      <div className="space-y-1">
        {categories.map((category) => (
          <button
            key={category.id}
            onClick={() => onSelectCategory(category.id)}
            className={cn(
              'w-full text-left px-3 py-2 rounded-md transition-colors',
              parentCategoryId === category.id
                ? 'bg-white text-black'
                : 'text-gray-300 hover:bg-white/10',
            )}
          >
            {category.categoryName}
          </button>
        ))}
      </div>
    </div>
  );
}
