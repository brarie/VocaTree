import { z } from 'zod';
import { ExampleResponse, SenseResponse } from '@/types/api/common/vocabulary';

// ==========================================
// Enums
// ==========================================

export const SharedState = {
  PUBLIC: 'PUBLIC',
  DELETED_BY_OWNER: 'DELETED_BY_OWNER',
  BANNED: 'BANNED',
} as const;
export type SharedState = (typeof SharedState)[keyof typeof SharedState];

export const TrustedLevel = {
  OFFICIAL: 'OFFICIAL',
  VERIFIED: 'VERIFIED',
  BASIC: 'BASIC',
} as const;
export type TrustedLevel = (typeof TrustedLevel)[keyof typeof TrustedLevel];

export const SharedWordbookSortType = {
  CREATED_AT_DESC: 'CREATED_AT_DESC',
  SUBSCRIPTION_COUNT_DESC: 'SUBSCRIPTION_COUNT_DESC',
} as const;
export type SharedWordbookSortType =
  (typeof SharedWordbookSortType)[keyof typeof SharedWordbookSortType];

export const SharedWordbookBundleSortType = {
  CREATED_AT_DESC: 'CREATED_AT_DESC',
  SUBSCRIPTION_COUNT_DESC: 'SUBSCRIPTION_COUNT_DESC',
} as const;
export type SharedWordbookBundleSortType =
  (typeof SharedWordbookBundleSortType)[keyof typeof SharedWordbookBundleSortType];

// ==========================================
// Common Views (Users)
// ==========================================

export interface SimpleUserView {
  id: string;
  username: string;
  nickname: string;
}

// ==========================================
// Shared Wordbook Views (List & Detail)
// ==========================================

export interface SharedWordbookListView {
  id: number;
  title: string;
  slug: string;
  sharedState: SharedState;
  trustedLevel: TrustedLevel;
  creator: SimpleUserView;
  latestVersion: number;
  createdAt: string; // ISO DateTime
}

export interface SharedWordbookDetailView {
  id: number;
  title: string;
  slug: string;
  description: string | null;
  sharedState: SharedState;
  trustedLevel: TrustedLevel;
  creator: SimpleUserView;
  createdAt: string; // ISO DateTime
  latestVersion: number;
  latestRevisionId: number;
}

export interface SharedWordbookVersionView {
  id: number;
  sharedWordbookId: number;
  version: number;
  versionNote: string | null;
  wordCount: number;
  revisedAt: string; // ISO DateTime
}

// ==========================================
// Shared Wordbook Response DTOs
// ==========================================

export interface SharedWordbookResponse {
  id: number;
  title: string;
  slug: string | null;
  description: string | null;
  sharedState: SharedState;
  trustedLevel: TrustedLevel;
  creatorId: string;
  createdAt: string;
}

export interface SharedWordbookRevisionResponse {
  id: number;
  version: number;
  versionNote: string | null;
  revisedAt: string;
}

// ==========================================
// Shared Word Snapshot
// ==========================================

export interface SharedWordSnapshotResponse {
  id: number;
  spelling: string | null;
  displayOrder: number;
  senses: SharedSenseSnapshotResponse[];
}

export interface SharedSenseSnapshotResponse {
  sense: SenseResponse;
  displayOrder: number;
  examples: SharedExampleSnapshotResponse[];
}

export interface SharedExampleSnapshotResponse {
  example: ExampleResponse;
  displayOrder: number;
}

// ==========================================
// Requests (Zod Schemas) - Wordbook
// ==========================================

// NOTE: 백엔드 DTO에 별도 Validation Annotation이 없으므로, 타입만 맞추고 임의의 제약(min, max 등)은 제거함.

export const ShareMyWordbookRequestSchema = z.object({
  title: z.string(),
  description: z.string().nullable().optional(), // Kotlin: String?
  myWordbookId: z.number(), // Kotlin: Long
  versionNote: z.string(),
});
export type ShareMyWordbookRequest = z.infer<
  typeof ShareMyWordbookRequestSchema
>;

export const UpdateSharedWordbookMetadataRequestSchema = z.object({
  title: z.string(),
  description: z.string().nullable().optional(),
});
export type UpdateSharedWordbookMetadataRequest = z.infer<
  typeof UpdateSharedWordbookMetadataRequestSchema
>;

export const MakeNewVersionRequestSchema = z.object({
  myWordbookId: z.number(),
  versionNote: z.string(),
});
export type MakeNewVersionRequest = z.infer<typeof MakeNewVersionRequestSchema>;

export const UpdateTrustedLevelRequestSchema = z.object({
  trustedLevel: z.enum([
    TrustedLevel.OFFICIAL,
    TrustedLevel.VERIFIED,
    TrustedLevel.BASIC,
  ]),
});
export type UpdateTrustedLevelRequest = z.infer<
  typeof UpdateTrustedLevelRequestSchema
>;

// ==========================================
// Shared Wordbook Bundle Views
// ==========================================

export interface SharedWordbookBundleListView {
  id: number;
  title: string;
  slug: string;
  sharedState: SharedState;
  trustedLevel: TrustedLevel;
  creator: SimpleUserView;
  wordbookCount: number;
  createdAt: string;
}

export interface SharedWordbookBundleDetailView {
  id: number;
  title: string;
  slug: string;
  description: string | null;
  sharedState: SharedState;
  trustedLevel: TrustedLevel;
  creator: SimpleUserView;
  createdAt: string;
  wordbookCount: number;
}

// ==========================================
// Shared Wordbook Bundle Response DTOs
// ==========================================

export interface SharedWordbookBundleResponse {
  id: number;
  title: string;
  slug: string | null;
  description: string | null;
  sharedState: SharedState;
  trustedLevel: TrustedLevel;
  creatorId: string;
  createdAt: string;
}

// ==========================================
// Requests (Zod Schemas) - Bundle
// ==========================================

export const MakeSharedBundleRequestSchema = z.object({
  title: z.string(),
  description: z.string().nullable().optional(),
});
export type MakeSharedBundleRequest = z.infer<
  typeof MakeSharedBundleRequestSchema
>;

export const UpdateSharedBundleMetadataRequestSchema = z.object({
  title: z.string(),
  description: z.string().nullable().optional(),
});
export type UpdateSharedBundleMetadataRequest = z.infer<
  typeof UpdateSharedBundleMetadataRequestSchema
>;

export const UpdateBundleTrustedLevelRequestSchema = z.object({
  trustedLevel: z.enum([
    TrustedLevel.OFFICIAL,
    TrustedLevel.VERIFIED,
    TrustedLevel.BASIC,
  ]),
});
export type UpdateBundleTrustedLevelRequest = z.infer<
  typeof UpdateBundleTrustedLevelRequestSchema
>;
