'use client';

import { useState, useEffect } from 'react';
import { Plus, Edit, Trash2 } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
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
  useCreateWord,
  useUpdateWord,
  useDeleteWord,
} from '@/hooks/dictionary/queries';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import {
  CreateWordRequestSchema,
  UpdateWordRequestSchema,
  CreateWordRequest,
  UpdateWordRequest,
} from '@/types/api/dictionary';

export function CreateWordDialog({
  onSuccess,
}: {
  onSuccess: (slug: string) => void;
}) {
  const [open, setOpen] = useState(false);
  const createMutation = useCreateWord();
  const form = useForm({
    resolver: zodResolver(CreateWordRequestSchema),
    defaultValues: { spelling: '' },
  });

  const onSubmit = async (data: CreateWordRequest) => {
    const result = await createMutation.mutateAsync(data);
    setOpen(false);
    form.reset();
    onSuccess(result.slug);
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button className="bg-white text-black hover:bg-gray-200">
          <Plus className="h-4 w-4 mr-2" />
          단어 추가
        </Button>
      </DialogTrigger>
      <DialogContent className="bg-zinc-900 border-zinc-800">
        <DialogHeader>
          <DialogTitle className="text-white">새 단어 추가</DialogTitle>
          <DialogDescription className="text-gray-400">
            새로운 영어 단어를 추가합니다.
          </DialogDescription>
        </DialogHeader>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="spelling"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="text-white">단어</FormLabel>
                  <FormControl>
                    <Input
                      {...field}
                      placeholder="예: exist"
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

export function EditWordDialog({
  word,
  onSuccess,
}: {
  word: { id: number; spelling: string };
  onSuccess: () => void;
}) {
  const [open, setOpen] = useState(false);
  const updateMutation = useUpdateWord();
  const form = useForm({
    resolver: zodResolver(UpdateWordRequestSchema),
    defaultValues: { spelling: word.spelling },
  });

  useEffect(() => {
    form.reset({ spelling: word.spelling });
  }, [word, form]);

  const onSubmit = async (data: UpdateWordRequest) => {
    await updateMutation.mutateAsync({ wordId: word.id, data });
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
          <Edit className="h-4 w-4" />
        </Button>
      </DialogTrigger>
      <DialogContent className="bg-zinc-900 border-zinc-800">
        <DialogHeader>
          <DialogTitle className="text-white">단어 수정</DialogTitle>
        </DialogHeader>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="spelling"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="text-white">단어</FormLabel>
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

export function DeleteWordDialog({
  wordId,
  spelling,
  onSuccess,
}: {
  wordId: number;
  spelling: string;
  onSuccess: () => void;
}) {
  const deleteMutation = useDeleteWord();

  const handleDelete = async () => {
    await deleteMutation.mutateAsync(wordId);
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
          <Trash2 className="h-4 w-4" />
        </Button>
      </AlertDialogTrigger>
      <AlertDialogContent className="bg-zinc-900 border-zinc-800">
        <AlertDialogHeader>
          <AlertDialogTitle className="text-white">
            &quot;{spelling}&quot; 삭제
          </AlertDialogTitle>
          <AlertDialogDescription className="text-gray-400">
            이 단어와 모든 의미, 예문이 삭제됩니다. 이 작업은 되돌릴 수
            없습니다.
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
