'use client';

import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import {
  UpdateWordbookTitleRequestSchema,
  UpdateWordbookTitleRequest,
  NodeResponseWithCountView,
  NodeType,
} from '@/types/api/wordbook';
import {
  useUpdateWordbookTitle,
  useUpdateBundleTitle,
} from '@/hooks/wordbook/queries';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';

interface EditDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  node: NodeResponseWithCountView;
}

export function EditDialog({ open, onOpenChange, node }: EditDialogProps) {
  const updateWordbookTitle = useUpdateWordbookTitle();
  const updateBundleTitle = useUpdateBundleTitle();

  const isBundle = node.nodeType === NodeType.BUNDLE;

  const form = useForm<UpdateWordbookTitleRequest>({
    resolver: zodResolver(UpdateWordbookTitleRequestSchema),
    defaultValues: {
      title: node.title,
    },
  });

  useEffect(() => {
    if (open) {
      form.reset({ title: node.title });
    }
  }, [open, node.title, form]);

  const onSubmit = async (data: UpdateWordbookTitleRequest) => {
    if (isBundle) {
      updateBundleTitle.mutate(
        { bundleId: node.id, request: data },
        {
          onSuccess: () => {
            onOpenChange(false);
          },
        },
      );
    } else {
      updateWordbookTitle.mutate(
        { wordbookId: node.id, request: data },
        {
          onSuccess: () => {
            onOpenChange(false);
          },
        },
      );
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>제목 수정</DialogTitle>
          <DialogDescription>
            {isBundle ? '단어장 모음' : '단어장'}의 제목을 수정하세요.
          </DialogDescription>
        </DialogHeader>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="title"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>제목</FormLabel>
                  <FormControl>
                    <Input placeholder="제목을 입력하세요" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <DialogFooter>
              <Button
                type="button"
                variant="outline"
                onClick={() => onOpenChange(false)}
              >
                취소
              </Button>
              <Button
                type="submit"
                disabled={
                  updateWordbookTitle.isPending || updateBundleTitle.isPending
                }
              >
                {updateWordbookTitle.isPending || updateBundleTitle.isPending
                  ? '수정 중...'
                  : '수정'}
              </Button>
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}
