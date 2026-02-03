import Link from 'next/link';
import { SquareArrowOutUpRight } from 'lucide-react';
import type { PublicWordSimpleView } from '@/types/api/dictionary';

interface SimpleWordListProps {
  words: PublicWordSimpleView[];
}

export function SimpleWordList({ words }: SimpleWordListProps) {
  return (
    <div className="space-y-3">
      {words.map((word) => (
        <Link
          key={word.id}
          href={`/dictionary/${word.slug}`}
          className="block bg-zinc-900 rounded-lg p-5 hover:bg-zinc-800 transition-colors border border-zinc-800 group"
        >
          <div className="flex items-start gap-4">
            <div className="flex-1">
              <div className="flex items-center gap-2 mb-2">
                <h3 className="text-xl font-bold">{word.spelling}</h3>
                <SquareArrowOutUpRight className="h-4 w-4 text-gray-600 group-hover:text-white transition-colors" />
              </div>
              {word.topDefinitions.length > 0 && (
                <p className="text-sm text-gray-400">
                  {word.topDefinitions.join(', ')}
                </p>
              )}
            </div>
          </div>
        </Link>
      ))}
    </div>
  );
}
