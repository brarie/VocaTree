export interface SenseResponse {
  definition: string | null;
  engDefinition: string | null;
  britishPhonetic: string | null;
  americanPhonetic: string | null;
  partOfSpeech: string | null;
  synonyms: string[];
  antonyms: string[];
}

export interface ExampleResponse {
  sentence: string | null;
  translation: string | null;
}
