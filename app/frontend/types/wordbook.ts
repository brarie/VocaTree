export interface Wordbook {
  id: string;
  name: string;
  wordCount: number;
  isPinned: boolean;
  createdAt: string;
  updatedAt: string;
  lastTestedAt?: string;
}

export type SortOption =
  | 'name-asc'
  | 'name-desc'
  | 'created-asc'
  | 'created-desc'
  | 'updated-asc'
  | 'updated-desc';
