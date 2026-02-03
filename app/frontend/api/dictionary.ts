import { fetcher, fetcherWithAuth } from '@/lib/fetcher';
import { handleApiResponse } from '@/lib/error';
import { Page } from '@/types/page';
import {
  CreateWordRequest,
  UpdateWordRequest,
  CreateSenseRequest,
  UpdateSenseRequest,
  CreateExampleRequest,
  UpdateExampleRequest,
  ReorderRequest,
  PublicWordSimpleView,
  PublicWordResponse,
  UserSearchHistory,
  DictionaryCountView,
} from '@/types/api/dictionary';

export async function searchAutocomplete(
  query: string,
  limit: number = 10,
): Promise<PublicWordSimpleView[]> {
  const response = await fetcher(
    `/api/dictionary/search/autocomplete?query=${encodeURIComponent(query)}&limit=${limit}`,
  );
  await handleApiResponse(response, 'Failed to fetch autocomplete');

  const data: PublicWordSimpleView[] = await response.json();
  return data;
}

export async function searchWords(
  query: string,
  page: number = 0,
  size: number = 20,
  isAuthenticated: boolean = false,
): Promise<Page<PublicWordSimpleView>> {
  const fetchFn = isAuthenticated ? fetcherWithAuth : fetcher;
  const response = await fetchFn(
    `/api/dictionary/search?query=${encodeURIComponent(query)}&page=${page}&size=${size}`,
  );
  await handleApiResponse(response, 'Failed to search words');

  const data: Page<PublicWordSimpleView> = await response.json();
  return data;
}

export async function getWordBySlug(slug: string): Promise<PublicWordResponse> {
  const response = await fetcher(`/api/dictionary/words/slug/${slug}`);
  await handleApiResponse(response, 'Failed to fetch word');

  const data: PublicWordResponse = await response.json();
  return data;
}

export async function getSearchHistory(
  limit: number = 10,
): Promise<UserSearchHistory[]> {
  const response = await fetcherWithAuth(
    `/api/dictionary/my/search-history?limit=${limit}`,
  );
  await handleApiResponse(response, 'Failed to fetch search history');

  const data: UserSearchHistory[] = await response.json();
  return data;
}

export async function deleteAllSearchHistory(): Promise<void> {
  const response = await fetcherWithAuth('/api/dictionary/my/search-history', {
    method: 'DELETE',
  });
  await handleApiResponse(response, 'Failed to delete all search history');
}

export async function deleteSearchHistory(historyId: number): Promise<void> {
  const response = await fetcherWithAuth(
    `/api/dictionary/my/search-history/${historyId}`,
    {
      method: 'DELETE',
    },
  );
  await handleApiResponse(response, 'Failed to delete search history');
}

export async function createWord(
  data: CreateWordRequest,
): Promise<PublicWordResponse> {
  const response = await fetcherWithAuth('/api/dictionary/words', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });

  await handleApiResponse(response, 'Failed to create word');

  const result: PublicWordResponse = await response.json();
  return result;
}

export async function updateWord(
  wordId: number,
  data: UpdateWordRequest,
): Promise<PublicWordResponse> {
  const response = await fetcherWithAuth(`/api/dictionary/words/${wordId}`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });

  await handleApiResponse(response, 'Failed to update word');

  const result: PublicWordResponse = await response.json();
  return result;
}

export async function deleteWord(wordId: number): Promise<void> {
  const response = await fetcherWithAuth(`/api/dictionary/words/${wordId}`, {
    method: 'DELETE',
  });

  await handleApiResponse(response, 'Failed to delete word');
}

export async function createSense(
  wordId: number,
  data: CreateSenseRequest,
): Promise<PublicWordResponse> {
  const response = await fetcherWithAuth(
    `/api/dictionary/words/${wordId}/senses`,
    {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    },
  );

  await handleApiResponse(response, 'Failed to create sense');

  const result: PublicWordResponse = await response.json();
  return result;
}

export async function updateSense(
  wordId: number,
  senseId: number,
  data: UpdateSenseRequest,
): Promise<PublicWordResponse> {
  const response = await fetcherWithAuth(
    `/api/dictionary/words/${wordId}/senses/${senseId}`,
    {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    },
  );

  await handleApiResponse(response, 'Failed to update sense');

  const result: PublicWordResponse = await response.json();
  return result;
}

export async function deleteSense(
  wordId: number,
  senseId: number,
): Promise<void> {
  const response = await fetcherWithAuth(
    `/api/dictionary/words/${wordId}/senses/${senseId}`,
    {
      method: 'DELETE',
    },
  );

  await handleApiResponse(response, 'Failed to delete sense');
}

export async function reorderSenses(
  wordId: number,
  data: ReorderRequest,
): Promise<void> {
  const response = await fetcherWithAuth(
    `/api/dictionary/words/${wordId}/senses/reorder`,
    {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    },
  );

  await handleApiResponse(response, 'Failed to reorder senses');
}

export async function createExample(
  wordId: number,
  senseId: number,
  data: CreateExampleRequest,
): Promise<PublicWordResponse> {
  const response = await fetcherWithAuth(
    `/api/dictionary/words/${wordId}/senses/${senseId}/examples`,
    {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    },
  );

  await handleApiResponse(response, 'Failed to create example');

  const result: PublicWordResponse = await response.json();
  return result;
}

export async function updateExample(
  wordId: number,
  senseId: number,
  exampleId: number,
  data: UpdateExampleRequest,
): Promise<PublicWordResponse> {
  const response = await fetcherWithAuth(
    `/api/dictionary/words/${wordId}/senses/${senseId}/examples/${exampleId}`,
    {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    },
  );

  await handleApiResponse(response, 'Failed to update example');

  const result: PublicWordResponse = await response.json();
  return result;
}

export async function deleteExample(
  wordId: number,
  senseId: number,
  exampleId: number,
): Promise<void> {
  const response = await fetcherWithAuth(
    `/api/dictionary/words/${wordId}/senses/${senseId}/examples/${exampleId}`,
    {
      method: 'DELETE',
    },
  );

  await handleApiResponse(response, 'Failed to delete example');
}

export async function reorderExamples(
  wordId: number,
  senseId: number,
  data: ReorderRequest,
): Promise<void> {
  const response = await fetcherWithAuth(
    `/api/dictionary/words/${wordId}/senses/${senseId}/examples/reorder`,
    {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    },
  );

  await handleApiResponse(response, 'Failed to reorder examples');
}

export async function getDictionaryCounts(): Promise<DictionaryCountView> {
  const response = await fetcher('/api/dictionary/count');
  await handleApiResponse(response, 'Failed to fetch dictionary counts');

  const data: DictionaryCountView = await response.json();
  return data;
}

export async function getAllWords(
  page: number = 0,
  size: number = 20,
): Promise<Page<PublicWordSimpleView>> {
  const response = await fetcher(`/api/dictionary/words?page=${page}&size=${size}`);
  await handleApiResponse(response, 'Failed to fetch all words');

  const data: Page<PublicWordSimpleView> = await response.json();
  return data;
}
