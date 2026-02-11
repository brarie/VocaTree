import { fetcherWithAuth } from '@/lib/fetcher';
import { handleApiResponse } from '@/lib/error';
import {
  WordbookWordListResponse,
  WordbookColumnResponse,
  UpdateWordbookWordRequest,
  UpdateWordbookSenseRequest,
  UpdateWordbookExampleRequest,
  UpdateColumnWidthRequest,
  SwapWordbookWordsOrderRequest,
  SwapWordbookSensesOrderRequest,
  SwapWordbookExamplesOrderRequest,
  ColumnType,
} from '@/types/api/wordbookWord';

export async function getWordList(
  wordbookId: number,
): Promise<WordbookWordListResponse> {
  const response = await fetcherWithAuth(
    `/api/wordbooks/${wordbookId}/words`,
  );
  await handleApiResponse(response, 'Failed to fetch word list');
  return await response.json();
}

export async function getColumns(
  wordbookId: number,
): Promise<WordbookColumnResponse> {
  const response = await fetcherWithAuth(
    `/api/wordbooks/${wordbookId}/columns`,
  );
  await handleApiResponse(response, 'Failed to fetch columns');
  return await response.json();
}

export async function createWordSeries(
  wordbookId: number,
): Promise<WordbookWordListResponse> {
  const response = await fetcherWithAuth(
    `/api/wordbooks/${wordbookId}/words/series`,
    {
      method: 'POST',
    },
  );
  await handleApiResponse(response, 'Failed to create word series');
  return await response.json();
}

export async function createSenseSeries(
  wordbookId: number,
  wordId: number,
): Promise<WordbookWordListResponse> {
  const response = await fetcherWithAuth(
    `/api/wordbooks/${wordbookId}/words/${wordId}/senses/series`,
    {
      method: 'POST',
    },
  );
  await handleApiResponse(response, 'Failed to create sense series');
  return await response.json();
}

export async function createWord(
  wordbookId: number,
): Promise<WordbookWordListResponse> {
  const response = await fetcherWithAuth(
    `/api/wordbooks/${wordbookId}/words`,
    {
      method: 'POST',
    },
  );
  await handleApiResponse(response, 'Failed to create word');
  return await response.json();
}

export async function createSense(
  wordbookId: number,
  wordId: number,
): Promise<WordbookWordListResponse> {
  const response = await fetcherWithAuth(
    `/api/wordbooks/${wordbookId}/words/${wordId}/senses`,
    { method: 'POST' },
  );
  await handleApiResponse(response, 'Failed to create sense');
  return await response.json();
}

export async function createExample(
  wordbookId: number,
  wordId: number,
  senseId: number,
): Promise<WordbookWordListResponse> {
  const response = await fetcherWithAuth(
    `/api/wordbooks/${wordbookId}/words/${wordId}/senses/${senseId}/examples`,
    { method: 'POST' },
  );
  await handleApiResponse(response, 'Failed to create example');
  return await response.json();
}

export async function updateWord(
  wordbookId: number,
  wordId: number,
  data: UpdateWordbookWordRequest,
): Promise<WordbookWordListResponse> {
  const response = await fetcherWithAuth(
    `/api/wordbooks/${wordbookId}/words/${wordId}`,
    {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    },
  );
  await handleApiResponse(response, 'Failed to update word');
  return await response.json();
}

export async function updateSense(
  wordbookId: number,
  wordId: number,
  senseId: number,
  data: UpdateWordbookSenseRequest,
): Promise<WordbookWordListResponse> {
  const response = await fetcherWithAuth(
    `/api/wordbooks/${wordbookId}/words/${wordId}/senses/${senseId}`,
    {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    },
  );
  await handleApiResponse(response, 'Failed to update sense');
  return await response.json();
}

export async function updateExample(
  wordbookId: number,
  wordId: number,
  senseId: number,
  exampleId: number,
  data: UpdateWordbookExampleRequest,
): Promise<WordbookWordListResponse> {
  const response = await fetcherWithAuth(
    `/api/wordbooks/${wordbookId}/words/${wordId}/senses/${senseId}/examples/${exampleId}`,
    {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    },
  );
  await handleApiResponse(response, 'Failed to update example');
  return await response.json();
}

export async function deleteWord(
  wordbookId: number,
  wordId: number,
): Promise<WordbookWordListResponse> {
  const response = await fetcherWithAuth(
    `/api/wordbooks/${wordbookId}/words/${wordId}`,
    { method: 'DELETE' },
  );
  await handleApiResponse(response, 'Failed to delete word');
  return await response.json();
}

export async function deleteSense(
  wordbookId: number,
  wordId: number,
  senseId: number,
): Promise<WordbookWordListResponse> {
  const response = await fetcherWithAuth(
    `/api/wordbooks/${wordbookId}/words/${wordId}/senses/${senseId}`,
    { method: 'DELETE' },
  );
  await handleApiResponse(response, 'Failed to delete sense');
  return await response.json();
}

export async function deleteExample(
  wordbookId: number,
  wordId: number,
  senseId: number,
  exampleId: number,
): Promise<WordbookWordListResponse> {
  const response = await fetcherWithAuth(
    `/api/wordbooks/${wordbookId}/words/${wordId}/senses/${senseId}/examples/${exampleId}`,
    { method: 'DELETE' },
  );
  await handleApiResponse(response, 'Failed to delete example');
  return await response.json();
}

export async function toggleColumnActive(
  wordbookId: number,
  columnType: ColumnType,
): Promise<WordbookColumnResponse> {
  const response = await fetcherWithAuth(
    `/api/wordbooks/${wordbookId}/columns/${columnType}/toggle`,
    { method: 'PATCH' },
  );
  await handleApiResponse(response, 'Failed to toggle column');
  return await response.json();
}

export async function updateColumnWidth(
  wordbookId: number,
  columnType: ColumnType,
  data: UpdateColumnWidthRequest,
): Promise<WordbookColumnResponse> {
  const response = await fetcherWithAuth(
    `/api/wordbooks/${wordbookId}/columns/${columnType}/width`,
    {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    },
  );
  await handleApiResponse(response, 'Failed to update column width');
  return await response.json();
}

// Swap order functions
export async function swapWordsOrder(
  wordbookId: number,
  data: SwapWordbookWordsOrderRequest,
): Promise<WordbookWordListResponse> {
  const response = await fetcherWithAuth(
    `/api/wordbooks/${wordbookId}/words/swap-order`,
    {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    },
  );
  await handleApiResponse(response, 'Failed to swap words order');
  return await response.json();
}

export async function swapSensesOrder(
  wordbookId: number,
  wordId: number,
  data: SwapWordbookSensesOrderRequest,
): Promise<WordbookWordListResponse> {
  const response = await fetcherWithAuth(
    `/api/wordbooks/${wordbookId}/words/${wordId}/senses/swap-order`,
    {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    },
  );
  await handleApiResponse(response, 'Failed to swap senses order');
  return await response.json();
}

export async function swapExamplesOrder(
  wordbookId: number,
  wordId: number,
  senseId: number,
  data: SwapWordbookExamplesOrderRequest,
): Promise<WordbookWordListResponse> {
  const response = await fetcherWithAuth(
    `/api/wordbooks/${wordbookId}/words/${wordId}/senses/${senseId}/examples/swap-order`,
    {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    },
  );
  await handleApiResponse(response, 'Failed to swap examples order');
  return await response.json();
}
