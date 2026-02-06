import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  paginateNodes,
  paginateNodesInBundle,
  getWordbook,
  getBundle,
  getAllBundles,
  createBundle,
  updateBundleTitle,
  toggleBundleStarred,
  deleteBundle,
  createWordbook,
  updateWordbookTitle,
  toggleWordbookStarred,
  moveWordbookToRoot,
  moveWordbookToBundle,
  deleteWordbook,
} from '@/api/wordbook';
import {
  CreateWordbookRequest,
  UpdateWordbookTitleRequest,
  WordbookNodeSortType,
} from '@/types/api/wordbook';
import { ApiError } from '@/types/api/error';
import { toast } from 'sonner';

export const wordbookKeys = {
  all: ['wordbook'] as const,
  nodes: (
    page: number,
    size: number,
    sortType: WordbookNodeSortType,
    starOnly: boolean,
  ) => [...wordbookKeys.all, 'nodes', page, size, sortType, starOnly] as const,
  nodesInBundle: (bundleId: number, page: number, size: number) =>
    [...wordbookKeys.all, 'nodesInBundle', bundleId, page, size] as const,
  wordbook: (wordbookId: number) =>
    [...wordbookKeys.all, 'wordbook', wordbookId] as const,
  bundle: (bundleId: number) =>
    [...wordbookKeys.all, 'bundle', bundleId] as const,
  bundles: () => [...wordbookKeys.all, 'bundles'] as const,
};

export function usePaginateNodes(
  page: number,
  size: number,
  sortType: WordbookNodeSortType,
  starOnly: boolean,
) {
  return useQuery({
    queryKey: wordbookKeys.nodes(page, size, sortType, starOnly),
    queryFn: () => paginateNodes(page, size, sortType, starOnly),
  });
}

export function usePaginateNodesInBundle(
  bundleId: number,
  page: number,
  size: number,
) {
  return useQuery({
    queryKey: wordbookKeys.nodesInBundle(bundleId, page, size),
    queryFn: () => paginateNodesInBundle(bundleId, page, size),
    enabled: bundleId > 0,
  });
}

export function useWordbook(wordbookId: number) {
  return useQuery({
    queryKey: wordbookKeys.wordbook(wordbookId),
    queryFn: () => getWordbook(wordbookId),
    enabled: wordbookId > 0,
  });
}

export function useBundle(bundleId: number) {
  return useQuery({
    queryKey: wordbookKeys.bundle(bundleId),
    queryFn: () => getBundle(bundleId),
    enabled: bundleId > 0,
  });
}

export function useAllBundles() {
  return useQuery({
    queryKey: wordbookKeys.bundles(),
    queryFn: getAllBundles,
  });
}

export function useCreateBundle() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (request: CreateWordbookRequest) => createBundle(request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: wordbookKeys.all });
      toast.success('단어장 모음이 생성되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useUpdateBundleTitle() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      bundleId,
      request,
    }: {
      bundleId: number;
      request: UpdateWordbookTitleRequest;
    }) => updateBundleTitle(bundleId, request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: wordbookKeys.all });
      toast.success('단어장 모음 제목이 수정되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useToggleBundleStarred() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (bundleId: number) => toggleBundleStarred(bundleId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: wordbookKeys.all });
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useDeleteBundle() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (bundleId: number) => deleteBundle(bundleId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: wordbookKeys.all });
      toast.success('단어장 모음이 삭제되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useCreateWordbook() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (request: CreateWordbookRequest) => createWordbook(request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: wordbookKeys.all });
      toast.success('단어장이 생성되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useUpdateWordbookTitle() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      wordbookId,
      request,
    }: {
      wordbookId: number;
      request: UpdateWordbookTitleRequest;
    }) => updateWordbookTitle(wordbookId, request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: wordbookKeys.all });
      toast.success('단어장 제목이 수정되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useToggleWordbookStarred() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (wordbookId: number) => toggleWordbookStarred(wordbookId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: wordbookKeys.all });
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useMoveWordbookToRoot() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (wordbookId: number) => moveWordbookToRoot(wordbookId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: wordbookKeys.all });
      toast.success('단어장을 루트로 이동했습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useMoveWordbookToBundle() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      wordbookId,
      bundleId,
    }: {
      wordbookId: number;
      bundleId: number;
    }) => moveWordbookToBundle(wordbookId, bundleId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: wordbookKeys.all });
      toast.success('단어장을 모음으로 이동했습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useDeleteWordbook() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (wordbookId: number) => deleteWordbook(wordbookId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: wordbookKeys.all });
      toast.success('단어장이 삭제되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}
