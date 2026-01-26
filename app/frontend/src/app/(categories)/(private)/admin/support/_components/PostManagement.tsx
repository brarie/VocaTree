'use client';

import { useState, useEffect } from 'react';
import {
  SupportCategoryResponse,
  SupportPostView,
} from '@/types/api/supportBoard';
import { useCategories, usePosts } from '@/hooks/supportBoard/queries';
import { usePageParams } from '@/hooks/usePageParams';
import { PaginationBar } from '@/components/PaginationBar';
import { Button } from '@/components/ui/button';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Plus, Edit, Trash2 } from 'lucide-react';
import {
  CreatePostDialog,
  UpdatePostDialog,
  DeletePostDialog,
} from './PostDialog';
import { SupportPostItem } from '@/src/app/(categories)/(public)/support/_components/SupportPostItem';

export function PostManagement() {
  const { data: allCategories, isLoading: categoriesLoading } = useCategories();
  const [selectedCategoryId, setSelectedCategoryId] = useState<number | null>(
    null,
  );

  const { pageParams, setPage, setSize } = usePageParams({ defaultSize: 10 });

  const { data: pageData, isLoading: postsLoading } = usePosts(
    selectedCategoryId,
    pageParams.page,
    pageParams.size,
  );

  const [createDialogOpen, setCreateDialogOpen] = useState(false);
  const [updateDialogOpen, setUpdateDialogOpen] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [selectedPost, setSelectedPost] = useState<SupportPostView | null>(
    null,
  );
  const [expandedPostIds, setExpandedPostIds] = useState<Set<number>>(
    new Set(),
  );

  useEffect(() => {
    if (allCategories && allCategories.length > 0 && !selectedCategoryId) {
      setSelectedCategoryId(allCategories[0].id);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [allCategories]);

  const togglePost = (postId: number) => {
    const newExpanded = new Set(expandedPostIds);
    if (newExpanded.has(postId)) {
      newExpanded.delete(postId);
    } else {
      newExpanded.add(postId);
    }
    setExpandedPostIds(newExpanded);
  };

  const getCategoryDisplayName = (cat: SupportCategoryResponse) => {
    if (!allCategories) return cat.categoryName;
    if (cat.parentCategoryId === null) {
      return cat.categoryName;
    }
    const parent = allCategories.find((c) => c.id === cat.parentCategoryId);
    return parent
      ? `${parent.categoryName} > ${cat.categoryName}`
      : cat.categoryName;
  };

  const loading = categoriesLoading || postsLoading;

  return (
    <div className="bg-white/5 border border-white/10 rounded-lg p-6">
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-xl font-semibold text-white">게시글 관리</h2>
        <div className="flex gap-2 items-center">
          <Select
            value={selectedCategoryId?.toString() || ''}
            onValueChange={(value) => setSelectedCategoryId(parseInt(value))}
          >
            <SelectTrigger className="w-48 bg-white/5 border-white/10 text-white">
              <SelectValue placeholder="카테고리 선택" />
            </SelectTrigger>
            <SelectContent className="bg-black border-white/10">
              {allCategories?.map((cat) => (
                <SelectItem key={cat.id} value={cat.id.toString()}>
                  {getCategoryDisplayName(cat)}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>

          <Button
            onClick={() => setCreateDialogOpen(true)}
            className="bg-white text-black hover:bg-gray-200"
          >
            <Plus className="w-4 h-4 mr-2" />
            게시글 작성
          </Button>
        </div>
      </div>

      {loading ? (
        <div className="text-center py-8 text-gray-400">로딩 중...</div>
      ) : pageData && pageData.content.length > 0 ? (
        <div className="space-y-3">
          {pageData.content.map((post) => (
            <SupportPostItem
              key={post.id}
              post={post}
              isExpanded={expandedPostIds.has(post.id)}
              onToggle={() => togglePost(post.id)}
              actions={
                <div className="flex gap-2">
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={() => {
                      setSelectedPost(post);
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
                      setSelectedPost(post);
                      setDeleteDialogOpen(true);
                    }}
                    className="bg-white/5 border-white/10 text-red-400 hover:bg-red-500/10"
                  >
                    <Trash2 className="w-3 h-3" />
                  </Button>
                </div>
              }
            />
          ))}

          {pageData.totalElements > 0 && (
            <PaginationBar
              pageData={pageData}
              onPageChange={setPage}
              onItemsPerPageChange={setSize}
              className="pt-2"
            />
          )}
        </div>
      ) : (
        <div className="text-center py-8 text-gray-500">게시글이 없습니다.</div>
      )}

      {allCategories && (
        <CreatePostDialog
          open={createDialogOpen}
          onOpenChange={setCreateDialogOpen}
          categories={allCategories}
        />
      )}

      {selectedPost && allCategories && (
        <>
          <UpdatePostDialog
            open={updateDialogOpen}
            onOpenChange={setUpdateDialogOpen}
            post={selectedPost}
            categories={allCategories}
          />

          <DeletePostDialog
            open={deleteDialogOpen}
            onOpenChange={setDeleteDialogOpen}
            post={selectedPost}
          />
        </>
      )}
    </div>
  );
}
