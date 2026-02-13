import {
  WordbookWordListResponse,
  FlattenedRow,
  WordbookWordResponse,
  ColumnType,
  UpdateWordbookSenseRequest,
  UpdateWordbookExampleRequest,
} from '@/types/api/wordbookWord';
import { PublicWordSense, PublicWordExample } from '@/types/api/dictionary';

/**
 * Flatten hierarchical wordbook data into rows for TanStack Table
 * Calculate rowspan metadata for cell merging
 */
export function flattenWordListToRows(
  wordList: WordbookWordResponse[],
): FlattenedRow[] {
  const rows: FlattenedRow[] = [];

  wordList.forEach((word) => {
    // If word has no senses, create a single row for the word
    if (word.senses.length === 0) {
      rows.push({
        rowId: `word-${word.id}`,
        level: 1,
        wordId: word.id,
        senseId: null,
        exampleId: null,

        spelling: word.spelling,
        wordDisplayOrder: word.displayOrder,

        definition: null,
        engDefinition: null,
        britishPhonetic: null,
        americanPhonetic: null,
        usPhonetic: null,
        ukPhonetic: null,
        partOfSpeech: null,
        synonyms: [],
        antonyms: [],
        senseDisplayOrder: null,

        sentence: null,
        translation: null,
        exampleDisplayOrder: null,

        wordRowspan: 1,
        senseRowspan: 0,
        isFirstRowOfWord: true,
        isFirstRowOfSense: false,
      });
      return; // Skip to next word
    }

    // Calculate total examples in this word for rowspan
    const totalExamplesInWord = word.senses.reduce(
      (sum, sense) => sum + Math.max(1, sense.examples.length),
      0,
    );

    word.senses.forEach((sense, senseIndex) => {
      const exampleCount = Math.max(1, sense.examples.length);

      if (sense.examples.length === 0) {
        // Sense with no examples - still creates one row
        rows.push({
          rowId: `word-${word.id}-sense-${sense.id}`,
          level: 2,
          wordId: word.id,
          senseId: sense.id,
          exampleId: null,

          spelling: word.spelling,
          wordDisplayOrder: word.displayOrder,

          definition: sense.senseResponse.definition,
          engDefinition: sense.senseResponse.engDefinition,
          britishPhonetic: sense.senseResponse.britishPhonetic,
          americanPhonetic: sense.senseResponse.americanPhonetic,
          usPhonetic: sense.senseResponse.americanPhonetic,
          ukPhonetic: sense.senseResponse.britishPhonetic,
          partOfSpeech: sense.senseResponse.partOfSpeech,
          synonyms: sense.senseResponse.synonyms,
          antonyms: sense.senseResponse.antonyms,
          senseDisplayOrder: sense.displayOrder,

          sentence: null,
          translation: null,
          exampleDisplayOrder: null,

          wordRowspan: totalExamplesInWord,
          senseRowspan: exampleCount,
          isFirstRowOfWord: senseIndex === 0,
          isFirstRowOfSense: true,
        });
      } else {
        sense.examples.forEach((example, exampleIndex) => {
          rows.push({
            rowId: `word-${word.id}-sense-${sense.id}-example-${example.id}`,
            level: 3,
            wordId: word.id,
            senseId: sense.id,
            exampleId: example.id,

            spelling: word.spelling,
            wordDisplayOrder: word.displayOrder,

            definition: sense.senseResponse.definition,
            engDefinition: sense.senseResponse.engDefinition,
            britishPhonetic: sense.senseResponse.britishPhonetic,
            americanPhonetic: sense.senseResponse.americanPhonetic,
            usPhonetic: sense.senseResponse.americanPhonetic,
            ukPhonetic: sense.senseResponse.britishPhonetic,
            partOfSpeech: sense.senseResponse.partOfSpeech,
            synonyms: sense.senseResponse.synonyms,
            antonyms: sense.senseResponse.antonyms,
            senseDisplayOrder: sense.displayOrder,

            sentence: example.exampleResponse.sentence,
            translation: example.exampleResponse.translation,
            exampleDisplayOrder: example.displayOrder,

            wordRowspan: totalExamplesInWord,
            senseRowspan: exampleCount,
            isFirstRowOfWord: senseIndex === 0 && exampleIndex === 0,
            isFirstRowOfSense: exampleIndex === 0,
          });
        });
      }
    });
  });

  return rows;
}

/**
 * Get column level (1, 2, or 3)
 */
export function getColumnLevel(columnId: ColumnType | string): 1 | 2 | 3 {
  if (columnId === ColumnType.SPELLING || columnId === 'SPELLING') return 1;
  if (
    columnId === ColumnType.SENTENCE ||
    columnId === ColumnType.TRANSLATION ||
    columnId === 'SENTENCE' ||
    columnId === 'TRANSLATION'
  ) {
    return 3;
  }
  return 2;
}

/**
 * Validate column activation rules
 */
export function canActivateColumn(
  columnId: ColumnType | string,
  activeColumns: Set<ColumnType | string>,
): { canActivate: boolean; reason?: string } {
  // SPELLING always required (cannot toggle)
  if (columnId === ColumnType.SPELLING || columnId === 'SPELLING') {
    return { canActivate: false, reason: '필수 칼럼입니다.' };
  }

  // To enable Level 3 column, must have at least one Level 2 column active
  if (getColumnLevel(columnId as ColumnType) === 3) {
    const hasLevel2Active = Array.from(activeColumns).some(
      (col) => getColumnLevel(col as ColumnType) === 2,
    );
    if (!hasLevel2Active) {
      return {
        canActivate: false,
        reason: 'Level 2 칼럼이 하나 이상 활성화되어야 합니다.',
      };
    }
  }

  return { canActivate: true };
}

/**
 * Validate column deactivation rules
 */
export function canDeactivateColumn(
  columnId: ColumnType | string,
  activeColumns: Set<ColumnType | string>,
): { canDeactivate: boolean; reason?: string } {
  // SPELLING cannot be deactivated
  if (columnId === ColumnType.SPELLING || columnId === 'SPELLING') {
    return { canDeactivate: false, reason: '필수 칼럼입니다.' };
  }

  // If this is the last Level 2 column and there are active Level 3 columns,
  // cannot deactivate
  if (getColumnLevel(columnId as ColumnType) === 2) {
    const level2Columns = Array.from(activeColumns).filter(
      (col) => getColumnLevel(col as ColumnType) === 2,
    );
    const hasLevel3Active = Array.from(activeColumns).some(
      (col) => getColumnLevel(col as ColumnType) === 3,
    );

    if (level2Columns.length === 1 && hasLevel3Active) {
      return {
        canDeactivate: false,
        reason:
          'Level 3 칼럼이 활성화되어 있어 마지막 Level 2 칼럼을 비활성화할 수 없습니다.',
      };
    }
  }

  return { canDeactivate: true };
}

/**
 * Get human-readable column header
 */
export function getColumnHeader(columnType: ColumnType): string {
  const headers: Record<ColumnType, string> = {
    [ColumnType.SPELLING]: '단어',
    [ColumnType.DEFINITION]: '한글 뜻',
    [ColumnType.ENG_DEFINITION]: '영어 뜻',
    [ColumnType.US_PHONETIC]: '미국 발음',
    [ColumnType.UK_PHONETIC]: '영국 발음',
    [ColumnType.PART_OF_SPEECH]: '품사',
    [ColumnType.SYNONYMS]: '유의어',
    [ColumnType.ANTONYMS]: '반의어',
    [ColumnType.SENTENCE]: '예문',
    [ColumnType.TRANSLATION]: '번역',
  };
  return headers[columnType];
}

/**
 * Get field name for API requests
 */
export function getFieldName(columnType: ColumnType): string {
  const fieldMap: Record<ColumnType, string> = {
    [ColumnType.SPELLING]: 'spelling',
    [ColumnType.DEFINITION]: 'definition',
    [ColumnType.ENG_DEFINITION]: 'engDefinition',
    [ColumnType.US_PHONETIC]: 'americanPhonetic',
    [ColumnType.UK_PHONETIC]: 'britishPhonetic',
    [ColumnType.PART_OF_SPEECH]: 'partOfSpeech',
    [ColumnType.SYNONYMS]: 'synonyms',
    [ColumnType.ANTONYMS]: 'antonyms',
    [ColumnType.SENTENCE]: 'sentence',
    [ColumnType.TRANSLATION]: 'translation',
  };

  return fieldMap[columnType];
}

/**
 * Get active columns from column settings
 */
export function getActiveColumns(
  columnsData: WordbookWordListResponse | { columns: Record<ColumnType, { isActive: boolean }> } | undefined
): Set<ColumnType> {
  if (!columnsData) return new Set<ColumnType>();

  const columns = 'columns' in columnsData ? columnsData.columns : {};

  return new Set(
    Object.entries(columns)
      .filter(([_, setting]) => (setting as { isActive: boolean }).isActive)
      .map(([key, _]) => key as ColumnType)
  );
}

/**
 * Filter sense data to only include active columns
 */
export function filterSenseData(
  sense: PublicWordSense,
  activeColumns: Set<ColumnType>
): UpdateWordbookSenseRequest {
  const data: UpdateWordbookSenseRequest = {};

  if (activeColumns.has(ColumnType.DEFINITION)) {
    data.definition = sense.sense.definition;
  }
  if (activeColumns.has(ColumnType.ENG_DEFINITION)) {
    data.engDefinition = sense.sense.engDefinition;
  }
  if (activeColumns.has(ColumnType.PART_OF_SPEECH)) {
    data.partOfSpeech = sense.sense.partOfSpeech;
  }
  if (activeColumns.has(ColumnType.US_PHONETIC)) {
    data.americanPhonetic = sense.sense.americanPhonetic;
  }
  if (activeColumns.has(ColumnType.UK_PHONETIC)) {
    data.britishPhonetic = sense.sense.britishPhonetic;
  }
  if (activeColumns.has(ColumnType.SYNONYMS)) {
    data.synonyms = sense.sense.synonyms;
  }
  if (activeColumns.has(ColumnType.ANTONYMS)) {
    data.antonyms = sense.sense.antonyms;
  }

  return data;
}

/**
 * Filter example data to only include active columns
 */
export function filterExampleData(
  example: PublicWordExample,
  activeColumns: Set<ColumnType>
): UpdateWordbookExampleRequest {
  const data: UpdateWordbookExampleRequest = {};

  if (activeColumns.has(ColumnType.SENTENCE)) {
    data.sentence = example.example.sentence;
  }
  if (activeColumns.has(ColumnType.TRANSLATION)) {
    data.translation = example.example.translation;
  }

  return data;
}
