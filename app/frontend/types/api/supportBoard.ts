import { z } from 'zod';

export const CreateMainCategoryRequestSchema = z.object({
  categoryName: z.string().min(1, '카테고리 이름을 입력해주세요.'),
});

export const CreateSubCategoryRequestSchema = z.object({
  categoryName: z.string().min(1, '카테고리 이름을 입력해주세요.'),
});

export const UpdateCategoryRequestSchema = z.object({
  categoryName: z.string().min(1, '카테고리 이름을 입력해주세요.'),
});

export const CreateSupportPostRequestSchema = z.object({
  title: z.string().min(1, '제목을 입력해주세요.'),
  content: z.string().min(1, '내용을 입력해주세요.'),
  categoryId: z.number(),
});

export const UpdateSupportPostRequestSchema = z.object({
  title: z.string().min(1, '제목을 입력해주세요.'),
  content: z.string().min(1, '내용을 입력해주세요.'),
  categoryId: z.number(),
});

export type CreateMainCategoryRequest = z.infer<
  typeof CreateMainCategoryRequestSchema
>;
export type CreateSubCategoryRequest = z.infer<
  typeof CreateSubCategoryRequestSchema
>;
export type UpdateCategoryRequest = z.infer<typeof UpdateCategoryRequestSchema>;
export type CreateSupportPostRequest = z.infer<
  typeof CreateSupportPostRequestSchema
>;
export type UpdateSupportPostRequest = z.infer<
  typeof UpdateSupportPostRequestSchema
>;

export interface SupportCategoryResponse {
  id: number;
  categoryName: string;
  parentCategoryId: number | null;
}

export interface SimpleUserView {
  id: string;
  nickname: string;
}

export interface SupportPostView {
  id: number;
  title: string;
  content: string;
  author: SimpleUserView;
  supportCategoryId: number;
  supportCategoryName: string;
  createdAt: string;
}

export interface SupportPostResponse {
  id: number;
  title: string;
  content: string;
  authorId: string;
  createdAt: string;
  categoryId: number;
  categoryName: string;
  parentCategoryId: number | null;
  parentCategoryName: string | null;
}
