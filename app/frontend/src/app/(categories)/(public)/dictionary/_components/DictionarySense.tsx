import { getPartOfSpeechKorean } from '@/types/constants/partOfSpeech';
import {
  DictionaryPhonetics,
  DictionaryEngDefinition,
  DictionarySynonymsAntonyms,
} from './DictionaryComponents';
import { DictionaryExample } from './DictionaryExample';
import type { PublicWordSense } from '@/types/api/dictionary';

interface DictionarySenseProps {
  publicWordSense: PublicWordSense;
  index: number;
}

export function DictionarySense({
  publicWordSense,
  index,
}: DictionarySenseProps) {
  return (
    <div className="bg-zinc-900 rounded-lg p-6 border border-zinc-800">
      <div>
        <div className="flex items-center gap-3 mb-1">
          <span className="text-xl font-bold text-gray-500">{index + 1}.</span>
          <p className="text-base mt-0.5">
            {publicWordSense.sense.definition}
          </p>
          {publicWordSense.sense.partOfSpeech && (
            <span className="px-2 py-1 bg-zinc-800 rounded-full text-sm text-gray-300">
              {getPartOfSpeechKorean(publicWordSense.sense.partOfSpeech)}
            </span>
          )}
        </div>

        <DictionaryPhonetics
          britishPhonetic={publicWordSense.sense.britishPhonetic}
          americanPhonetic={publicWordSense.sense.americanPhonetic}
        />

        <DictionaryEngDefinition
          engDefinition={publicWordSense.sense.engDefinition}
        />

        <DictionarySynonymsAntonyms
          synonyms={publicWordSense.sense.synonyms}
          antonyms={publicWordSense.sense.antonyms}
        />
      </div>

      {publicWordSense.examples.length > 0 && (
        <div className="mt-4 pt-4 border-t border-zinc-800">
          <h4 className="text-sm font-semibold text-gray-400 mb-3">예문</h4>
          <div className="space-y-3">
            {publicWordSense.examples.map((publicWordExample) => (
              <DictionaryExample
                key={publicWordExample.id}
                publicWordExample={publicWordExample}
              />
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
