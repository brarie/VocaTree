import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  getAllCategories,
  getPosts,
  createMainCategory,
  createSubCategory,
  updateCategory,
  deleteCategory,
  createPost,
  updatePost,
  deletePost,
} from '@/api/supportBoard';
import {
  CreateMainCategoryRequest,
  CreateSubCategoryRequest,
  UpdateCategoryRequest,
  CreateSupportPostRequest,
  UpdateSupportPostRequest,
} from '@/types/api/supportBoard';
import { ApiError } from '@/types/api/error';
import { toast } from 'sonner';

export const supportBoardKeys = {
  all: ['supportBoard'] as const,
  categories: () => [...supportBoardKeys.all, 'categories'] as const,
  posts: (categoryId: number, page: number, size: number) =>
    [...supportBoardKeys.all, 'posts', categoryId, page, size] as const,
};

export function useCategories() {
  return useQuery({
    queryKey: supportBoardKeys.categories(),
    queryFn: getAllCategories,
  });
}

export function usePosts(
  categoryId: number | null,
  page: number,
  size: number,
) {
  return useQuery({
    queryKey: supportBoardKeys.posts(categoryId || 0, page, size),
    queryFn: () => {
      if (!categoryId) throw new Error('No category selected');
      return getPosts(categoryId, page, size);
    },
    enabled: categoryId !== null,
  });
}

export function useCreateMainCategory() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateMainCategoryRequest) => createMainCategory(data),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: supportBoardKeys.categories(),
      });
      toast.success('대분류 카테고리가 생성되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useCreateSubCategory() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      parentCategoryId,
      data,
    }: {
      parentCategoryId: number;
      data: CreateSubCategoryRequest;
    }) => createSubCategory(parentCategoryId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: supportBoardKeys.categories(),
      });
      toast.success('서브 카테고리가 생성되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useUpdateCategory() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      categoryId,
      data,
    }: {
      categoryId: number;
      data: UpdateCategoryRequest;
    }) => updateCategory(categoryId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: supportBoardKeys.categories(),
      });
      toast.success('카테고리가 수정되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useDeleteCategory() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (categoryId: number) => deleteCategory(categoryId),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: supportBoardKeys.categories(),
      });
      toast.success('카테고리가 삭제되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useCreatePost() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateSupportPostRequest) => createPost(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: supportBoardKeys.all });
      toast.success('게시글이 작성되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useUpdatePost() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      postId,
      data,
    }: {
      postId: number;
      data: UpdateSupportPostRequest;
    }) => updatePost(postId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: supportBoardKeys.all });
      toast.success('게시글이 수정되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useDeletePost() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (postId: number) => deletePost(postId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: supportBoardKeys.all });
      toast.success('게시글이 삭제되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}
