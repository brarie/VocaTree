'use client';

import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import {
  CreateMainCategoryRequestSchema,
  CreateSubCategoryRequestSchema,
  UpdateCategoryRequestSchema,
  CreateMainCategoryRequest,
  CreateSubCategoryRequest,
  UpdateCategoryRequest,
  SupportCategoryResponse,
} from '@/types/api/supportBoard';
import {
  useCreateMainCategory,
  useCreateSubCategory,
  useUpdateCategory,
  useDeleteCategory,
} from '@/hooks/supportBoard/queries';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';

interface CreateMainCategoryDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

export function CreateMainCategoryDialog({ open, onOpenChange }: CreateMainCategoryDialogProps) {
  const createMutation = useCreateMainCategory();

  const form = useForm({
    resolver: zodResolver(CreateMainCategoryRequestSchema),
    defaultValues: {
      categoryName: '',
    },
  });

  const onSubmit = async (data: CreateMainCategoryRequest) => {
    await createMutation.mutateAsync(data);
    form.reset();
    onOpenChange(false);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="bg-black border-white/10">
        <DialogHeader>
          <DialogTitle className="text-white">대분류 카테고리 추가</DialogTitle>
          <DialogDescription className="text-gray-400">
            새로운 대분류 카테고리를 추가합니다.
          </DialogDescription>
        </DialogHeader>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="categoryName"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="text-white">카테고리 이름</FormLabel>
                  <FormControl>
                    <Input
                      {...field}
                      placeholder="예: 공지사항"
                      className="bg-white/5 border-white/10 text-white placeholder:text-gray-500"
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
                onClick={() => onOpenChange(false)}
                className="bg-white/5 border-white/10 text-white hover:bg-white/10"
              >
                취소
              </Button>
              <Button type="submit" disabled={createMutation.isPending} className="bg-white text-black hover:bg-gray-200">
                추가
              </Button>
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}

interface CreateSubCategoryDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  parentCategoryId: number;
}

export function CreateSubCategoryDialog({
  open,
  onOpenChange,
  parentCategoryId,
}: CreateSubCategoryDialogProps) {
  const createMutation = useCreateSubCategory();

  const form = useForm({
    resolver: zodResolver(CreateSubCategoryRequestSchema),
    defaultValues: {
      categoryName: '',
    },
  });

  const onSubmit = async (data: CreateSubCategoryRequest) => {
    await createMutation.mutateAsync({ parentCategoryId, data });
    form.reset();
    onOpenChange(false);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="bg-black border-white/10">
        <DialogHeader>
          <DialogTitle className="text-white">서브 카테고리 추가</DialogTitle>
          <DialogDescription className="text-gray-400">
            새로운 서브 카테고리를 추가합니다.
          </DialogDescription>
        </DialogHeader>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="categoryName"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="text-white">카테고리 이름</FormLabel>
                  <FormControl>
                    <Input
                      {...field}
                      placeholder="예: 업데이트 안내"
                      className="bg-white/5 border-white/10 text-white placeholder:text-gray-500"
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
                onClick={() => onOpenChange(false)}
                className="bg-white/5 border-white/10 text-white hover:bg-white/10"
              >
                취소
              </Button>
              <Button type="submit" disabled={createMutation.isPending} className="bg-white text-black hover:bg-gray-200">
                추가
              </Button>
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}

interface UpdateCategoryDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  category: SupportCategoryResponse;
}

export function UpdateCategoryDialog({ open, onOpenChange, category }: UpdateCategoryDialogProps) {
  const updateMutation = useUpdateCategory();

  const form = useForm({
    resolver: zodResolver(UpdateCategoryRequestSchema),
    defaultValues: {
      categoryName: category.categoryName,
    },
  });

  useEffect(() => {
    form.reset({
      categoryName: category.categoryName,
    });
  }, [category, form]);

  const onSubmit = async (data: UpdateCategoryRequest) => {
    await updateMutation.mutateAsync({ categoryId: category.id, data });
    form.reset();
    onOpenChange(false);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="bg-black border-white/10">
        <DialogHeader>
          <DialogTitle className="text-white">카테고리 수정</DialogTitle>
          <DialogDescription className="text-gray-400">
            카테고리 이름을 수정합니다.
          </DialogDescription>
        </DialogHeader>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="categoryName"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="text-white">카테고리 이름</FormLabel>
                  <FormControl>
                    <Input
                      {...field}
                      className="bg-white/5 border-white/10 text-white placeholder:text-gray-500"
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
                onClick={() => onOpenChange(false)}
                className="bg-white/5 border-white/10 text-white hover:bg-white/10"
              >
                취소
              </Button>
              <Button type="submit" disabled={updateMutation.isPending} className="bg-white text-black hover:bg-gray-200">
                수정
              </Button>
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}

interface DeleteCategoryDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  category: SupportCategoryResponse;
}

export function DeleteCategoryDialog({ open, onOpenChange, category }: DeleteCategoryDialogProps) {
  const deleteMutation = useDeleteCategory();

  const handleDelete = async () => {
    await deleteMutation.mutateAsync(category.id);
    onOpenChange(false);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="bg-black border-white/10">
        <DialogHeader>
          <DialogTitle className="text-white">카테고리 삭제</DialogTitle>
          <DialogDescription className="text-gray-400">
            정말로 &quot;{category.categoryName}&quot; 카테고리를 삭제하시겠습니까?
            <br />
            서브 카테고리나 게시글이 있는 경우 삭제할 수 없습니다.
          </DialogDescription>
        </DialogHeader>

        <DialogFooter>
          <Button
            type="button"
            variant="outline"
            onClick={() => onOpenChange(false)}
            className="bg-white/5 border-white/10 text-white hover:bg-white/10"
          >
            취소
          </Button>
          <Button
            onClick={handleDelete}
            disabled={deleteMutation.isPending}
            className="bg-red-500 text-white hover:bg-red-600"
          >
            삭제
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
