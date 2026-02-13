import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  searchAutocomplete,
  searchWords,
  getWordBySlug,
  getSearchHistory,
  deleteAllSearchHistory,
  deleteSearchHistory,
  createWord,
  updateWord,
  deleteWord,
  createSense,
  updateSense,
  deleteSense,
  reorderSenses,
  createExample,
  updateExample,
  deleteExample,
  reorderExamples,
  getDictionaryCounts,
  getAllWords,
} from '@/api/dictionary';
import {
  CreateWordRequest,
  UpdateWordRequest,
  CreateSenseRequest,
  UpdateSenseRequest,
  CreateExampleRequest,
  UpdateExampleRequest,
  ReorderRequest,
} from '@/types/api/dictionary';
import { ApiError } from '@/types/api/error';
import { toast } from 'sonner';

export const dictionaryKeys = {
  all: ['dictionary'] as const,
  autocomplete: (query: string, limit: number) =>
    [...dictionaryKeys.all, 'autocomplete', query, limit] as const,
  search: (query: string, page: number, size: number) =>
    [...dictionaryKeys.all, 'search', query, page, size] as const,
  word: (slug: string) => [...dictionaryKeys.all, 'word', slug] as const,
  searchHistory: () => [...dictionaryKeys.all, 'searchHistory'] as const,
  counts: () => [...dictionaryKeys.all, 'counts'] as const,
  allWords: (page: number, size: number) =>
    [...dictionaryKeys.all, 'allWords', page, size] as const,
};

export function useAutocomplete(query: string | null, limit: number = 10) {
  return useQuery({
    queryKey: dictionaryKeys.autocomplete(query || '', limit),
    queryFn: () => searchAutocomplete(query || '', limit),
    enabled: !!query && query.length > 0,
    staleTime: 1000 * 60 * 5,
  });
}

export function useSearchWords(
  query: string,
  page: number,
  size: number,
  isAuthenticated: boolean = false,
) {
  return useQuery({
    queryKey: dictionaryKeys.search(query, page, size),
    queryFn: () => searchWords(query, page, size, isAuthenticated),
    enabled: query.length > 0,
  });
}

export function useWord(slug: string) {
  return useQuery({
    queryKey: dictionaryKeys.word(slug),
    queryFn: () => getWordBySlug(slug),
    enabled: slug.length > 0,
  });
}

export function useSearchHistory() {
  return useQuery({
    queryKey: dictionaryKeys.searchHistory(),
    queryFn: () => getSearchHistory(20),
    staleTime: 1000 * 60 * 5,
  });
}

export function useDeleteAllSearchHistory() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: deleteAllSearchHistory,
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: dictionaryKeys.searchHistory(),
      });
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useDeleteSearchHistory() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (historyId: number) => deleteSearchHistory(historyId),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: dictionaryKeys.searchHistory(),
      });
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useCreateWord() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateWordRequest) => createWord(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: dictionaryKeys.all });
      toast.success('단어가 생성되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useUpdateWord() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      wordId,
      data,
    }: {
      wordId: number;
      data: UpdateWordRequest;
    }) => updateWord(wordId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: dictionaryKeys.all });
      toast.success('단어가 수정되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useDeleteWord() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (wordId: number) => deleteWord(wordId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: dictionaryKeys.all });
      toast.success('단어가 삭제되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useCreateSense() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      wordId,
      data,
    }: {
      wordId: number;
      data: CreateSenseRequest;
    }) => createSense(wordId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: dictionaryKeys.all });
      toast.success('의미가 추가되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useUpdateSense() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      wordId,
      senseId,
      data,
    }: {
      wordId: number;
      senseId: number;
      data: UpdateSenseRequest;
    }) => updateSense(wordId, senseId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: dictionaryKeys.all });
      toast.success('의미가 수정되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useDeleteSense() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ wordId, senseId }: { wordId: number; senseId: number }) =>
      deleteSense(wordId, senseId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: dictionaryKeys.all });
      toast.success('의미가 삭제되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useReorderSenses() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ wordId, data }: { wordId: number; data: ReorderRequest }) =>
      reorderSenses(wordId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: dictionaryKeys.all });
      toast.success('순서가 변경되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useCreateExample() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      wordId,
      senseId,
      data,
    }: {
      wordId: number;
      senseId: number;
      data: CreateExampleRequest;
    }) => createExample(wordId, senseId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: dictionaryKeys.all });
      toast.success('예문이 추가되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useUpdateExample() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      wordId,
      senseId,
      exampleId,
      data,
    }: {
      wordId: number;
      senseId: number;
      exampleId: number;
      data: UpdateExampleRequest;
    }) => updateExample(wordId, senseId, exampleId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: dictionaryKeys.all });
      toast.success('예문이 수정되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useDeleteExample() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      wordId,
      senseId,
      exampleId,
    }: {
      wordId: number;
      senseId: number;
      exampleId: number;
    }) => deleteExample(wordId, senseId, exampleId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: dictionaryKeys.all });
      toast.success('예문이 삭제되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useReorderExamples() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      wordId,
      senseId,
      data,
    }: {
      wordId: number;
      senseId: number;
      data: ReorderRequest;
    }) => reorderExamples(wordId, senseId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: dictionaryKeys.all });
      toast.success('순서가 변경되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useDictionaryCounts() {
  return useQuery({
    queryKey: dictionaryKeys.counts(),
    queryFn: getDictionaryCounts,
  });
}

export function useAllWords(page: number = 0, size: number = 20) {
  return useQuery({
    queryKey: dictionaryKeys.allWords(page, size),
    queryFn: () => getAllWords(page, size),
  });
}
