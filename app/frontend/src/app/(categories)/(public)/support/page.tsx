'use client';

import { useState, useEffect, useMemo, Suspense } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import Link from 'next/link';
import { Settings } from 'lucide-react';
import { useCategories, usePosts } from '@/hooks/supportBoard/queries';
import { usePageParams } from '@/hooks/usePageParams';
import { PageHeader } from '@/components/PageHeader';
import { PaginationBar } from '@/components/PaginationBar';
import { Button } from '@/components/ui/button';
import { AuthOnly } from '@/components/auth/AuthOnly';
import { CategorySidebar } from './_components/CategorySidebar';
import { SubCategoryTabs } from './_components/SubCategoryTabs';
import { SupportPostItem } from './_components/SupportPostItem';

function SupportPageContent() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const selectedCategoryId = searchParams.get('category');

  const [expandedPostIds, setExpandedPostIds] = useState<Set<number>>(
    new Set(),
  );

  const { pageParams, setPage, setSize } = usePageParams({ defaultSize: 10 });

  const { data: allCategories = [], isLoading: categoriesLoading } =
    useCategories();
  const mainCategories = useMemo(
    () => allCategories.filter((c) => c.parentCategoryId === null),
    [allCategories],
  );

  const categoryId = selectedCategoryId ? parseInt(selectedCategoryId) : null;
  const { data: pageData, isLoading: postsLoading } = usePosts(
    categoryId,
    pageParams.page,
    pageParams.size,
  );

  const parentCategoryId = useMemo(() => {
    if (!categoryId) return null;
    const selectedCategory = allCategories.find((c) => c.id === categoryId);
    if (!selectedCategory) return null;
    return selectedCategory.parentCategoryId ?? categoryId;
  }, [categoryId, allCategories]);

  const subCategories = useMemo(() => {
    if (!parentCategoryId) return [];
    return allCategories.filter((c) => c.parentCategoryId === parentCategoryId);
  }, [parentCategoryId, allCategories]);

  useEffect(() => {
    if (!selectedCategoryId && mainCategories.length > 0) {
      router.push(`/support?category=${mainCategories[0].id}`);
    }
  }, [mainCategories, selectedCategoryId, router]);

  const handleCategorySelect = (categoryId: number) => {
    router.push(`/support?category=${categoryId}`);
    setExpandedPostIds(new Set());
  };

  const togglePost = (postId: number) => {
    const newExpanded = new Set(expandedPostIds);
    if (newExpanded.has(postId)) {
      newExpanded.delete(postId);
    } else {
      newExpanded.add(postId);
    }
    setExpandedPostIds(newExpanded);
  };

  const parentCategory = useMemo(
    () =>
      parentCategoryId
        ? allCategories.find((c) => c.id === parentCategoryId)
        : null,
    [parentCategoryId, allCategories],
  );

  if (mainCategories.length === 0 && !categoriesLoading) {
    return (
      <div className="text-center py-12 space-y-4">
        <p className="text-gray-500">아직 카테고리가 없습니다.</p>
        <AuthOnly roles={['ADMIN']}>
          <Button asChild className="bg-white text-black hover:bg-gray-200">
            <Link href="/admin/support">
              <Settings className="w-4 h-4 mr-2" />
              관리 페이지에서 카테고리 추가하기
            </Link>
          </Button>
        </AuthOnly>
      </div>
    );
  }

  return (
    <div>
      <div className="flex items-start justify-between gap-4 mb-3">
        <div className="flex-1 min-w-0">
          <PageHeader
            title="도움마당"
            description="궁금한 게 있거나 도움이 필요할 땐 여기서 확인해보세요."
          >
            <AuthOnly roles={['ADMIN']}>
              <Button
                asChild
                variant="outline"
                className="items-center bg-white/5 border-white/10 text-white hover:bg-white/10 mt-3"
              >
                <Link href="/admin/support">
                  <Settings className="w-4 h-4 mr-2" />
                  관리
                </Link>
              </Button>
            </AuthOnly>
          </PageHeader>
        </div>
      </div>

      <div className="flex flex-col md:flex-row gap-6">
        <CategorySidebar
          categories={mainCategories}
          selectedCategoryId={categoryId}
          parentCategoryId={parentCategoryId}
          onSelectCategory={handleCategorySelect}
        />

        <div className="flex-1 min-w-0 space-y-4">
          {parentCategory && subCategories.length > 0 && (
            <SubCategoryTabs
              parentCategory={parentCategory}
              subCategories={subCategories}
              selectedCategoryId={categoryId}
              onSelectCategory={handleCategorySelect}
            />
          )}

          {postsLoading ? (
            <div className="text-center py-12 text-gray-400">로딩 중...</div>
          ) : pageData && pageData.content.length > 0 ? (
            <div className="space-y-3">
              {pageData.content.map((post) => (
                <SupportPostItem
                  key={post.id}
                  post={post}
                  isExpanded={expandedPostIds.has(post.id)}
                  onToggle={() => togglePost(post.id)}
                />
              ))}
            </div>
          ) : (
            <div className="text-center py-12 text-gray-500">
              게시글이 없습니다.
            </div>
          )}

          {pageData && pageData.totalElements > 0 && (
            <PaginationBar
              pageData={pageData}
              onPageChange={setPage}
              onItemsPerPageChange={setSize}
              className="pt-2"
            />
          )}
        </div>
      </div>
    </div>
  );
}

export default function SupportPage() {
  return (
    <Suspense
      fallback={
        <div className="text-center py-12 text-gray-400">로딩 중...</div>
      }
    >
      <SupportPageContent />
    </Suspense>
  );
}
