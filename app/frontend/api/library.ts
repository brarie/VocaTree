import { fetcher, fetcherWithAuth } from '@/lib/fetcher';

// slug 형식: "{kebab-title}-{id}" — 끝의 숫자가 항상 ID
export function extractIdFromSlug(slug: string): number | null {
  const match = slug.match(/-(\d+)$/);
  if (!match) return null;
  return parseInt(match[1], 10);
}
import { handleApiResponse } from '@/lib/error';
import { Page, PageParams, pageParamsToQueryString } from '@/types/page';
import {
  SharedWordbookListView,
  SharedWordbookDetailView,
  SharedWordbookVersionView,
  SharedWordSnapshotResponse,
  ShareMyWordbookRequest,
  SharedWordbookResponse,
  UpdateSharedWordbookMetadataRequest,
  MakeNewVersionRequest,
  SharedWordbookRevisionResponse,
  UpdateTrustedLevelRequest,
  TrustedLevel,
  SharedWordbookBundleListView,
  SharedWordbookBundleDetailView,
  MakeSharedBundleRequest,
  SharedWordbookBundleResponse,
  UpdateSharedBundleMetadataRequest,
  UpdateBundleTrustedLevelRequest,
  SharedWordbookSortType,
  SharedWordbookBundleSortType,
} from '@/types/api/library';

// ==========================================
// Shared Wordbook APIs
// ==========================================

export async function getSharedWordbooks(
  params: PageParams,
  titlePrefix?: string,
  trustedLevels?: TrustedLevel[],
  sortType: SharedWordbookSortType = 'CREATED_AT_DESC',
): Promise<Page<SharedWordbookListView>> {
  let queryString = pageParamsToQueryString(params);
  if (titlePrefix)
    queryString += `&titlePrefix=${encodeURIComponent(titlePrefix)}`;
  if (trustedLevels && trustedLevels.length > 0) {
    queryString += `&trustedLevels=${trustedLevels.join(',')}`;
  }
  queryString += `&sortType=${sortType}`;

  const response = await fetcher(
    `/api/library/shared-wordbooks?${queryString}`,
  );
  await handleApiResponse(response, 'Failed to fetch shared wordbooks');
  return response.json();
}

export async function getSharedWordbookDetail(
  slug: string,
): Promise<SharedWordbookDetailView> {
  const id = extractIdFromSlug(slug);
  if (id === null) throw new Error(`유효하지 않은 slug 형식입니다: ${slug}`);
  const response = await fetcher(`/api/library/shared-wordbooks/${id}`);
  await handleApiResponse(response, 'Failed to fetch shared wordbook detail');
  return response.json();
}

export async function getSharedWordbookVersions(
  id: number,
  params: PageParams,
): Promise<Page<SharedWordbookVersionView>> {
  const queryString = pageParamsToQueryString(params);
  const response = await fetcher(
    `/api/library/shared-wordbooks/${id}/versions?${queryString}`,
  );
  await handleApiResponse(response, 'Failed to fetch shared wordbook versions');
  return response.json();
}

export async function getSharedWordsByVersion(
  id: number,
  version: number,
): Promise<SharedWordSnapshotResponse[]> {
  const response = await fetcher(
    `/api/library/shared-wordbooks/${id}/versions/${version}/words`,
  );
  await handleApiResponse(response, 'Failed to fetch shared words by version');
  return response.json();
}

export async function getMySharedWordbooks(
  params: PageParams,
): Promise<Page<SharedWordbookListView>> {
  const queryString = pageParamsToQueryString(params);
  const response = await fetcherWithAuth(
    `/api/library/shared-wordbooks/my?${queryString}`,
  );
  await handleApiResponse(response, 'Failed to fetch my shared wordbooks');
  return response.json();
}

export async function shareOwnWordbook(
  data: ShareMyWordbookRequest,
): Promise<SharedWordbookResponse> {
  const response = await fetcherWithAuth('/api/library/shared-wordbooks', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  await handleApiResponse(response, 'Failed to share wordbook');
  return response.json();
}

export async function updateSharedWordbookMetadata(
  id: number,
  data: UpdateSharedWordbookMetadataRequest,
): Promise<SharedWordbookResponse> {
  const response = await fetcherWithAuth(
    `/api/library/shared-wordbooks/${id}`,
    {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    },
  );
  await handleApiResponse(
    response,
    'Failed to update shared wordbook metadata',
  );
  return response.json();
}

export async function makeNewVersion(
  id: number,
  data: MakeNewVersionRequest,
): Promise<SharedWordbookRevisionResponse> {
  const response = await fetcherWithAuth(
    `/api/library/shared-wordbooks/${id}/versions`,
    {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    },
  );
  await handleApiResponse(response, 'Failed to make new version');
  return response.json();
}

export async function deleteMySharedWordbook(
  id: number,
): Promise<SharedWordbookResponse> {
  const response = await fetcherWithAuth(
    `/api/library/shared-wordbooks/${id}`,
    {
      method: 'DELETE',
    },
  );
  await handleApiResponse(response, 'Failed to delete shared wordbook');
  return response.json();
}

// ADMIN List APIs
export async function getAdminSharedWordbooks(
  params: PageParams,
  titlePrefix?: string,
): Promise<Page<SharedWordbookListView>> {
  let queryString = pageParamsToQueryString(params);
  if (titlePrefix)
    queryString += `&titlePrefix=${encodeURIComponent(titlePrefix)}`;
  const response = await fetcherWithAuth(
    `/api/library/shared-wordbooks/admin?${queryString}`,
  );
  await handleApiResponse(response, 'Failed to fetch admin shared wordbooks');
  return response.json();
}

export async function getAdminSharedBundles(
  params: PageParams,
  titlePrefix?: string,
): Promise<Page<SharedWordbookBundleListView>> {
  let queryString = pageParamsToQueryString(params);
  if (titlePrefix)
    queryString += `&titlePrefix=${encodeURIComponent(titlePrefix)}`;
  const response = await fetcherWithAuth(
    `/api/library/shared-wordbook-bundles/admin?${queryString}`,
  );
  await handleApiResponse(response, 'Failed to fetch admin shared bundles');
  return response.json();
}

// ADMIN APIs
export async function banSharedWordbook(
  id: number,
): Promise<SharedWordbookResponse> {
  const response = await fetcherWithAuth(
    `/api/library/shared-wordbooks/${id}/ban`,
    { method: 'PATCH' },
  );
  await handleApiResponse(response, 'Failed to ban shared wordbook');
  return response.json();
}

export async function unbanSharedWordbook(
  id: number,
): Promise<SharedWordbookResponse> {
  const response = await fetcherWithAuth(
    `/api/library/shared-wordbooks/${id}/unban`,
    { method: 'PATCH' },
  );
  await handleApiResponse(response, 'Failed to unban shared wordbook');
  return response.json();
}

export async function updateTrustedLevel(
  id: number,
  data: UpdateTrustedLevelRequest,
): Promise<SharedWordbookResponse> {
  const response = await fetcherWithAuth(
    `/api/library/shared-wordbooks/${id}/trusted-level`,
    {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    },
  );
  await handleApiResponse(response, 'Failed to update trusted level');
  return response.json();
}

// ==========================================
// Shared Wordbook Bundle APIs
// ==========================================

export async function getMySharedBundles(
  params: PageParams,
): Promise<Page<SharedWordbookBundleListView>> {
  const queryString = pageParamsToQueryString(params);
  const response = await fetcherWithAuth(
    `/api/library/shared-wordbook-bundles/my?${queryString}`,
  );
  await handleApiResponse(response, 'Failed to fetch my shared bundles');
  return response.json();
}

export async function getSharedBundles(
  params: PageParams,
  titlePrefix?: string,
  trustedLevels?: TrustedLevel[],
  sortType: SharedWordbookBundleSortType = 'CREATED_AT_DESC',
): Promise<Page<SharedWordbookBundleListView>> {
  let queryString = pageParamsToQueryString(params);
  if (titlePrefix)
    queryString += `&titlePrefix=${encodeURIComponent(titlePrefix)}`;
  if (trustedLevels && trustedLevels.length > 0) {
    queryString += `&trustedLevels=${trustedLevels.join(',')}`;
  }
  queryString += `&sortType=${sortType}`;

  const response = await fetcher(
    `/api/library/shared-wordbook-bundles?${queryString}`,
  );
  await handleApiResponse(response, 'Failed to fetch shared bundles');
  return response.json();
}

export async function getSharedBundleDetail(
  slug: string,
): Promise<SharedWordbookBundleDetailView> {
  const id = extractIdFromSlug(slug);
  if (id === null) throw new Error(`유효하지 않은 slug 형식입니다: ${slug}`);
  const response = await fetcher(`/api/library/shared-wordbook-bundles/${id}`);
  await handleApiResponse(response, 'Failed to fetch shared bundle detail');
  return response.json();
}

export async function getWordbooksInBundle(
  bundleId: number,
  params: PageParams,
): Promise<Page<SharedWordbookListView>> {
  const queryString = pageParamsToQueryString(params);
  const response = await fetcher(
    `/api/library/shared-wordbook-bundles/${bundleId}/wordbooks?${queryString}`,
  );
  await handleApiResponse(response, 'Failed to fetch wordbooks in bundle');
  return response.json();
}

export async function makeSharedBundle(
  data: MakeSharedBundleRequest,
): Promise<SharedWordbookBundleResponse> {
  const response = await fetcherWithAuth(
    '/api/library/shared-wordbook-bundles',
    {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    },
  );
  await handleApiResponse(response, 'Failed to make shared bundle');
  return response.json();
}

export async function updateSharedBundleMetadata(
  id: number,
  data: UpdateSharedBundleMetadataRequest,
): Promise<SharedWordbookBundleResponse> {
  const response = await fetcherWithAuth(
    `/api/library/shared-wordbook-bundles/${id}`,
    {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    },
  );
  await handleApiResponse(response, 'Failed to update shared bundle metadata');
  return response.json();
}

export async function deleteSharedBundle(
  id: number,
): Promise<SharedWordbookBundleResponse> {
  const response = await fetcherWithAuth(
    `/api/library/shared-wordbook-bundles/${id}`,
    {
      method: 'DELETE',
    },
  );
  await handleApiResponse(response, 'Failed to delete shared bundle');
  return response.json();
}

export async function addWordbookToBundle(
  bundleId: number,
  sharedWordbookId: number,
): Promise<void> {
  const response = await fetcherWithAuth(
    `/api/library/shared-wordbook-bundles/${bundleId}/wordbooks/${sharedWordbookId}`,
    {
      method: 'POST',
    },
  );
  await handleApiResponse(response, 'Failed to add wordbook to bundle');
}

export async function removeWordbookFromBundle(
  bundleId: number,
  sharedWordbookId: number,
): Promise<void> {
  const response = await fetcherWithAuth(
    `/api/library/shared-wordbook-bundles/${bundleId}/wordbooks/${sharedWordbookId}`,
    {
      method: 'DELETE',
    },
  );
  await handleApiResponse(response, 'Failed to remove wordbook from bundle');
}

// ADMIN Bundle APIs
export async function banSharedBundle(
  id: number,
): Promise<SharedWordbookBundleResponse> {
  const response = await fetcherWithAuth(
    `/api/library/shared-wordbook-bundles/${id}/ban`,
    { method: 'PATCH' },
  );
  await handleApiResponse(response, 'Failed to ban shared bundle');
  return response.json();
}

export async function unbanSharedBundle(
  id: number,
): Promise<SharedWordbookBundleResponse> {
  const response = await fetcherWithAuth(
    `/api/library/shared-wordbook-bundles/${id}/unban`,
    { method: 'PATCH' },
  );
  await handleApiResponse(response, 'Failed to unban shared bundle');
  return response.json();
}

export async function updateTrustedLevelBundle(
  id: number,
  data: UpdateBundleTrustedLevelRequest,
): Promise<SharedWordbookBundleResponse> {
  const response = await fetcherWithAuth(
    `/api/library/shared-wordbook-bundles/${id}/trusted-level`,
    {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    },
  );
  await handleApiResponse(response, 'Failed to update bundle trusted level');
  return response.json();
}
