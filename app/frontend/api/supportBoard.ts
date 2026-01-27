import { fetcher, fetcherWithAuth } from '@/lib/fetcher';
import { handleApiResponse } from '@/lib/error';
import { Page } from '@/types/page';
import {
  CreateMainCategoryRequest,
  CreateSubCategoryRequest,
  UpdateCategoryRequest,
  CreateSupportPostRequest,
  UpdateSupportPostRequest,
  SupportCategoryResponse,
  SupportPostResponse,
  SupportPostView,
} from '@/types/api/supportBoard';

export async function getAllCategories(): Promise<SupportCategoryResponse[]> {
  const response = await fetcher('/api/support-categories/all');
  await handleApiResponse(response, 'Failed to fetch all categories');

  const data: SupportCategoryResponse[] = await response.json();
  return data;
}

export async function getMainCategories(): Promise<SupportCategoryResponse[]> {
  const response = await fetcher('/api/support-categories');
  await handleApiResponse(response, 'Failed to fetch main categories');

  const data: SupportCategoryResponse[] = await response.json();
  return data;
}

export async function getSubCategories(
  categoryId: number,
): Promise<SupportCategoryResponse[]> {
  const response = await fetcher(
    `/api/support-categories/${categoryId}/sub-categories`,
  );
  await handleApiResponse(response, 'Failed to fetch sub categories');

  const data: SupportCategoryResponse[] = await response.json();
  return data;
}

export async function createMainCategory(
  data: CreateMainCategoryRequest,
): Promise<SupportCategoryResponse> {
  const response = await fetcherWithAuth('/api/support-categories', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });

  await handleApiResponse(response, 'Failed to create main category');

  const result: SupportCategoryResponse = await response.json();
  return result;
}

export async function createSubCategory(
  parentCategoryId: number,
  data: CreateSubCategoryRequest,
): Promise<SupportCategoryResponse> {
  const response = await fetcherWithAuth(
    `/api/support-categories/${parentCategoryId}/sub-categories`,
    {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    },
  );

  await handleApiResponse(response, 'Failed to create sub category');

  const result: SupportCategoryResponse = await response.json();
  return result;
}

export async function updateCategory(
  categoryId: number,
  data: UpdateCategoryRequest,
): Promise<SupportCategoryResponse> {
  const response = await fetcherWithAuth(
    `/api/support-categories/${categoryId}`,
    {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    },
  );

  await handleApiResponse(response, 'Failed to update category');

  const result: SupportCategoryResponse = await response.json();
  return result;
}

export async function deleteCategory(categoryId: number): Promise<void> {
  const response = await fetcherWithAuth(
    `/api/support-categories/${categoryId}`,
    {
      method: 'DELETE',
    },
  );

  await handleApiResponse(response, 'Failed to delete category');
}

export async function getPosts(
  categoryId: number,
  page: number,
  size: number,
): Promise<Page<SupportPostView>> {
  const response = await fetcher(
    `/api/support-posts?categoryId=${categoryId}&page=${page}&size=${size}`,
  );
  await handleApiResponse(response, 'Failed to fetch posts');

  const data: Page<SupportPostView> = await response.json();
  return data;
}

export async function createPost(
  data: CreateSupportPostRequest,
): Promise<SupportPostResponse> {
  const response = await fetcherWithAuth('/api/support-posts', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });

  await handleApiResponse(response, 'Failed to create post');

  const result: SupportPostResponse = await response.json();
  return result;
}

export async function updatePost(
  postId: number,
  data: UpdateSupportPostRequest,
): Promise<SupportPostResponse> {
  const response = await fetcherWithAuth(`/api/support-posts/${postId}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });

  await handleApiResponse(response, 'Failed to update post');

  const result: SupportPostResponse = await response.json();
  return result;
}

export async function deletePost(postId: number): Promise<void> {
  const response = await fetcherWithAuth(`/api/support-posts/${postId}`, {
    method: 'DELETE',
  });

  await handleApiResponse(response, 'Failed to delete post');
}
