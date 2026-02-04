import { z } from 'zod';
import { SenseResponse, ExampleResponse } from './common/vocabulary';

export const CreateWordRequestSchema = z.object({
  spelling: z.string().min(1, '단어를 입력해주세요.'),
});

export const UpdateWordRequestSchema = z.object({
  spelling: z.string().min(1, '단어를 입력해주세요.'),
});

export const CreateSenseRequestSchema = z.object({
  definition: z.string().min(1, '한글 뜻을 입력해주세요.'),
  engDefinition: z.string().optional(),
  britishPhonetic: z.string().optional(),
  americanPhonetic: z.string().optional(),
  partOfSpeech: z.string().optional(),
  synonyms: z.array(z.string()).optional(),
  antonyms: z.array(z.string()).optional(),
  displayOrder: z.number().optional(),
});

export const UpdateSenseRequestSchema = z.object({
  definition: z.string().optional(),
  engDefinition: z.string().optional(),
  britishPhonetic: z.string().optional(),
  americanPhonetic: z.string().optional(),
  partOfSpeech: z.string().optional(),
  synonyms: z.array(z.string()).optional(),
  antonyms: z.array(z.string()).optional(),
  displayOrder: z.number().optional(),
});

export const CreateExampleRequestSchema = z.object({
  sentence: z.string().min(1, '예문을 입력해주세요.'),
  translation: z.string().optional(),
  isOfficial: z.boolean().optional(),
  sourceInfo: z.string().optional(),
  displayOrder: z.number().optional(),
});

export const UpdateExampleRequestSchema = z.object({
  sentence: z.string().optional(),
  translation: z.string().optional(),
  isOfficial: z.boolean().optional(),
  sourceInfo: z.string().optional(),
  displayOrder: z.number().optional(),
});

export interface DictionaryCountView {
  totalWords: number;
  totalSenses: number;
  totalExamples: number;
}

export interface PublicWordSimpleView {
  id: number;
  spelling: string;
  slug: string;
  topDefinitions: string[];
}

export const ReorderRequestSchema = z.object({
  orders: z.array(
    z.object({
      id: z.number(),
      displayOrder: z.number(),
    }),
  ),
});

export type CreateWordRequest = z.infer<typeof CreateWordRequestSchema>;
export type UpdateWordRequest = z.infer<typeof UpdateWordRequestSchema>;
export type CreateSenseRequest = z.infer<typeof CreateSenseRequestSchema>;
export type UpdateSenseRequest = z.infer<typeof UpdateSenseRequestSchema>;
export type CreateExampleRequest = z.infer<typeof CreateExampleRequestSchema>;
export type UpdateExampleRequest = z.infer<typeof UpdateExampleRequestSchema>;
export type ReorderRequest = z.infer<typeof ReorderRequestSchema>;

export interface PublicWordExample {
  id: number;
  example: ExampleResponse;
  isOfficial: boolean;
  sourceInfo: string | null;
  displayOrder: number;
}

export interface PublicWordSense {
  id: number;
  sense: SenseResponse;
  displayOrder: number;
  examples: PublicWordExample[];
}

export interface PublicWordResponse {
  id: number;
  spelling: string;
  slug: string;
  senses: PublicWordSense[];
}

export interface UserSearchHistory {
  id: number;
  spelling: string;
  searchedAt: string;
}
