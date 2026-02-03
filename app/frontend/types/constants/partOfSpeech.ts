export const PART_OF_SPEECH = {
  NOUN: {
    value: 'NOUN',
    label: 'NOUN (명사)',
    abbreviation: 'n.',
    korean: '명사',
  },
  VERB: {
    value: 'VERB',
    label: 'VERB (동사)',
    abbreviation: 'v.',
    korean: '동사',
  },
  ADJECTIVE: {
    value: 'ADJECTIVE',
    label: 'ADJECTIVE (형용사)',
    abbreviation: 'adj.',
    korean: '형용사',
  },
  ADVERB: {
    value: 'ADVERB',
    label: 'ADVERB (부사)',
    abbreviation: 'adv.',
    korean: '부사',
  },
  PRONOUN: {
    value: 'PRONOUN',
    label: 'PRONOUN (대명사)',
    abbreviation: 'pron.',
    korean: '대명사',
  },
  PREPOSITION: {
    value: 'PREPOSITION',
    label: 'PREPOSITION (전치사)',
    abbreviation: 'prep.',
    korean: '전치사',
  },
  CONJUNCTION: {
    value: 'CONJUNCTION',
    label: 'CONJUNCTION (접속사)',
    abbreviation: 'conj.',
    korean: '접속사',
  },
  INTERJECTION: {
    value: 'INTERJECTION',
    label: 'INTERJECTION (감탄사)',
    abbreviation: 'interj.',
    korean: '감탄사',
  },
  DETERMINER: {
    value: 'DETERMINER',
    label: 'DETERMINER (관사)',
    abbreviation: 'det.',
    korean: '관사',
  },
  PHRASE: {
    value: 'PHRASE',
    label: 'PHRASE (구)',
    abbreviation: 'phr.',
    korean: '구',
  },
  IDIOM: {
    value: 'IDIOM',
    label: 'IDIOM (숙어)',
    abbreviation: 'idiom',
    korean: '숙어',
  },
} as const;

export const PART_OF_SPEECH_OPTIONS = Object.values(PART_OF_SPEECH);

export type PartOfSpeechValue = keyof typeof PART_OF_SPEECH;

export function getPartOfSpeechAbbreviation(
  value: string | null,
): string | null {
  if (!value) return null;
  const pos = PART_OF_SPEECH[value as PartOfSpeechValue];
  return pos ? pos.abbreviation : value;
}

export function getPartOfSpeechLabel(value: string | null): string | null {
  if (!value) return null;
  const pos = PART_OF_SPEECH[value as PartOfSpeechValue];
  return pos ? pos.label : value;
}

export function getPartOfSpeechKorean(value: string | null): string | null {
  if (!value) return null;
  const pos = PART_OF_SPEECH[value as PartOfSpeechValue];
  return pos ? pos.korean : value;
}
