'use client';

import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import {
  useUpdateSharedBundleMetadata,
  useSharedBundleDetail,
} from '@/hooks/library/queries';
import {
  UpdateSharedBundleMetadataRequest,
  UpdateSharedBundleMetadataRequestSchema,
} from '@/types/api/library';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import {
  Form,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
  FormControl,
} from '@/components/ui/form';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { Button } from '@/components/ui/button';

interface EditBundleDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  id: number;
  slug: string;
}

export function EditBundleDialog({
  open,
  onOpenChange,
  id,
  slug,
}: EditBundleDialogProps) {
  const { data: detail } = useSharedBundleDetail(slug);
  const updateMetadata = useUpdateSharedBundleMetadata();

  const form = useForm<UpdateSharedBundleMetadataRequest>({
    resolver: zodResolver(UpdateSharedBundleMetadataRequestSchema),
    defaultValues: {
      title: '',
      description: '',
    },
  });

  useEffect(() => {
    if (detail) {
      form.reset({
        title: detail.title,
        description: detail.description ?? '',
      });
    }
  }, [detail, form]);

  const onSubmit = (data: UpdateSharedBundleMetadataRequest) => {
    updateMetadata.mutate(
      {
        id,
        data: { ...data, description: data.description || null },
      },
      {
        onSuccess: () => {
          onOpenChange(false);
        },
      },
    );
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="bg-zinc-950 border-zinc-800 text-white sm:max-w-md">
        <DialogHeader>
          <DialogTitle className="text-white">번들 수정</DialogTitle>
        </DialogHeader>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="title"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="text-zinc-300">번들 제목</FormLabel>
                  <FormControl>
                    <Input
                      {...field}
                      placeholder="라이브러리에 표시될 제목"
                      className="bg-zinc-900 border-zinc-700 text-white placeholder:text-zinc-500"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="description"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="text-zinc-300">
                    설명{' '}
                    <span className="text-zinc-500 font-normal">(선택)</span>
                  </FormLabel>
                  <FormControl>
                    <Textarea
                      {...field}
                      value={field.value ?? ''}
                      placeholder="번들 설명..."
                      rows={3}
                      className="bg-zinc-900 border-zinc-700 text-white placeholder:text-zinc-500 resize-none"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <div className="flex justify-end gap-2 pt-2">
              <Button
                type="button"
                variant="outline"
                onClick={() => onOpenChange(false)}
                className="bg-transparent border-zinc-700 text-zinc-300 hover:bg-zinc-800 hover:text-white"
              >
                취소
              </Button>
              <Button
                type="submit"
                disabled={updateMetadata.isPending || !detail}
                className="bg-white text-zinc-900 hover:bg-zinc-200"
              >
                {updateMetadata.isPending ? '저장 중...' : '저장'}
              </Button>
            </div>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}
