'use client';

import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import {
  CreateSupportPostRequestSchema,
  UpdateSupportPostRequestSchema,
  CreateSupportPostRequest,
  UpdateSupportPostRequest,
  SupportCategoryResponse,
  SupportPostView,
} from '@/types/api/supportBoard';
import {
  useCreatePost,
  useUpdatePost,
  useDeletePost,
} from '@/hooks/supportBoard/queries';
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
import { Textarea } from '@/components/ui/textarea';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Button } from '@/components/ui/button';

function getCategoryDisplayName(
  cat: SupportCategoryResponse,
  allCategories: SupportCategoryResponse[],
) {
  if (cat.parentCategoryId === null) {
    return cat.categoryName;
  }
  const parent = allCategories.find((c) => c.id === cat.parentCategoryId);
  return parent
    ? `${parent.categoryName} > ${cat.categoryName}`
    : cat.categoryName;
}

interface CreatePostDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  categories: SupportCategoryResponse[];
}

export function CreatePostDialog({
  open,
  onOpenChange,
  categories,
}: CreatePostDialogProps) {
  const createMutation = useCreatePost();

  const subCategories = categories.filter(
    (cat) => cat.parentCategoryId !== null,
  );

  const form = useForm({
    resolver: zodResolver(CreateSupportPostRequestSchema),
    defaultValues: {
      title: '',
      content: '',
      categoryId: subCategories.length > 0 ? subCategories[0].id : 0,
    },
  });

  const onSubmit = async (data: CreateSupportPostRequest) => {
    await createMutation.mutateAsync(data);
    form.reset();
    onOpenChange(false);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="bg-black border-white/10 max-w-2xl">
        <DialogHeader>
          <DialogTitle className="text-white">게시글 작성</DialogTitle>
          <DialogDescription className="text-gray-400">
            새로운 게시글을 작성합니다.
          </DialogDescription>
        </DialogHeader>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="categoryId"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="text-white">카테고리</FormLabel>
                  <Select
                    value={field.value.toString()}
                    onValueChange={(value) => field.onChange(parseInt(value))}
                  >
                    <FormControl>
                      <SelectTrigger className="bg-white/5 border-white/10 text-white">
                        <SelectValue />
                      </SelectTrigger>
                    </FormControl>
                    <SelectContent className="bg-black border-white/10">
                      {subCategories.map((cat) => (
                        <SelectItem key={cat.id} value={cat.id.toString()}>
                          {getCategoryDisplayName(cat, categories)}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="title"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="text-white">제목</FormLabel>
                  <FormControl>
                    <Input
                      {...field}
                      placeholder="제목을 입력하세요"
                      className="bg-white/5 border-white/10 text-white placeholder:text-gray-500"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="content"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="text-white">내용</FormLabel>
                  <FormControl>
                    <Textarea
                      {...field}
                      placeholder="내용을 입력하세요"
                      rows={10}
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
              <Button
                type="submit"
                disabled={createMutation.isPending}
                className="bg-white text-black hover:bg-gray-200"
              >
                작성
              </Button>
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}

interface UpdatePostDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  post: SupportPostView;
  categories: SupportCategoryResponse[];
}

export function UpdatePostDialog({
  open,
  onOpenChange,
  post,
  categories,
}: UpdatePostDialogProps) {
  const updateMutation = useUpdatePost();

  const subCategories = categories.filter(
    (cat) => cat.parentCategoryId !== null,
  );

  const form = useForm({
    resolver: zodResolver(UpdateSupportPostRequestSchema),
    defaultValues: {
      title: post.title,
      content: post.content,
      categoryId: post.supportCategoryId,
    },
  });

  useEffect(() => {
    form.reset({
      title: post.title,
      content: post.content,
      categoryId: post.supportCategoryId,
    });
  }, [post, form]);

  const onSubmit = async (data: UpdateSupportPostRequest) => {
    await updateMutation.mutateAsync({ postId: post.id, data });
    form.reset();
    onOpenChange(false);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="bg-black border-white/10 max-w-2xl">
        <DialogHeader>
          <DialogTitle className="text-white">게시글 수정</DialogTitle>
          <DialogDescription className="text-gray-400">
            게시글을 수정합니다.
          </DialogDescription>
        </DialogHeader>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="categoryId"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="text-white">카테고리</FormLabel>
                  <Select
                    value={field.value.toString()}
                    onValueChange={(value) => field.onChange(parseInt(value))}
                  >
                    <FormControl>
                      <SelectTrigger className="bg-white/5 border-white/10 text-white">
                        <SelectValue />
                      </SelectTrigger>
                    </FormControl>
                    <SelectContent className="bg-black border-white/10">
                      {subCategories.map((cat) => (
                        <SelectItem key={cat.id} value={cat.id.toString()}>
                          {getCategoryDisplayName(cat, categories)}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="title"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="text-white">제목</FormLabel>
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

            <FormField
              control={form.control}
              name="content"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="text-white">내용</FormLabel>
                  <FormControl>
                    <Textarea
                      {...field}
                      rows={10}
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
              <Button
                type="submit"
                disabled={updateMutation.isPending}
                className="bg-white text-black hover:bg-gray-200"
              >
                수정
              </Button>
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}

interface DeletePostDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  post: SupportPostView;
}

export function DeletePostDialog({
  open,
  onOpenChange,
  post,
}: DeletePostDialogProps) {
  const deleteMutation = useDeletePost();

  const handleDelete = async () => {
    await deleteMutation.mutateAsync(post.id);
    onOpenChange(false);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="bg-black border-white/10">
        <DialogHeader>
          <DialogTitle className="text-white">게시글 삭제</DialogTitle>
          <DialogDescription className="text-gray-400">
            정말로 &quot;{post.title}&quot; 게시글을 삭제하시겠습니까?
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
