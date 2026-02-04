'use client';

import { useState, useEffect } from 'react';
import { Plus, Edit, Trash2 } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog';
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from '@/components/ui/alert-dialog';
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import {
  useCreateSense,
  useUpdateSense,
  useDeleteSense,
} from '@/hooks/dictionary/queries';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import {
  CreateSenseRequestSchema,
  UpdateSenseRequestSchema,
  CreateSenseRequest,
  UpdateSenseRequest,
  PublicWordSense,
} from '@/types/api/dictionary';
import { PART_OF_SPEECH_OPTIONS } from '@/types/constants/partOfSpeech';

export function CreateSenseDialog({
  wordId,
  onSuccess,
}: {
  wordId: number;
  onSuccess: () => void;
}) {
  const [open, setOpen] = useState(false);
  const [synonymsText, setSynonymsText] = useState('');
  const [antonymsText, setAntonymsText] = useState('');
  const createMutation = useCreateSense();
  const form = useForm({
    resolver: zodResolver(CreateSenseRequestSchema),
    defaultValues: {
      definition: '',
      engDefinition: '',
      britishPhonetic: '',
      americanPhonetic: '',
      partOfSpeech: '',
    },
  });

  const onSubmit = async (data: CreateSenseRequest) => {
    const synonymsArray = synonymsText
      ? synonymsText
          .split(',')
          .map((s) => s.trim())
          .filter((s) => s)
      : undefined;
    const antonymsArray = antonymsText
      ? antonymsText
          .split(',')
          .map((s) => s.trim())
          .filter((s) => s)
      : undefined;

    const cleanedData = {
      definition: data.definition,
      engDefinition: data.engDefinition || undefined,
      britishPhonetic: data.britishPhonetic || undefined,
      americanPhonetic: data.americanPhonetic || undefined,
      partOfSpeech: data.partOfSpeech || undefined,
      synonyms: synonymsArray,
      antonyms: antonymsArray,
    };
    await createMutation.mutateAsync({ wordId, data: cleanedData });
    setOpen(false);
    form.reset();
    setSynonymsText('');
    setAntonymsText('');
    onSuccess();
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button size="sm">
          <Plus className="h-4 w-4 mr-2" />
          의미 추가
        </Button>
      </DialogTrigger>
      <DialogContent className="bg-zinc-900 border-zinc-800 max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle className="text-white">새 의미 추가</DialogTitle>
          <DialogDescription className="text-gray-400">
            새로운 의미를 추가합니다.
          </DialogDescription>
        </DialogHeader>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="definition"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="text-white">한글 뜻 *</FormLabel>
                  <FormControl>
                    <Textarea
                      {...field}
                      placeholder="예: 존재하다"
                      className="bg-zinc-800 border-zinc-700 text-white"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="engDefinition"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="text-white">영영 뜻</FormLabel>
                  <FormControl>
                    <Textarea
                      {...field}
                      placeholder="예: to be real or present"
                      className="bg-zinc-800 border-zinc-700 text-white"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <div className="grid grid-cols-2 gap-4">
              <FormField
                control={form.control}
                name="britishPhonetic"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel className="text-white">영국식 발음</FormLabel>
                    <FormControl>
                      <Input
                        {...field}
                        placeholder="/ɪɡˈzɪst/"
                        className="bg-zinc-800 border-zinc-700 text-white"
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="americanPhonetic"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel className="text-white">미국식 발음</FormLabel>
                    <FormControl>
                      <Input
                        {...field}
                        placeholder="/ɪɡˈzɪst/"
                        className="bg-zinc-800 border-zinc-700 text-white"
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>
            <FormField
              control={form.control}
              name="partOfSpeech"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="text-white">품사</FormLabel>
                  <Select onValueChange={field.onChange} value={field.value}>
                    <FormControl>
                      <SelectTrigger className="bg-zinc-800 border-zinc-700 text-white">
                        <SelectValue placeholder="품사를 선택하세요" />
                      </SelectTrigger>
                    </FormControl>
                    <SelectContent className="bg-zinc-800 border-zinc-700">
                      {PART_OF_SPEECH_OPTIONS.map((pos) => (
                        <SelectItem
                          key={pos.value}
                          value={pos.value}
                          className="text-white hover:bg-zinc-700 focus:bg-zinc-700"
                        >
                          {pos.label}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormItem>
              <FormLabel className="text-white">유의어</FormLabel>
              <FormControl>
                <Textarea
                  value={synonymsText}
                  onChange={(e) => setSynonymsText(e.target.value)}
                  placeholder="예: big, large, huge (쉼표로 구분)"
                  className="bg-zinc-800 border-zinc-700 text-white"
                />
              </FormControl>
            </FormItem>
            <FormItem>
              <FormLabel className="text-white">반의어</FormLabel>
              <FormControl>
                <Textarea
                  value={antonymsText}
                  onChange={(e) => setAntonymsText(e.target.value)}
                  placeholder="예: small, tiny, little (쉼표로 구분)"
                  className="bg-zinc-800 border-zinc-700 text-white"
                />
              </FormControl>
            </FormItem>
            <DialogFooter>
              <Button
                type="button"
                variant="outline"
                onClick={() => setOpen(false)}
                className="bg-white/5 border-white/10 text-white hover:bg-white/10"
              >
                취소
              </Button>
              <Button
                type="submit"
                disabled={createMutation.isPending}
                className="bg-white text-black hover:bg-gray-200"
              >
                {createMutation.isPending ? '추가 중...' : '추가'}
              </Button>
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}

export function EditSenseDialog({
  wordId,
  publicWordSense,
  onSuccess,
}: {
  wordId: number;
  publicWordSense: PublicWordSense;
  onSuccess: () => void;
}) {
  const [open, setOpen] = useState(false);
  const [synonymsText, setSynonymsText] = useState(
    publicWordSense.sense.synonyms?.join(', ') || '',
  );
  const [antonymsText, setAntonymsText] = useState(
    publicWordSense.sense.antonyms?.join(', ') || '',
  );
  const updateMutation = useUpdateSense();
  const form = useForm({
    resolver: zodResolver(UpdateSenseRequestSchema),
    defaultValues: {
      definition: publicWordSense.sense.definition || '',
      engDefinition: publicWordSense.sense.engDefinition || '',
      britishPhonetic: publicWordSense.sense.britishPhonetic || '',
      americanPhonetic: publicWordSense.sense.americanPhonetic || '',
      partOfSpeech: publicWordSense.sense.partOfSpeech || '',
    },
  });

  useEffect(() => {
    form.reset({
      definition: publicWordSense.sense.definition || '',
      engDefinition: publicWordSense.sense.engDefinition || '',
      britishPhonetic: publicWordSense.sense.britishPhonetic || '',
      americanPhonetic: publicWordSense.sense.americanPhonetic || '',
      partOfSpeech: publicWordSense.sense.partOfSpeech || '',
    });
    setSynonymsText(publicWordSense.sense.synonyms?.join(', ') || '');
    setAntonymsText(publicWordSense.sense.antonyms?.join(', ') || '');
  }, [publicWordSense, form]);

  const onSubmit = async (data: UpdateSenseRequest) => {
    const synonymsArray = synonymsText
      ? synonymsText
          .split(',')
          .map((s) => s.trim())
          .filter((s) => s)
      : undefined;
    const antonymsArray = antonymsText
      ? antonymsText
          .split(',')
          .map((s) => s.trim())
          .filter((s) => s)
      : undefined;

    const cleanedData = {
      definition: data.definition || undefined,
      engDefinition: data.engDefinition || undefined,
      britishPhonetic: data.britishPhonetic || undefined,
      americanPhonetic: data.americanPhonetic || undefined,
      partOfSpeech: data.partOfSpeech || undefined,
      synonyms:
        synonymsArray && synonymsArray.length > 0 ? synonymsArray : undefined,
      antonyms:
        antonymsArray && antonymsArray.length > 0 ? antonymsArray : undefined,
    };
    await updateMutation.mutateAsync({
      wordId,
      senseId: publicWordSense.id,
      data: cleanedData,
    });
    setOpen(false);
    onSuccess();
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button
          variant="outline"
          size="sm"
          className="bg-white/5 border-white/10 text-white hover:bg-white/10"
        >
          <Edit className="h-3 w-3" />
        </Button>
      </DialogTrigger>
      <DialogContent className="bg-zinc-900 border-zinc-800 max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle className="text-white">의미 수정</DialogTitle>
          <DialogDescription className="text-gray-400">
            의미를 수정합니다.
          </DialogDescription>
        </DialogHeader>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="definition"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="text-white">한글 뜻</FormLabel>
                  <FormControl>
                    <Textarea
                      {...field}
                      className="bg-zinc-800 border-zinc-700 text-white"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="engDefinition"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="text-white">영영 뜻</FormLabel>
                  <FormControl>
                    <Textarea
                      {...field}
                      className="bg-zinc-800 border-zinc-700 text-white"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <div className="grid grid-cols-2 gap-4">
              <FormField
                control={form.control}
                name="britishPhonetic"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel className="text-white">영국식 발음</FormLabel>
                    <FormControl>
                      <Input
                        {...field}
                        className="bg-zinc-800 border-zinc-700 text-white"
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="americanPhonetic"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel className="text-white">미국식 발음</FormLabel>
                    <FormControl>
                      <Input
                        {...field}
                        className="bg-zinc-800 border-zinc-700 text-white"
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>
            <FormField
              control={form.control}
              name="partOfSpeech"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="text-white">품사</FormLabel>
                  <Select onValueChange={field.onChange} value={field.value}>
                    <FormControl>
                      <SelectTrigger className="bg-zinc-800 border-zinc-700 text-white">
                        <SelectValue placeholder="품사를 선택하세요" />
                      </SelectTrigger>
                    </FormControl>
                    <SelectContent className="bg-zinc-800 border-zinc-700">
                      {PART_OF_SPEECH_OPTIONS.map((pos) => (
                        <SelectItem
                          key={pos.value}
                          value={pos.value}
                          className="text-white hover:bg-zinc-700 focus:bg-zinc-700"
                        >
                          {pos.label}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormItem>
              <FormLabel className="text-white">유의어</FormLabel>
              <FormControl>
                <Textarea
                  value={synonymsText}
                  onChange={(e) => setSynonymsText(e.target.value)}
                  placeholder="예: big, large, huge (쉼표로 구분)"
                  className="bg-zinc-800 border-zinc-700 text-white"
                />
              </FormControl>
            </FormItem>
            <FormItem>
              <FormLabel className="text-white">반의어</FormLabel>
              <FormControl>
                <Textarea
                  value={antonymsText}
                  onChange={(e) => setAntonymsText(e.target.value)}
                  placeholder="예: small, tiny, little (쉼표로 구분)"
                  className="bg-zinc-800 border-zinc-700 text-white"
                />
              </FormControl>
            </FormItem>
            <DialogFooter>
              <Button
                type="button"
                variant="outline"
                onClick={() => setOpen(false)}
                className="bg-white/5 border-white/10 text-white hover:bg-white/10"
              >
                취소
              </Button>
              <Button
                type="submit"
                disabled={updateMutation.isPending}
                className="bg-white text-black hover:bg-gray-200"
              >
                {updateMutation.isPending ? '수정 중...' : '수정'}
              </Button>
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}

export function DeleteSenseDialog({
  wordId,
  senseId,
  onSuccess,
}: {
  wordId: number;
  senseId: number;
  onSuccess: () => void;
}) {
  const deleteMutation = useDeleteSense();

  const handleDelete = async () => {
    await deleteMutation.mutateAsync({ wordId, senseId });
    onSuccess();
  };

  return (
    <AlertDialog>
      <AlertDialogTrigger asChild>
        <Button
          variant="outline"
          size="sm"
          className="bg-white/5 border-white/10 text-red-400 hover:bg-red-500/10"
        >
          <Trash2 className="h-3 w-3" />
        </Button>
      </AlertDialogTrigger>
      <AlertDialogContent className="bg-zinc-900 border-zinc-800">
        <AlertDialogHeader>
          <AlertDialogTitle className="text-white">의미 삭제</AlertDialogTitle>
          <AlertDialogDescription className="text-gray-400">
            이 의미와 모든 예문이 삭제됩니다.
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel className="bg-zinc-800 text-white border-zinc-700">
            취소
          </AlertDialogCancel>
          <AlertDialogAction
            onClick={handleDelete}
            className="bg-red-600 hover:bg-red-700"
          >
            삭제
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
}
