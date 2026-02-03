'use client';

import { useState, useEffect } from 'react';
import { Plus, Edit, Trash2 } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { Switch } from '@/components/ui/switch';
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
  useCreateExample,
  useUpdateExample,
  useDeleteExample,
} from '@/hooks/dictionary/queries';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import {
  CreateExampleRequestSchema,
  UpdateExampleRequestSchema,
  CreateExampleRequest,
  UpdateExampleRequest,
  PublicWordExample,
} from '@/types/api/dictionary';

export function CreateExampleDialog({
  wordId,
  senseId,
  onSuccess,
}: {
  wordId: number;
  senseId: number;
  onSuccess: () => void;
}) {
  const [open, setOpen] = useState(false);
  const createMutation = useCreateExample();
  const form = useForm({
    resolver: zodResolver(CreateExampleRequestSchema),
    defaultValues: {
      sentence: '',
      translation: '',
      isOfficial: false,
      sourceInfo: '',
    },
  });

  const onSubmit = async (data: CreateExampleRequest) => {
    const cleanedData = {
      sentence: data.sentence,
      translation: data.translation || undefined,
      isOfficial: data.isOfficial,
      sourceInfo: data.sourceInfo || undefined,
    };
    await createMutation.mutateAsync({
      wordId,
      senseId,
      data: cleanedData,
    });
    setOpen(false);
    form.reset();
    onSuccess();
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button
          size="sm"
          variant="outline"
          className="bg-white/5 border-white/10 text-white hover:bg-white/10"
        >
          <Plus className="h-3 w-3 mr-1" />
          예문
        </Button>
      </DialogTrigger>
      <DialogContent className="bg-zinc-900 border-zinc-800 max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle className="text-white">새 예문 추가</DialogTitle>
          <DialogDescription className="text-gray-400">
            새로운 예문을 추가합니다.
          </DialogDescription>
        </DialogHeader>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="sentence"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="text-white">예문 *</FormLabel>
                  <FormControl>
                    <Textarea
                      {...field}
                      placeholder="예: Does God exist?"
                      className="bg-zinc-800 border-zinc-700 text-white"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="translation"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="text-white">한글 번역</FormLabel>
                  <FormControl>
                    <Textarea
                      {...field}
                      placeholder="예: 신은 존재하는가?"
                      className="bg-zinc-800 border-zinc-700 text-white"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="isOfficial"
              render={({ field }) => (
                <FormItem className="flex items-center gap-2 space-y-0">
                  <FormControl>
                    <Switch
                      checked={field.value}
                      onCheckedChange={field.onChange}
                    />
                  </FormControl>
                  <FormLabel className="text-white">
                    신뢰할 수 있는 출처
                  </FormLabel>
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="sourceInfo"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="text-white">출처 정보</FormLabel>
                  <FormControl>
                    <Input
                      {...field}
                      placeholder="예: Oxford Dictionary"
                      className="bg-zinc-800 border-zinc-700 text-white"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
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

export function EditExampleDialog({
  wordId,
  senseId,
  example,
  onSuccess,
}: {
  wordId: number;
  senseId: number;
  example: PublicWordExample;
  onSuccess: () => void;
}) {
  const [open, setOpen] = useState(false);
  const updateMutation = useUpdateExample();
  const form = useForm({
    resolver: zodResolver(UpdateExampleRequestSchema),
    defaultValues: {
      sentence: example.sentence,
      translation: example.translation || '',
      isOfficial: example.isOfficial,
      sourceInfo: example.sourceInfo || '',
    },
  });

  useEffect(() => {
    form.reset({
      sentence: example.sentence,
      translation: example.translation || '',
      isOfficial: example.isOfficial,
      sourceInfo: example.sourceInfo || '',
    });
  }, [example, form]);

  const onSubmit = async (data: UpdateExampleRequest) => {
    const cleanedData = {
      sentence: data.sentence || undefined,
      translation: data.translation || undefined,
      isOfficial: data.isOfficial,
      sourceInfo: data.sourceInfo || undefined,
    };
    await updateMutation.mutateAsync({
      wordId,
      senseId,
      exampleId: example.id,
      data: cleanedData,
    });
    setOpen(false);
    onSuccess();
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button variant="ghost" size="sm">
          <Edit className="h-3 w-3" />
        </Button>
      </DialogTrigger>
      <DialogContent className="bg-zinc-900 border-zinc-800 max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle className="text-white">예문 수정</DialogTitle>
          <DialogDescription className="text-gray-400">
            예문을 수정합니다.
          </DialogDescription>
        </DialogHeader>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="sentence"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="text-white">예문</FormLabel>
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
              name="translation"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="text-white">한글 번역</FormLabel>
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
              name="isOfficial"
              render={({ field }) => (
                <FormItem className="flex items-center gap-2 space-y-0">
                  <FormControl>
                    <Switch
                      checked={field.value}
                      onCheckedChange={field.onChange}
                    />
                  </FormControl>
                  <FormLabel className="text-white">
                    신뢰할 수 있는 출처
                  </FormLabel>
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="sourceInfo"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="text-white">출처 정보</FormLabel>
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

export function DeleteExampleDialog({
  wordId,
  senseId,
  exampleId,
  onSuccess,
}: {
  wordId: number;
  senseId: number;
  exampleId: number;
  onSuccess: () => void;
}) {
  const deleteMutation = useDeleteExample();

  const handleDelete = async () => {
    await deleteMutation.mutateAsync({ wordId, senseId, exampleId });
    onSuccess();
  };

  return (
    <AlertDialog>
      <AlertDialogTrigger asChild>
        <Button variant="ghost" size="sm">
          <Trash2 className="h-3 w-3" />
        </Button>
      </AlertDialogTrigger>
      <AlertDialogContent className="bg-zinc-900 border-zinc-800">
        <AlertDialogHeader>
          <AlertDialogTitle className="text-white">예문 삭제</AlertDialogTitle>
          <AlertDialogDescription className="text-gray-400">
            이 예문을 삭제하시겠습니까?
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
