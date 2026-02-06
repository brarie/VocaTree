'use client';

import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import {
  CreateWordbookRequestSchema,
  CreateWordbookRequest,
} from '@/types/api/wordbook';
import { useCreateWordbook, useCreateBundle } from '@/hooks/wordbook/queries';
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
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';

interface CreateDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

export function CreateDialog({ open, onOpenChange }: CreateDialogProps) {
  const [type, setType] = useState<'wordbook' | 'bundle'>('wordbook');
  const createWordbook = useCreateWordbook();
  const createBundle = useCreateBundle();

  const form = useForm<CreateWordbookRequest>({
    resolver: zodResolver(CreateWordbookRequestSchema),
    defaultValues: {
      title: '',
    },
  });

  const onSubmit = async (data: CreateWordbookRequest) => {
    if (type === 'wordbook') {
      createWordbook.mutate(data, {
        onSuccess: () => {
          form.reset();
          onOpenChange(false);
        },
      });
    } else {
      createBundle.mutate(data, {
        onSuccess: () => {
          form.reset();
          onOpenChange(false);
        },
      });
    }
  };

  const handleOpenChange = (newOpen: boolean) => {
    if (!newOpen) {
      form.reset();
    }
    onOpenChange(newOpen);
  };

  return (
    <Dialog open={open} onOpenChange={handleOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>
            {type === 'wordbook' ? '단어장 만들기' : '단어장 모음 만들기'}
          </DialogTitle>
          <DialogDescription>
            {type === 'wordbook'
              ? '새로운 단어장을 만들어 단어를 저장하세요.'
              : '여러 단어장을 하나로 묶어 관리하세요.'}
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-4">
          <div>
            <label className="text-sm font-medium mb-2 block">종류</label>
            <Select
              value={type}
              onValueChange={(value) => setType(value as 'wordbook' | 'bundle')}
            >
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="wordbook">단어장</SelectItem>
                <SelectItem value="bundle">단어장 모음</SelectItem>
              </SelectContent>
            </Select>
          </div>

          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
              <FormField
                control={form.control}
                name="title"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>제목</FormLabel>
                    <FormControl>
                      <Input
                        placeholder={
                          type === 'wordbook'
                            ? '단어장 제목을 입력하세요'
                            : '단어장 모음 제목을 입력하세요'
                        }
                        {...field}
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
                  onClick={() => handleOpenChange(false)}
                >
                  취소
                </Button>
                <Button
                  type="submit"
                  disabled={
                    createWordbook.isPending || createBundle.isPending
                  }
                >
                  {createWordbook.isPending || createBundle.isPending
                    ? '생성 중...'
                    : '생성'}
                </Button>
              </DialogFooter>
            </form>
          </Form>
        </div>
      </DialogContent>
    </Dialog>
  );
}
