import { SharedWordSnapshotResponse } from '@/types/api/library';

export interface SharedFlattenedRow {
  rowId: string;
  wordId: number;
  senseIndex: number | null;
  exampleIndex: number | null;

  // Word
  spelling: string | null;

  // Sense
  definition: string | null;
  engDefinition: string | null;
  britishPhonetic: string | null;
  americanPhonetic: string | null;
  partOfSpeech: string | null;
  synonyms: string[];
  antonyms: string[];

  // Example
  sentence: string | null;
  translation: string | null;

  // Rendering metadata
  wordRowspan: number;
  senseRowspan: number;
  isFirstRowOfWord: boolean;
  isFirstRowOfSense: boolean;
}

export function flattenSharedWordsToRows(
  words: SharedWordSnapshotResponse[],
): SharedFlattenedRow[] {
  const rows: SharedFlattenedRow[] = [];

  words.forEach((word) => {
    if (word.senses.length === 0) {
      rows.push({
        rowId: `w${word.id}`,
        wordId: word.id,
        senseIndex: null,
        exampleIndex: null,
        spelling: word.spelling,
        definition: null,
        engDefinition: null,
        britishPhonetic: null,
        americanPhonetic: null,
        partOfSpeech: null,
        synonyms: [],
        antonyms: [],
        sentence: null,
        translation: null,
        wordRowspan: 1,
        senseRowspan: 0,
        isFirstRowOfWord: true,
        isFirstRowOfSense: false,
      });
      return;
    }

    const totalRows = word.senses.reduce(
      (sum, sense) => sum + Math.max(1, sense.examples.length),
      0,
    );

    word.senses.forEach((sense, si) => {
      const senseRows = Math.max(1, sense.examples.length);

      if (sense.examples.length === 0) {
        rows.push({
          rowId: `w${word.id}-s${si}`,
          wordId: word.id,
          senseIndex: si,
          exampleIndex: null,
          spelling: word.spelling,
          definition: sense.sense.definition,
          engDefinition: sense.sense.engDefinition,
          britishPhonetic: sense.sense.britishPhonetic,
          americanPhonetic: sense.sense.americanPhonetic,
          partOfSpeech: sense.sense.partOfSpeech,
          synonyms: sense.sense.synonyms,
          antonyms: sense.sense.antonyms,
          sentence: null,
          translation: null,
          wordRowspan: totalRows,
          senseRowspan: senseRows,
          isFirstRowOfWord: si === 0,
          isFirstRowOfSense: true,
        });
      } else {
        sense.examples.forEach((example, ei) => {
          rows.push({
            rowId: `w${word.id}-s${si}-e${ei}`,
            wordId: word.id,
            senseIndex: si,
            exampleIndex: ei,
            spelling: word.spelling,
            definition: sense.sense.definition,
            engDefinition: sense.sense.engDefinition,
            britishPhonetic: sense.sense.britishPhonetic,
            americanPhonetic: sense.sense.americanPhonetic,
            partOfSpeech: sense.sense.partOfSpeech,
            synonyms: sense.sense.synonyms,
            antonyms: sense.sense.antonyms,
            sentence: example.example.sentence,
            translation: example.example.translation,
            wordRowspan: totalRows,
            senseRowspan: senseRows,
            isFirstRowOfWord: si === 0 && ei === 0,
            isFirstRowOfSense: ei === 0,
          });
        });
      }
    });
  });

  return rows;
}
