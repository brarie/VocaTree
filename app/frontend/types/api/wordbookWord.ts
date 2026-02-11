import { z } from 'zod';
import { SenseResponse, ExampleResponse } from './common/vocabulary';

// Enum matching backend ColumnType
export enum ColumnType {
  SPELLING = 'SPELLING',
  DEFINITION = 'DEFINITION',
  ENG_DEFINITION = 'ENG_DEFINITION',
  US_PHONETIC = 'US_PHONETIC',
  UK_PHONETIC = 'UK_PHONETIC',
  PART_OF_SPEECH = 'PART_OF_SPEECH',
  SYNONYMS = 'SYNONYMS',
  ANTONYMS = 'ANTONYMS',
  SENTENCE = 'SENTENCE',
  TRANSLATION = 'TRANSLATION',
}

// Request schemas (Zod for validation)
export const UpdateWordbookWordRequestSchema = z.object({
  spelling: z.string().nullable().optional(),
});

export const UpdateWordbookSenseRequestSchema = z.object({
  definition: z.string().nullable().optional(),
  engDefinition: z.string().nullable().optional(),
  britishPhonetic: z.string().nullable().optional(),
  americanPhonetic: z.string().nullable().optional(),
  partOfSpeech: z.string().nullable().optional(),
  synonyms: z.array(z.string()).optional(),
  antonyms: z.array(z.string()).optional(),
});

export const UpdateWordbookExampleRequestSchema = z.object({
  sentence: z.string().nullable().optional(),
  translation: z.string().nullable().optional(),
});

export const UpdateColumnWidthRequestSchema = z.object({
  width: z.number().min(50).max(1000),
});

// Swap schemas
export const SwapWordbookWordsOrderRequestSchema = z.object({
  wordId1: z.number(),
  wordId2: z.number(),
});

export const SwapWordbookSensesOrderRequestSchema = z.object({
  senseId1: z.number(),
  senseId2: z.number(),
});

export const SwapWordbookExamplesOrderRequestSchema = z.object({
  exampleId1: z.number(),
  exampleId2: z.number(),
});

export type UpdateWordbookWordRequest = z.infer<
  typeof UpdateWordbookWordRequestSchema
>;
export type UpdateWordbookSenseRequest = z.infer<
  typeof UpdateWordbookSenseRequestSchema
>;
export type UpdateWordbookExampleRequest = z.infer<
  typeof UpdateWordbookExampleRequestSchema
>;
export type UpdateColumnWidthRequest = z.infer<
  typeof UpdateColumnWidthRequestSchema
>;
export type SwapWordbookWordsOrderRequest = z.infer<
  typeof SwapWordbookWordsOrderRequestSchema
>;
export type SwapWordbookSensesOrderRequest = z.infer<
  typeof SwapWordbookSensesOrderRequestSchema
>;
export type SwapWordbookExamplesOrderRequest = z.infer<
  typeof SwapWordbookExamplesOrderRequestSchema
>;

// Response interfaces (TypeScript - backend trusted)
export interface WordbookWordExampleResponse {
  id: number;
  displayOrder: number;
  exampleResponse: ExampleResponse;
}

export interface WordbookWordSenseResponse {
  id: number;
  senseResponse: SenseResponse;
  displayOrder: number;
  examples: WordbookWordExampleResponse[];
}

export interface WordbookWordResponse {
  id: number;
  spelling: string | null;
  displayOrder: number;
  senses: WordbookWordSenseResponse[];
}

export interface WordbookWordListResponse {
  wordbookId: number;
  wordList: WordbookWordResponse[];
}

export interface WordbookColumnSettingResponse {
  isActive: boolean;
  width: number;
}

export interface WordbookColumnResponse {
  columns: Record<ColumnType, WordbookColumnSettingResponse>;
}

// Flattened row for TanStack Table
export interface FlattenedRow {
  rowId: string;
  level: 1 | 2 | 3;
  wordId: number;
  senseId: number | null;
  exampleId: number | null;

  // Level 1
  spelling: string | null;
  wordDisplayOrder: number;

  // Level 2
  definition: string | null;
  engDefinition: string | null;
  britishPhonetic: string | null;
  americanPhonetic: string | null;
  usPhonetic: string | null; // same as americanPhonetic
  ukPhonetic: string | null; // same as britishPhonetic
  partOfSpeech: string | null;
  synonyms: string[];
  antonyms: string[];
  senseDisplayOrder: number | null;

  // Level 3
  sentence: string | null;
  translation: string | null;
  exampleDisplayOrder: number | null;

  // Metadata for rendering
  wordRowspan: number;
  senseRowspan: number;
  isFirstRowOfWord: boolean;
  isFirstRowOfSense: boolean;
}
