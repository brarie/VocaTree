'use client';

import { useState, useEffect } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { Search, ExternalLink } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { useWord } from '@/hooks/dictionary/queries';
import { getPartOfSpeechKorean } from '@/types/constants/partOfSpeech';
import {
  CreateWordDialog,
  EditWordDialog,
  DeleteWordDialog,
} from './WordDialog';
import {
  CreateSenseDialog,
  EditSenseDialog,
  DeleteSenseDialog,
} from './SenseDialog';
import {
  CreateExampleDialog,
  EditExampleDialog,
  DeleteExampleDialog,
} from './ExampleDialog';
import Link from 'next/link';
import {
  DictionaryPhonetics,
  DictionaryEngDefinition,
  DictionarySynonymsAntonyms,
} from '@/src/app/(categories)/(public)/dictionary/_components/DictionaryComponents';
import { DictionaryExample } from '@/src/app/(categories)/(public)/dictionary/_components/DictionaryExample';

export function WordManagement() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const slugFromUrl = searchParams.get('slug') || '';

  const [searchSlug, setSearchSlug] = useState(slugFromUrl);
  const { data: word, refetch } = useWord(slugFromUrl);

  useEffect(() => {
    setSearchSlug(slugFromUrl);
  }, [slugFromUrl]);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (searchSlug.trim()) {
      router.push(`/admin/dictionary?slug=${searchSlug.trim()}`);
    }
  };

  const handleWordCreated = (slug: string) => {
    router.push(`/admin/dictionary?slug=${slug}`);
  };

  return (
    <div className="bg-white/5 border border-white/10 rounded-lg p-6">
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-xl font-semibold text-white">단어 검색</h2>
        <CreateWordDialog onSuccess={handleWordCreated} />
      </div>

      <form onSubmit={handleSearch} className="flex gap-2 mb-6">
        <Input
          type="text"
          value={searchSlug}
          onChange={(e) => setSearchSlug(e.target.value)}
          placeholder="단어의 slug를 입력하세요 (예: exist, well-known)"
          className="flex-1 bg-zinc-900 border-zinc-800"
        />
        <Button type="submit">
          <Search className="h-4 w-4 mr-2" />
          검색
        </Button>
      </form>

      {word && (
        <div className="space-y-4">
          <div className="bg-zinc-900 rounded-lg p-6 border border-zinc-800">
            <div className="flex items-center justify-between mb-4">
              <div>
                <h2 className="text-2xl font-bold">{word.spelling}</h2>
                <p className="text-sm text-gray-400">Slug: {word.slug}</p>
              </div>
              <div className="flex gap-2">
                <Link href={`/dictionary/${word.slug}`} target="_blank">
                  <Button
                    variant="outline"
                    size="sm"
                    className="bg-white/5 border-white/10 text-white hover:bg-white/10"
                  >
                    <ExternalLink className="h-4 w-4" />
                  </Button>
                </Link>
                <EditWordDialog word={word} onSuccess={refetch} />
                <DeleteWordDialog
                  wordId={word.id}
                  spelling={word.spelling}
                  onSuccess={() => {
                    router.push('/admin/dictionary');
                  }}
                />
              </div>
            </div>

            <div className="flex items-center justify-between mb-4">
              <h3 className="text-lg font-semibold">의미 목록</h3>
              <CreateSenseDialog wordId={word.id} onSuccess={refetch} />
            </div>

            {word.senses.length === 0 ? (
              <p className="text-gray-400 text-center py-6">
                등록된 의미가 없습니다.
              </p>
            ) : (
              <div className="space-y-4">
                {word.senses.map((sense, index) => (
                  <div
                    key={sense.id}
                    className="bg-zinc-800 rounded-lg p-4 border border-zinc-700"
                  >
                    <div className="flex items-start justify-between">
                      <div className="flex-1">
                        <div className="flex items-center gap-3 mb-1">
                          <span className="text-xl font-bold text-gray-500">
                            {index + 1}.
                          </span>
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
                        <DictionaryEngDefinition
                          engDefinition={sense.engDefinition}
                        />
                        <DictionarySynonymsAntonyms
                          synonyms={sense.synonyms}
                          antonyms={sense.antonyms}
                        />
                      </div>
                      <div className="flex gap-2">
                        <EditSenseDialog
                          wordId={word.id}
                          sense={sense}
                          onSuccess={refetch}
                        />
                        <DeleteSenseDialog
                          wordId={word.id}
                          senseId={sense.id}
                          onSuccess={refetch}
                        />
                      </div>
                    </div>

                    <div className="mt-4 pt-4 border-t border-zinc-700">
                      <div className="flex items-center justify-between mb-2">
                        <h4 className="text-sm font-semibold text-gray-400">
                          예문
                        </h4>
                        <CreateExampleDialog
                          wordId={word.id}
                          senseId={sense.id}
                          onSuccess={refetch}
                        />
                      </div>

                      {sense.examples.length === 0 ? (
                        <p className="text-gray-500 text-xs">
                          예문이 없습니다.
                        </p>
                      ) : (
                        <div className="space-y-2">
                          {sense.examples.map((example) => (
                            <div key={example.id} className="relative group">
                              <DictionaryExample example={example} />
                              <div className="absolute top-0 right-0 flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                                <EditExampleDialog
                                  wordId={word.id}
                                  senseId={sense.id}
                                  example={example}
                                  onSuccess={refetch}
                                />
                                <DeleteExampleDialog
                                  wordId={word.id}
                                  senseId={sense.id}
                                  exampleId={example.id}
                                  onSuccess={refetch}
                                />
                              </div>
                            </div>
                          ))}
                        </div>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      )}

      {slugFromUrl && !word && (
        <div className="text-center py-12 bg-zinc-900 rounded-lg border border-zinc-800">
          <p className="text-gray-400">해당 단어를 찾을 수 없습니다.</p>
        </div>
      )}
    </div>
  );
}
