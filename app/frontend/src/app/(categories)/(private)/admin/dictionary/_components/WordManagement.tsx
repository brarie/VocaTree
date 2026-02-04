'use client';

import { useState, useEffect } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { Search, ExternalLink, GripVertical } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import {
  useWord,
  useReorderSenses,
  useReorderExamples,
} from '@/hooks/dictionary/queries';
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
import {
  DndContext,
  closestCenter,
  KeyboardSensor,
  PointerSensor,
  useSensor,
  useSensors,
  DragEndEvent,
} from '@dnd-kit/core';
import {
  arrayMove,
  SortableContext,
  sortableKeyboardCoordinates,
  useSortable,
  verticalListSortingStrategy,
} from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import type {
  PublicWordSense,
  PublicWordExample,
} from '@/types/api/dictionary';

interface SortableSenseItemProps {
  publicWordSense: PublicWordSense;
  index: number;
  wordId: number;
  onRefetch: () => void;
}

function SortableSenseItem({
  publicWordSense,
  index,
  wordId,
  onRefetch,
}: SortableSenseItemProps) {
  const {
    attributes,
    listeners,
    setNodeRef,
    transform,
    transition,
    isDragging,
  } = useSortable({ id: publicWordSense.id });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    opacity: isDragging ? 0.5 : 1,
  };

  return (
    <div
      ref={setNodeRef}
      style={style}
      className="bg-zinc-800 rounded-lg p-4 border border-zinc-700"
    >
      <div className="flex items-start gap-2">
        <button
          {...attributes}
          {...listeners}
          className="mt-1 cursor-grab active:cursor-grabbing text-gray-500 hover:text-gray-300"
        >
          <GripVertical className="h-5 w-5" />
        </button>
        <div className="flex-1">
          <div className="flex items-center gap-3 mb-1">
            <span className="text-xl font-bold text-gray-500">
              {index + 1}.
            </span>
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
        <div className="flex gap-2">
          <EditSenseDialog
            wordId={wordId}
            publicWordSense={publicWordSense}
            onSuccess={onRefetch}
          />
          <DeleteSenseDialog
            wordId={wordId}
            senseId={publicWordSense.id}
            onSuccess={onRefetch}
          />
        </div>
      </div>

      <ExampleList
        wordId={wordId}
        publicWordSense={publicWordSense}
        onRefetch={onRefetch}
      />
    </div>
  );
}

interface ExampleListProps {
  wordId: number;
  publicWordSense: PublicWordSense;
  onRefetch: () => void;
}

function ExampleList({ wordId, publicWordSense, onRefetch }: ExampleListProps) {
  const sensors = useSensors(
    useSensor(PointerSensor),
    useSensor(KeyboardSensor, {
      coordinateGetter: sortableKeyboardCoordinates,
    }),
  );

  const reorderExamplesMutation = useReorderExamples();
  const [localExamples, setLocalExamples] = useState(publicWordSense.examples);

  useEffect(() => {
    setLocalExamples(publicWordSense.examples);
  }, [publicWordSense.examples]);

  const handleDragEnd = async (event: DragEndEvent) => {
    const { active, over } = event;

    if (over && active.id !== over.id) {
      const oldIndex = localExamples.findIndex((ex) => ex.id === active.id);
      const newIndex = localExamples.findIndex((ex) => ex.id === over.id);

      const newExamples = arrayMove(localExamples, oldIndex, newIndex);
      setLocalExamples(newExamples);

      const orders = newExamples.map((ex, idx) => ({
        id: ex.id,
        displayOrder: idx,
      }));

      await reorderExamplesMutation.mutateAsync({
        wordId,
        senseId: publicWordSense.id,
        data: { orders },
      });
      onRefetch();
    }
  };

  return (
    <div className="mt-4 pt-4 border-t border-zinc-700">
      <div className="flex items-center justify-between mb-2">
        <h4 className="text-sm font-semibold text-gray-400">예문</h4>
        <CreateExampleDialog
          wordId={wordId}
          senseId={publicWordSense.id}
          onSuccess={onRefetch}
        />
      </div>

      {localExamples.length === 0 ? (
        <p className="text-gray-500 text-xs">예문이 없습니다.</p>
      ) : (
        <DndContext
          sensors={sensors}
          collisionDetection={closestCenter}
          onDragEnd={handleDragEnd}
        >
          <SortableContext
            items={localExamples.map((ex) => ex.id)}
            strategy={verticalListSortingStrategy}
          >
            <div className="space-y-2">
              {localExamples.map((publicWordExample) => (
                <SortableExampleItem
                  key={publicWordExample.id}
                  publicWordExample={publicWordExample}
                  wordId={wordId}
                  senseId={publicWordSense.id}
                  onRefetch={onRefetch}
                />
              ))}
            </div>
          </SortableContext>
        </DndContext>
      )}
    </div>
  );
}

interface SortableExampleItemProps {
  publicWordExample: PublicWordExample;
  wordId: number;
  senseId: number;
  onRefetch: () => void;
}

function SortableExampleItem({
  publicWordExample,
  wordId,
  senseId,
  onRefetch,
}: SortableExampleItemProps) {
  const {
    attributes,
    listeners,
    setNodeRef,
    transform,
    transition,
    isDragging,
  } = useSortable({ id: publicWordExample.id });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    opacity: isDragging ? 0.5 : 1,
  };

  return (
    <div
      ref={setNodeRef}
      style={style}
      className="relative group flex items-start gap-2"
    >
      <button
        {...attributes}
        {...listeners}
        className="cursor-grab active:cursor-grabbing text-gray-500 hover:text-gray-300 mt-1"
      >
        <GripVertical className="h-4 w-4" />
      </button>
      <div className="flex-1">
        <DictionaryExample publicWordExample={publicWordExample} />
      </div>
      <div className="flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
        <EditExampleDialog
          wordId={wordId}
          senseId={senseId}
          publicWordExample={publicWordExample}
          onSuccess={onRefetch}
        />
        <DeleteExampleDialog
          wordId={wordId}
          senseId={senseId}
          exampleId={publicWordExample.id}
          onSuccess={onRefetch}
        />
      </div>
    </div>
  );
}

export function WordManagement() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const slugFromUrl = searchParams.get('slug') || '';

  const [searchSlug, setSearchSlug] = useState(slugFromUrl);
  const { data: word, refetch } = useWord(slugFromUrl);

  const sensors = useSensors(
    useSensor(PointerSensor),
    useSensor(KeyboardSensor, {
      coordinateGetter: sortableKeyboardCoordinates,
    }),
  );

  const reorderSensesMutation = useReorderSenses();
  const [localSenses, setLocalSenses] = useState(word?.senses || []);

  useEffect(() => {
    setSearchSlug(slugFromUrl);
  }, [slugFromUrl]);

  useEffect(() => {
    if (word) {
      setLocalSenses(word.senses);
    }
  }, [word]);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (searchSlug.trim()) {
      router.push(`/admin/dictionary?slug=${searchSlug.trim()}`);
    }
  };

  const handleWordCreated = (slug: string) => {
    router.push(`/admin/dictionary?slug=${slug}`);
  };

  const handleDragEnd = async (event: DragEndEvent) => {
    const { active, over } = event;

    if (over && active.id !== over.id && word) {
      const oldIndex = localSenses.findIndex((sense) => sense.id === active.id);
      const newIndex = localSenses.findIndex((sense) => sense.id === over.id);

      const newSenses = arrayMove(localSenses, oldIndex, newIndex);
      setLocalSenses(newSenses);

      const orders = newSenses.map((sense, idx) => ({
        id: sense.id,
        displayOrder: idx,
      }));

      await reorderSensesMutation.mutateAsync({
        wordId: word.id,
        data: { orders },
      });
      refetch();
    }
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

            {localSenses.length === 0 ? (
              <p className="text-gray-400 text-center py-6">
                등록된 의미가 없습니다.
              </p>
            ) : (
              <DndContext
                sensors={sensors}
                collisionDetection={closestCenter}
                onDragEnd={handleDragEnd}
              >
                <SortableContext
                  items={localSenses.map((sense) => sense.id)}
                  strategy={verticalListSortingStrategy}
                >
                  <div className="space-y-4">
                    {localSenses.map((publicWordSense, index) => (
                      <SortableSenseItem
                        key={publicWordSense.id}
                        publicWordSense={publicWordSense}
                        index={index}
                        wordId={word.id}
                        onRefetch={refetch}
                      />
                    ))}
                  </div>
                </SortableContext>
              </DndContext>
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
