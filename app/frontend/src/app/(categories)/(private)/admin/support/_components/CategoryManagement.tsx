'use client';

import { useState, useMemo } from 'react';
import { useCategories } from '@/hooks/supportBoard/queries';
import { SupportCategoryResponse } from '@/types/api/supportBoard';
import { Button } from '@/components/ui/button';
import { Plus, Edit, Trash2, ChevronDown, ChevronRight } from 'lucide-react';
import {
  CreateMainCategoryDialog,
  CreateSubCategoryDialog,
  UpdateCategoryDialog,
  DeleteCategoryDialog,
} from './CategoryDialog';

export function CategoryManagement() {
  const { data: allCategories, isLoading } = useCategories();

  const [expandedCategories, setExpandedCategories] = useState<Set<number>>(
    new Set(),
  );

  const [createMainDialogOpen, setCreateMainDialogOpen] = useState(false);
  const [createSubDialogOpen, setCreateSubDialogOpen] = useState(false);
  const [updateDialogOpen, setUpdateDialogOpen] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [selectedCategory, setSelectedCategory] =
    useState<SupportCategoryResponse | null>(null);
  const [parentCategoryIdForSub, setParentCategoryIdForSub] = useState<
    number | null
  >(null);

  const mainCategories = useMemo(
    () => allCategories?.filter((cat) => cat.parentCategoryId === null) || [],
    [allCategories],
  );

  const getSubCategories = (parentId: number) =>
    allCategories?.filter((cat) => cat.parentCategoryId === parentId) || [];

  const toggleCategory = (categoryId: number) => {
    const newExpanded = new Set(expandedCategories);
    if (newExpanded.has(categoryId)) {
      newExpanded.delete(categoryId);
    } else {
      newExpanded.add(categoryId);
    }
    setExpandedCategories(newExpanded);
  };

  return (
    <div className="bg-white/5 border border-white/10 rounded-lg p-6">
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-xl font-semibold text-white">카테고리 관리</h2>
        <Button
          onClick={() => setCreateMainDialogOpen(true)}
          className="bg-white text-black hover:bg-gray-200"
        >
          <Plus className="w-4 h-4 mr-2" />
          대분류 추가
        </Button>
      </div>

      {isLoading ? (
        <div className="text-center py-8 text-gray-400">로딩 중...</div>
      ) : (
        <div className="space-y-2">
          {mainCategories.map((mainCategory) => (
            <div key={mainCategory.id} className="bg-white/5 rounded-lg">
              <div
                className="flex items-center gap-2 px-4 py-3 cursor-pointer hover:bg-white/5"
                onClick={() => toggleCategory(mainCategory.id)}
              >
                <div className="text-gray-400">
                  {expandedCategories.has(mainCategory.id) ? (
                    <ChevronDown className="w-4 h-4" />
                  ) : (
                    <ChevronRight className="w-4 h-4" />
                  )}
                </div>
                <span className="flex-1 text-white">
                  {mainCategory.categoryName}
                </span>
                <div
                  className="flex gap-2"
                  onClick={(e) => e.stopPropagation()}
                >
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={() => {
                      setParentCategoryIdForSub(mainCategory.id);
                      setCreateSubDialogOpen(true);
                    }}
                    className="bg-white/5 border-white/10 text-white hover:bg-white/10"
                  >
                    <Plus className="w-3 h-3 mr-1" />
                    서브
                  </Button>
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={() => {
                      setSelectedCategory(mainCategory);
                      setUpdateDialogOpen(true);
                    }}
                    className="bg-white/5 border-white/10 text-white hover:bg-white/10"
                  >
                    <Edit className="w-3 h-3" />
                  </Button>
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={() => {
                      setSelectedCategory(mainCategory);
                      setDeleteDialogOpen(true);
                    }}
                    className="bg-white/5 border-white/10 text-red-400 hover:bg-red-500/10"
                  >
                    <Trash2 className="w-3 h-3" />
                  </Button>
                </div>
              </div>

              {expandedCategories.has(mainCategory.id) && (
                <div className="pl-12 pr-4 pb-3 space-y-2 pt-3 border-t">
                  {getSubCategories(mainCategory.id).map((subCategory) => (
                    <div
                      key={subCategory.id}
                      className="flex items-center gap-2 px-4 py-2 bg-white/5 rounded-md"
                    >
                      <span className="flex-1 text-gray-300">
                        {subCategory.categoryName}
                      </span>
                      <div className="flex gap-2">
                        <Button
                          size="sm"
                          variant="outline"
                          onClick={() => {
                            setSelectedCategory(subCategory);
                            setUpdateDialogOpen(true);
                          }}
                          className="bg-white/5 border-white/10 text-white hover:bg-white/10"
                        >
                          <Edit className="w-3 h-3" />
                        </Button>
                        <Button
                          size="sm"
                          variant="outline"
                          onClick={() => {
                            setSelectedCategory(subCategory);
                            setDeleteDialogOpen(true);
                          }}
                          className="bg-white/5 border-white/10 text-red-400 hover:bg-red-500/10"
                        >
                          <Trash2 className="w-3 h-3" />
                        </Button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          ))}
        </div>
      )}

      <CreateMainCategoryDialog
        open={createMainDialogOpen}
        onOpenChange={setCreateMainDialogOpen}
      />

      {parentCategoryIdForSub && (
        <CreateSubCategoryDialog
          open={createSubDialogOpen}
          onOpenChange={setCreateSubDialogOpen}
          parentCategoryId={parentCategoryIdForSub}
        />
      )}

      {selectedCategory && (
        <>
          <UpdateCategoryDialog
            open={updateDialogOpen}
            onOpenChange={setUpdateDialogOpen}
            category={selectedCategory}
          />

          <DeleteCategoryDialog
            open={deleteDialogOpen}
            onOpenChange={setDeleteDialogOpen}
            category={selectedCategory}
          />
        </>
      )}
    </div>
  );
}
