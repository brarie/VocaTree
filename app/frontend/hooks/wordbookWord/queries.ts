import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  getWordList,
  getColumns,
  createWordSeries,
  createSenseSeries,
  createWord,
  createSense,
  createExample,
  updateWord,
  updateSense,
  updateExample,
  deleteWord,
  deleteSense,
  deleteExample,
  toggleColumnActive,
  updateColumnWidth,
  swapWordsOrder,
  swapSensesOrder,
  swapExamplesOrder,
  autoCompleteFromDictionary,
} from '@/api/wordbookWord';
import {
  UpdateWordbookWordRequest,
  UpdateWordbookSenseRequest,
  UpdateWordbookExampleRequest,
  UpdateColumnWidthRequest,
  SwapWordbookWordsOrderRequest,
  SwapWordbookSensesOrderRequest,
  SwapWordbookExamplesOrderRequest,
  AutoCompleteFromDictionaryRequest,
  ColumnType,
} from '@/types/api/wordbookWord';
import { ApiError } from '@/types/api/error';
import { toast } from 'sonner';

export const wordbookWordKeys = {
  all: ['wordbookWord'] as const,
  wordList: (wordbookId: number) =>
    [...wordbookWordKeys.all, 'wordList', wordbookId] as const,
  columns: (wordbookId: number) =>
    [...wordbookWordKeys.all, 'columns', wordbookId] as const,
};

export function useWordList(wordbookId: number) {
  return useQuery({
    queryKey: wordbookWordKeys.wordList(wordbookId),
    queryFn: () => getWordList(wordbookId),
    enabled: wordbookId > 0,
  });
}

export function useColumns(wordbookId: number) {
  return useQuery({
    queryKey: wordbookWordKeys.columns(wordbookId),
    queryFn: () => getColumns(wordbookId),
    enabled: wordbookId > 0,
  });
}

export function useCreateWordSeries() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (wordbookId: number) => createWordSeries(wordbookId),
    onSuccess: (data) => {
      queryClient.setQueryData(wordbookWordKeys.wordList(data.wordbookId), data);
      toast.success('단어가 추가되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useCreateSenseSeries() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ wordbookId, wordId }: { wordbookId: number; wordId: number }) =>
      createSenseSeries(wordbookId, wordId),
    onSuccess: (data) => {
      queryClient.setQueryData(wordbookWordKeys.wordList(data.wordbookId), data);
      toast.success('의미가 추가되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useCreateWord() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (wordbookId: number) => createWord(wordbookId),
    onSuccess: (data) => {
      queryClient.setQueryData(wordbookWordKeys.wordList(data.wordbookId), data);
      toast.success('단어가 추가되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useCreateSense() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ wordbookId, wordId }: { wordbookId: number; wordId: number }) =>
      createSense(wordbookId, wordId),
    onSuccess: (data) => {
      queryClient.setQueryData(wordbookWordKeys.wordList(data.wordbookId), data);
      toast.success('의미가 추가되었습니다.');
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
      wordbookId,
      wordId,
      senseId,
    }: {
      wordbookId: number;
      wordId: number;
      senseId: number;
    }) => createExample(wordbookId, wordId, senseId),
    onSuccess: (data) => {
      queryClient.setQueryData(wordbookWordKeys.wordList(data.wordbookId), data);
      toast.success('예문이 추가되었습니다.');
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
      wordbookId,
      wordId,
      data,
    }: {
      wordbookId: number;
      wordId: number;
      data: UpdateWordbookWordRequest;
    }) => updateWord(wordbookId, wordId, data),
    onMutate: async ({ wordbookId, wordId, data }) => {
      // Cancel outgoing refetches
      await queryClient.cancelQueries({
        queryKey: wordbookWordKeys.wordList(wordbookId),
      });

      // Snapshot previous value
      const previous = queryClient.getQueryData(
        wordbookWordKeys.wordList(wordbookId),
      );

      // Optimistically update to new value
      queryClient.setQueryData(
        wordbookWordKeys.wordList(wordbookId),
        (old: any) => {
          if (!old) return old;
          return {
            ...old,
            wordList: old.wordList.map((word: any) =>
              word.id === wordId ? { ...word, ...data } : word
            ),
          };
        },
      );

      return { previous };
    },
    onSuccess: (data) => {
      queryClient.setQueryData(wordbookWordKeys.wordList(data.wordbookId), data);
    },
    onError: (error: ApiError, variables, context) => {
      // Revert to previous value on error
      if (context?.previous) {
        queryClient.setQueryData(
          wordbookWordKeys.wordList(variables.wordbookId),
          context.previous,
        );
      }
      toast.error(error.message);
    },
  });
}

export function useUpdateSense() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      wordbookId,
      wordId,
      senseId,
      data,
    }: {
      wordbookId: number;
      wordId: number;
      senseId: number;
      data: UpdateWordbookSenseRequest;
    }) => updateSense(wordbookId, wordId, senseId, data),
    onMutate: async ({ wordbookId, wordId, senseId, data }) => {
      await queryClient.cancelQueries({
        queryKey: wordbookWordKeys.wordList(wordbookId),
      });

      const previous = queryClient.getQueryData(
        wordbookWordKeys.wordList(wordbookId),
      );

      // Optimistically update sense
      queryClient.setQueryData(
        wordbookWordKeys.wordList(wordbookId),
        (old: any) => {
          if (!old) return old;
          return {
            ...old,
            wordList: old.wordList.map((word: any) =>
              word.id === wordId
                ? {
                    ...word,
                    senses: word.senses.map((sense: any) =>
                      sense.id === senseId
                        ? {
                            ...sense,
                            senseResponse: { ...sense.senseResponse, ...data },
                          }
                        : sense
                    ),
                  }
                : word
            ),
          };
        },
      );

      return { previous };
    },
    onSuccess: (data) => {
      queryClient.setQueryData(wordbookWordKeys.wordList(data.wordbookId), data);
    },
    onError: (error: ApiError, variables, context) => {
      if (context?.previous) {
        queryClient.setQueryData(
          wordbookWordKeys.wordList(variables.wordbookId),
          context.previous,
        );
      }
      toast.error(error.message);
    },
  });
}

export function useUpdateExample() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      wordbookId,
      wordId,
      senseId,
      exampleId,
      data,
    }: {
      wordbookId: number;
      wordId: number;
      senseId: number;
      exampleId: number;
      data: UpdateWordbookExampleRequest;
    }) => updateExample(wordbookId, wordId, senseId, exampleId, data),
    onMutate: async ({ wordbookId, wordId, senseId, exampleId, data }) => {
      await queryClient.cancelQueries({
        queryKey: wordbookWordKeys.wordList(wordbookId),
      });

      const previous = queryClient.getQueryData(
        wordbookWordKeys.wordList(wordbookId),
      );

      // Optimistically update example
      queryClient.setQueryData(
        wordbookWordKeys.wordList(wordbookId),
        (old: any) => {
          if (!old) return old;
          return {
            ...old,
            wordList: old.wordList.map((word: any) =>
              word.id === wordId
                ? {
                    ...word,
                    senses: word.senses.map((sense: any) =>
                      sense.id === senseId
                        ? {
                            ...sense,
                            examples: sense.examples.map((example: any) =>
                              example.id === exampleId
                                ? {
                                    ...example,
                                    exampleResponse: {
                                      ...example.exampleResponse,
                                      ...data,
                                    },
                                  }
                                : example
                            ),
                          }
                        : sense
                    ),
                  }
                : word
            ),
          };
        },
      );

      return { previous };
    },
    onSuccess: (data) => {
      queryClient.setQueryData(wordbookWordKeys.wordList(data.wordbookId), data);
    },
    onError: (error: ApiError, variables, context) => {
      if (context?.previous) {
        queryClient.setQueryData(
          wordbookWordKeys.wordList(variables.wordbookId),
          context.previous,
        );
      }
      toast.error(error.message);
    },
  });
}

export function useDeleteWord() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ wordbookId, wordId }: { wordbookId: number; wordId: number }) =>
      deleteWord(wordbookId, wordId),
    onSuccess: (data) => {
      queryClient.setQueryData(wordbookWordKeys.wordList(data.wordbookId), data);
      toast.success('단어가 삭제되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useDeleteSense() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      wordbookId,
      wordId,
      senseId,
    }: {
      wordbookId: number;
      wordId: number;
      senseId: number;
    }) => deleteSense(wordbookId, wordId, senseId),
    onSuccess: (data) => {
      queryClient.setQueryData(wordbookWordKeys.wordList(data.wordbookId), data);
      toast.success('의미가 삭제되었습니다.');
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
      wordbookId,
      wordId,
      senseId,
      exampleId,
    }: {
      wordbookId: number;
      wordId: number;
      senseId: number;
      exampleId: number;
    }) => deleteExample(wordbookId, wordId, senseId, exampleId),
    onSuccess: (data) => {
      queryClient.setQueryData(wordbookWordKeys.wordList(data.wordbookId), data);
      toast.success('예문이 삭제되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useToggleColumnActive() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      wordbookId,
      columnType,
    }: {
      wordbookId: number;
      columnType: ColumnType;
    }) => toggleColumnActive(wordbookId, columnType),
    onSuccess: (data, variables) => {
      queryClient.setQueryData(wordbookWordKeys.columns(variables.wordbookId), data);
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useUpdateColumnWidth() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      wordbookId,
      columnType,
      data,
    }: {
      wordbookId: number;
      columnType: ColumnType;
      data: UpdateColumnWidthRequest;
    }) => updateColumnWidth(wordbookId, columnType, data),
    onSuccess: (data, variables) => {
      queryClient.setQueryData(wordbookWordKeys.columns(variables.wordbookId), data);
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

// Swap order hooks
export function useSwapWordsOrder() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      wordbookId,
      data,
    }: {
      wordbookId: number;
      data: SwapWordbookWordsOrderRequest;
    }) => swapWordsOrder(wordbookId, data),
    onSuccess: (data, variables) => {
      queryClient.setQueryData(
        wordbookWordKeys.wordList(variables.wordbookId),
        data,
      );
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useSwapSensesOrder() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      wordbookId,
      wordId,
      data,
    }: {
      wordbookId: number;
      wordId: number;
      data: SwapWordbookSensesOrderRequest;
    }) => swapSensesOrder(wordbookId, wordId, data),
    onSuccess: (data, variables) => {
      queryClient.setQueryData(
        wordbookWordKeys.wordList(variables.wordbookId),
        data,
      );
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useSwapExamplesOrder() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      wordbookId,
      wordId,
      senseId,
      data,
    }: {
      wordbookId: number;
      wordId: number;
      senseId: number;
      data: SwapWordbookExamplesOrderRequest;
    }) => swapExamplesOrder(wordbookId, wordId, senseId, data),
    onSuccess: (data, variables) => {
      queryClient.setQueryData(
        wordbookWordKeys.wordList(variables.wordbookId),
        data,
      );
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}

export function useAutoCompleteFromDictionary() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      wordbookId,
      wordId,
      data,
    }: {
      wordbookId: number;
      wordId: number;
      data: AutoCompleteFromDictionaryRequest;
    }) => autoCompleteFromDictionary(wordbookId, wordId, data),
    onSuccess: (data, variables) => {
      queryClient.setQueryData(
        wordbookWordKeys.wordList(variables.wordbookId),
        data,
      );
      toast.success('자동완성이 적용되었습니다.');
    },
    onError: (error: ApiError) => {
      toast.error(error.message);
    },
  });
}
