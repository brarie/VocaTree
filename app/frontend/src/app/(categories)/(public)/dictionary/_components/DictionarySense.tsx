import { getPartOfSpeechKorean } from '@/types/constants/partOfSpeech';
import {
  DictionaryPhonetics,
  DictionaryEngDefinition,
  DictionarySynonymsAntonyms,
} from './DictionaryComponents';
import { DictionaryExample } from './DictionaryExample';
import type { PublicWordSense } from '@/types/api/dictionary';

interface DictionarySenseProps {
  sense: PublicWordSense;
  index: number;
}

export function DictionarySense({ sense, index }: DictionarySenseProps) {
  return (
    <div className="bg-zinc-900 rounded-lg p-6 border border-zinc-800">
      <div>
        <div className="flex items-center gap-3 mb-1">
          <span className="text-xl font-bold text-gray-500">{index + 1}.</span>
          <p className="text-base mt-0.5">{sense.definition}</p>
          {sense.partOfSpeech && (
            <span className="px-2 py-1 bg-zinc-800 rounded-full text-sm text-gray-300">
              {getPartOfSpeechKorean(sense.partOfSpeech)}
            </span>
          )}
        </div>

        <DictionaryPhonetics
          britishPhonetic={sense.britishPhonetic}
          americanPhonetic={sense.americanPhonetic}
        />

        <DictionaryEngDefinition engDefinition={sense.engDefinition} />

        <DictionarySynonymsAntonyms
          synonyms={sense.synonyms}
          antonyms={sense.antonyms}
        />
      </div>

      {sense.examples.length > 0 && (
        <div className="mt-4 pt-4 border-t border-zinc-800">
          <h4 className="text-sm font-semibold text-gray-400 mb-3">예문</h4>
          <div className="space-y-3">
            {sense.examples.map((example) => (
              <DictionaryExample key={example.id} example={example} />
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
