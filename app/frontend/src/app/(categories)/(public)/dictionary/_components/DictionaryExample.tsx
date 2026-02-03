import { CheckCircle } from 'lucide-react';
import type { PublicWordExample } from '@/types/api/dictionary';

interface DictionaryExampleProps {
  example: PublicWordExample;
}

export function DictionaryExample({ example }: DictionaryExampleProps) {
  return (
    <div className="pl-4 border-l-2 border-zinc-700">
      <p className="text-sm text-gray-200 mb-1 flex items-center gap-2">
        <span>{example.sentence}</span>
        {example.isOfficial && (
          <CheckCircle className="h-4 w-4 text-blue-400 shrink-0" />
        )}
      </p>
      {example.translation && (
        <p className="text-sm text-gray-400">{example.translation}</p>
      )}
      {example.sourceInfo && (
        <p className="text-xs text-gray-500 mt-1">출처: {example.sourceInfo}</p>
      )}
    </div>
  );
}
