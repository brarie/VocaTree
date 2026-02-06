import { z } from 'zod';

export enum NodeType {
  SINGLE = 'SINGLE',
  BUNDLE = 'BUNDLE',
}

export enum WordbookNodeSortType {
  TITLE_ASC = 'TITLE_ASC',
  TITLE_DESC = 'TITLE_DESC',
  CREATED_AT_ASC = 'CREATED_AT_ASC',
  CREATED_AT_DESC = 'CREATED_AT_DESC',
}

export const CreateWordbookRequestSchema = z.object({
  title: z.string().min(1, '제목을 입력해주세요.'),
});

export type CreateWordbookRequest = z.infer<typeof CreateWordbookRequestSchema>;

export const UpdateWordbookTitleRequestSchema = z.object({
  title: z.string().min(1, '제목을 입력해주세요.'),
});

export type UpdateWordbookTitleRequest = z.infer<
  typeof UpdateWordbookTitleRequestSchema
>;

export interface WordbookNodeResponse {
  id: number;
  title: string;
  isStarred: boolean;
  nodeType: NodeType;
  createdAt: string;
}

export interface NodeResponseWithCountView {
  id: number;
  title: string;
  isStarred: boolean;
  nodeType: NodeType;
  createdAt: string;
  count: number;
}
