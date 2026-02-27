import { fetcherWithAuth } from '@/lib/fetcher';
import { handleApiResponse } from '@/lib/error';
import { Page } from '@/types/page';
import {
  WordbookNodeResponse,
  NodeResponseWithCountView,
  SimpleWordbookView,
  CreateWordbookRequest,
  UpdateWordbookTitleRequest,
  CreateBundleRequest,
  UpdateBundleTitleRequest,
  WordbookNodeSortType,
} from '@/types/api/wordbook';

export async function getSimpleWordbooks(): Promise<SimpleWordbookView[]> {
  const response = await fetcherWithAuth('/api/wordbooks/simple');
  await handleApiResponse(response, 'Failed to fetch simple wordbooks');
  return response.json();
}

export async function paginateNodes(
  page: number = 0,
  size: number = 30,
  sortType: WordbookNodeSortType = WordbookNodeSortType.CREATED_AT_DESC,
  starOnly: boolean = false,
): Promise<Page<NodeResponseWithCountView>> {
  const response = await fetcherWithAuth(
    `/api/wordbook-nodes?page=${page}&size=${size}&wordbookNodeSortType=${sortType}&starOnly=${starOnly}`,
  );
  await handleApiResponse(response, 'Failed to fetch wordbook nodes');

  const data: Page<NodeResponseWithCountView> = await response.json();
  return data;
}

export async function paginateNodesInBundle(
  bundleId: number,
  page: number = 0,
  size: number = 30,
): Promise<Page<NodeResponseWithCountView>> {
  const response = await fetcherWithAuth(
    `/api/wordbook-nodes/${bundleId}?page=${page}&size=${size}`,
  );
  await handleApiResponse(response, 'Failed to fetch nodes in bundle');

  const data: Page<NodeResponseWithCountView> = await response.json();
  return data;
}

export async function getWordbook(
  wordbookId: number,
): Promise<WordbookNodeResponse> {
  const response = await fetcherWithAuth(`/api/wordbooks/${wordbookId}`);
  await handleApiResponse(response, 'Failed to fetch wordbook');

  const data: WordbookNodeResponse = await response.json();
  return data;
}

export async function getBundle(
  bundleId: number,
): Promise<WordbookNodeResponse> {
  const response = await fetcherWithAuth(`/api/wordbook-bundles/${bundleId}`);
  await handleApiResponse(response, 'Failed to fetch bundle');

  const data: WordbookNodeResponse = await response.json();
  return data;
}

export async function getAllBundles(): Promise<WordbookNodeResponse[]> {
  const response = await fetcherWithAuth('/api/wordbook-bundles');
  await handleApiResponse(response, 'Failed to fetch all bundles');

  const data: WordbookNodeResponse[] = await response.json();
  return data;
}

export async function createBundle(
  request: CreateBundleRequest,
): Promise<WordbookNodeResponse> {
  const response = await fetcherWithAuth('/api/wordbook-bundles', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request),
  });
  await handleApiResponse(response, 'Failed to create bundle');

  const data: WordbookNodeResponse = await response.json();
  return data;
}

export async function updateBundleTitle(
  bundleId: number,
  request: UpdateBundleTitleRequest,
): Promise<WordbookNodeResponse> {
  const response = await fetcherWithAuth(
    `/api/wordbook-bundles/${bundleId}/title`,
    {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(request),
    },
  );
  await handleApiResponse(response, 'Failed to update bundle title');

  const data: WordbookNodeResponse = await response.json();
  return data;
}

export async function toggleBundleStarred(
  bundleId: number,
): Promise<WordbookNodeResponse> {
  const response = await fetcherWithAuth(
    `/api/wordbook-bundles/${bundleId}/star`,
    {
      method: 'PATCH',
    },
  );
  await handleApiResponse(response, 'Failed to toggle bundle starred');

  const data: WordbookNodeResponse = await response.json();
  return data;
}

export async function deleteBundle(bundleId: number): Promise<void> {
  const response = await fetcherWithAuth(`/api/wordbook-bundles/${bundleId}`, {
    method: 'DELETE',
  });
  await handleApiResponse(response, 'Failed to delete bundle');
}

export async function createWordbook(
  request: CreateWordbookRequest,
): Promise<WordbookNodeResponse> {
  const response = await fetcherWithAuth('/api/wordbooks', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request),
  });
  await handleApiResponse(response, 'Failed to create wordbook');

  const data: WordbookNodeResponse = await response.json();
  return data;
}

export async function createWordbookInBundle(
  bundleId: number,
  request: CreateWordbookRequest,
): Promise<WordbookNodeResponse> {
  const response = await fetcherWithAuth(
    `/api/wordbooks/wordbook-bundle/${bundleId}`,
    {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(request),
    },
  );
  await handleApiResponse(response, 'Failed to create wordbook in bundle');

  const data: WordbookNodeResponse = await response.json();
  return data;
}

export async function updateWordbookTitle(
  wordbookId: number,
  request: UpdateWordbookTitleRequest,
): Promise<WordbookNodeResponse> {
  const response = await fetcherWithAuth(
    `/api/wordbooks/${wordbookId}/title`,
    {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(request),
    },
  );
  await handleApiResponse(response, 'Failed to update wordbook title');

  const data: WordbookNodeResponse = await response.json();
  return data;
}

export async function toggleWordbookStarred(
  wordbookId: number,
): Promise<WordbookNodeResponse> {
  const response = await fetcherWithAuth(`/api/wordbooks/${wordbookId}/star`, {
    method: 'PATCH',
  });
  await handleApiResponse(response, 'Failed to toggle wordbook starred');

  const data: WordbookNodeResponse = await response.json();
  return data;
}

export async function moveWordbookToRoot(
  wordbookId: number,
): Promise<WordbookNodeResponse> {
  const response = await fetcherWithAuth(
    `/api/wordbooks/${wordbookId}/move/root`,
    {
      method: 'PATCH',
    },
  );
  await handleApiResponse(response, 'Failed to move wordbook to root');

  const data: WordbookNodeResponse = await response.json();
  return data;
}

export async function moveWordbookToBundle(
  wordbookId: number,
  bundleId: number,
): Promise<WordbookNodeResponse> {
  const response = await fetcherWithAuth(
    `/api/wordbooks/${wordbookId}/move/${bundleId}`,
    {
      method: 'PATCH',
    },
  );
  await handleApiResponse(response, 'Failed to move wordbook to bundle');

  const data: WordbookNodeResponse = await response.json();
  return data;
}

export async function deleteWordbook(wordbookId: number): Promise<void> {
  const response = await fetcherWithAuth(`/api/wordbooks/${wordbookId}`, {
    method: 'DELETE',
  });
  await handleApiResponse(response, 'Failed to delete wordbook');
}
