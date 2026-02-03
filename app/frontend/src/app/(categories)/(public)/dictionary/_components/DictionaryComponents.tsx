export function DictionaryPhonetics({
  britishPhonetic,
  americanPhonetic,
}: {
  britishPhonetic?: string | null;
  americanPhonetic?: string | null;
}) {
  if (!britishPhonetic && !americanPhonetic) return null;

  return (
    <div className="flex items-center gap-4 text-sm text-gray-400 mb-1">
      {americanPhonetic && (
        <div className="flex items-center gap-1">
          <span className="font-medium">미국</span>
          <span>[{americanPhonetic}]</span>
        </div>
      )}
      {britishPhonetic && (
        <div className="flex items-center gap-1">
          <span className="font-medium">영국</span>
          <span>[{britishPhonetic}]</span>
        </div>
      )}
    </div>
  );
}

export function DictionaryEngDefinition({
  engDefinition,
}: {
  engDefinition?: string | null;
}) {
  if (!engDefinition) return null;

  return <p className="text-sm text-gray-400 italic mb-1">{engDefinition}</p>;
}

export function DictionarySynonymsAntonyms({
  synonyms,
  antonyms,
}: {
  synonyms: string[];
  antonyms: string[];
}) {
  if (synonyms.length === 0 && antonyms.length === 0) return null;

  return (
    <div className="mt-2 space-y-1">
      {synonyms.length > 0 && (
        <div className="flex gap-2 flex-wrap items-center">
          <span className="text-xs text-gray-500">유의어:</span>
          {synonyms.map((syn, idx) => (
            <span
              key={idx}
              className="px-2 py-1 bg-zinc-800 rounded text-xs text-gray-300"
            >
              {syn}
            </span>
          ))}
        </div>
      )}
      {antonyms.length > 0 && (
        <div className="flex gap-2 flex-wrap items-center">
          <span className="text-xs text-gray-500">반의어:</span>
          {antonyms.map((ant, idx) => (
            <span
              key={idx}
              className="px-2 py-1 bg-zinc-800 rounded text-xs text-gray-300"
            >
              {ant}
            </span>
          ))}
        </div>
      )}
    </div>
  );
}
