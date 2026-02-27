import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import { ApiError } from '@/types/api/error';
import {
  getSharedWordbooks,
  getMySharedWordbooks,
  getMySharedBundles,
  getSharedWordbookDetail,
  getSharedWordbookVersions,
  getSharedWordsByVersion,
  shareOwnWordbook,
  updateSharedWordbookMetadata,
  makeNewVersion,
  deleteMySharedWordbook,
  banSharedWordbook,
  updateTrustedLevel,
  getSharedBundles,
  getSharedBundleDetail,
  getWordbooksInBundle,
  makeSharedBundle,
  updateSharedBundleMetadata,
  deleteSharedBundle,
  addWordbookToBundle,
  removeWordbookFromBundle,
  banSharedBundle,
  updateTrustedLevelBundle,
  extractIdFromSlug,
} from '@/api/library';
import {
  ShareMyWordbookRequest,
  UpdateSharedWordbookMetadataRequest,
  MakeNewVersionRequest,
  UpdateTrustedLevelRequest,
  TrustedLevel,
  SharedWordbookSortType,
  SharedWordbookBundleSortType,
  MakeSharedBundleRequest,
  UpdateSharedBundleMetadataRequest,
  UpdateBundleTrustedLevelRequest,
} from '@/types/api/library';

// ==========================================
// Query Keys
// ==========================================

export const libraryKeys = {
  all: ['library'] as const,
  // Shared Wordbooks
  wordbooks: (
    page: number,
    size: number,
    titlePrefix?: string,
    trustedLevels?: TrustedLevel[],
    sortType?: SharedWordbookSortType,
  ) =>
    [
      ...libraryKeys.all,
      'wordbooks',
      page,
      size,
      titlePrefix,
      trustedLevels,
      sortType,
    ] as const,
  wordbookDetail: (id: number) => [...libraryKeys.all, 'wordbook', id] as const,
  wordbookVersions: (id: number, page: number, size: number) =>
    [...libraryKeys.all, 'wordbook', id, 'versions', page, size] as const,
  wordbookWords: (id: number, version: number) =>
    [...libraryKeys.all, 'wordbook', id, 'version', version, 'words'] as const,

  myWordbooks: (page: number, size: number) =>
    [...libraryKeys.all, 'myWordbooks', page, size] as const,
  myBundles: (page: number, size: number) =>
    [...libraryKeys.all, 'myBundles', page, size] as const,

  // Shared Bundles
  bundles: (
    page: number,
    size: number,
    titlePrefix?: string,
    trustedLevels?: TrustedLevel[],
    sortType?: SharedWordbookBundleSortType,
  ) =>
    [
      ...libraryKeys.all,
      'bundles',
      page,
      size,
      titlePrefix,
      trustedLevels,
      sortType,
    ] as const,
  bundleDetail: (id: number) => [...libraryKeys.all, 'bundle', id] as const,
  bundleWordbooks: (id: number, page: number, size: number) =>
    [...libraryKeys.all, 'bundle', id, 'wordbooks', page, size] as const,
};

// ==========================================
// Shared Wordbook Hooks
// ==========================================

export function useSharedWordbooks(
  page: number,
  size: number,
  titlePrefix?: string,
  trustedLevels?: TrustedLevel[],
  sortType?: SharedWordbookSortType,
  enabled: boolean = true,
) {
  return useQuery({
    queryKey: libraryKeys.wordbooks(
      page,
      size,
      titlePrefix,
      trustedLevels,
      sortType,
    ),
    queryFn: () =>
      getSharedWordbooks({ page, size }, titlePrefix, trustedLevels, sortType),
    enabled,
  });
}

export function useSharedWordbookDetail(slug: string) {
  const id = extractIdFromSlug(slug);
  return useQuery({
    queryKey: libraryKeys.wordbookDetail(id ?? 0),
    queryFn: () => getSharedWordbookDetail(slug),
    enabled: id !== null,
  });
}

export function useSharedWordbookVersions(
  id: number,
  page: number,
  size: number,
  enabled: boolean = true,
) {
  return useQuery({
    queryKey: libraryKeys.wordbookVersions(id, page, size),
    queryFn: () => getSharedWordbookVersions(id, { page, size }),
    enabled: !!id && enabled,
  });
}

export function useSharedWordsByVersion(id: number, version: number) {
  return useQuery({
    queryKey: libraryKeys.wordbookWords(id, version),
    queryFn: () => getSharedWordsByVersion(id, version),
    enabled: !!id && !!version,
  });
}

export function useMySharedWordbooks(page: number, size: number) {
  return useQuery({
    queryKey: libraryKeys.myWordbooks(page, size),
    queryFn: () => getMySharedWordbooks({ page, size }),
  });
}

export function useMySharedBundles(page: number, size: number) {
  return useQuery({
    queryKey: libraryKeys.myBundles(page, size),
    queryFn: () => getMySharedBundles({ page, size }),
  });
}

export function useShareOwnWordbook() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: ShareMyWordbookRequest) => shareOwnWordbook(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: libraryKeys.all });
      toast.success('단어장이 공유되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useUpdateSharedWordbookMetadata() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      id,
      data,
    }: {
      id: number;
      data: UpdateSharedWordbookMetadataRequest;
    }) => updateSharedWordbookMetadata(id, data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({
        queryKey: libraryKeys.wordbookDetail(variables.id),
      });
      queryClient.invalidateQueries({
        queryKey: ['library', 'wordbooks'], // Invalidate list
      });
      toast.success('단어장 정보가 수정되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useMakeNewVersion() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: MakeNewVersionRequest }) =>
      makeNewVersion(id, data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({
        queryKey: libraryKeys.wordbookDetail(variables.id),
      });
      queryClient.invalidateQueries({
        queryKey: ['library', 'wordbook', variables.id, 'versions'],
      });
      queryClient.invalidateQueries({
        queryKey: [...libraryKeys.all, 'myWordbooks'],
      });
      toast.success('새 버전이 업데이트되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useDeleteMySharedWordbook() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: number) => deleteMySharedWordbook(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: libraryKeys.all });
      toast.success('공유 단어장이 삭제되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

// ADMIN Hooks
export function useBanSharedWordbook() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: number) => banSharedWordbook(id),
    onSuccess: (_, id) => {
      queryClient.invalidateQueries({
        queryKey: libraryKeys.wordbookDetail(id),
      });
      toast.success('단어장이 차단되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useUpdateTrustedLevel() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      id,
      data,
    }: {
      id: number;
      data: UpdateTrustedLevelRequest;
    }) => updateTrustedLevel(id, data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({
        queryKey: libraryKeys.wordbookDetail(variables.id),
      });
      toast.success('신뢰 등급이 변경되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

// ==========================================
// Shared Bundle Hooks
// ==========================================

export function useSharedBundles(
  page: number,
  size: number,
  titlePrefix?: string,
  trustedLevels?: TrustedLevel[],
  sortType?: SharedWordbookBundleSortType,
  enabled: boolean = true,
) {
  return useQuery({
    queryKey: libraryKeys.bundles(
      page,
      size,
      titlePrefix,
      trustedLevels,
      sortType,
    ),
    queryFn: () =>
      getSharedBundles({ page, size }, titlePrefix, trustedLevels, sortType),
    enabled,
  });
}

export function useSharedBundleDetail(slug: string) {
  const id = extractIdFromSlug(slug);
  return useQuery({
    queryKey: libraryKeys.bundleDetail(id ?? 0),
    queryFn: () => getSharedBundleDetail(slug),
    enabled: id !== null,
  });
}

export function useWordbooksInBundle(
  id: number,
  page: number,
  size: number,
  enabled: boolean = true,
) {
  return useQuery({
    queryKey: libraryKeys.bundleWordbooks(id, page, size),
    queryFn: () => getWordbooksInBundle(id, { page, size }),
    enabled: !!id && enabled,
  });
}

export function useMakeSharedBundle() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: MakeSharedBundleRequest) => makeSharedBundle(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: libraryKeys.all });
      toast.success('번들이 생성되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useUpdateSharedBundleMetadata() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      id,
      data,
    }: {
      id: number;
      data: UpdateSharedBundleMetadataRequest;
    }) => updateSharedBundleMetadata(id, data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({
        queryKey: libraryKeys.bundleDetail(variables.id),
      });
      queryClient.invalidateQueries({
        queryKey: ['library', 'bundles'],
      });
      toast.success('번들 정보가 수정되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useDeleteSharedBundle() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: number) => deleteSharedBundle(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: libraryKeys.all });
      toast.success('번들이 삭제되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useAddWordbookToBundle() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      bundleId,
      sharedWordbookId,
    }: {
      bundleId: number;
      sharedWordbookId: number;
    }) => addWordbookToBundle(bundleId, sharedWordbookId),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({
        queryKey: libraryKeys.bundleDetail(variables.bundleId),
      });
      queryClient.invalidateQueries({
        queryKey: ['library', 'bundle', variables.bundleId, 'wordbooks'],
      });
      toast.success('단어장이 번들에 추가되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useRemoveWordbookFromBundle() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      bundleId,
      sharedWordbookId,
    }: {
      bundleId: number;
      sharedWordbookId: number;
    }) => removeWordbookFromBundle(bundleId, sharedWordbookId),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({
        queryKey: libraryKeys.bundleDetail(variables.bundleId),
      });
      queryClient.invalidateQueries({
        queryKey: ['library', 'bundle', variables.bundleId, 'wordbooks'],
      });
      toast.success('단어장이 번들에서 제거되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

// ADMIN Bundle Hooks
export function useBanSharedBundle() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: number) => banSharedBundle(id),
    onSuccess: (_, id) => {
      queryClient.invalidateQueries({ queryKey: libraryKeys.bundleDetail(id) });
      toast.success('번들이 차단되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useUpdateBundleTrustedLevel() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      id,
      data,
    }: {
      id: number;
      data: UpdateBundleTrustedLevelRequest;
    }) => updateTrustedLevelBundle(id, data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({
        queryKey: libraryKeys.bundleDetail(variables.id),
      });
      toast.success('번들 신뢰 등급이 변경되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}
