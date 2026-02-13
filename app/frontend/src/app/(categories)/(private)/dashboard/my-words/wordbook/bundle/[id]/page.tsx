'use client';

import { Suspense, useEffect, useState } from 'react';
import { useParams, useSearchParams, useRouter } from 'next/navigation';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { ArrowLeft, Plus } from 'lucide-react';
import {
  usePaginateNodesInBundle,
  useBundle,
  useCreateWordbookInBundle,
} from '@/hooks/wordbook/queries';
import {
  CreateWordbookRequestSchema,
  CreateWordbookRequest,
} from '@/types/api/wordbook';
import { usePageHeader } from '@/contexts/PageHeaderContext';
import { Button } from '@/components/ui/button';
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
import { PaginationBar } from '@/components/PaginationBar';
import { WordbookCard } from '../../_components/WordbookCard';

export function BundleContent() {
  const router = useRouter();
  const params = useParams();
  const searchParams = useSearchParams();

  const bundleId = parseInt(params.id as string, 10);
  const page = parseInt(searchParams.get('page') || '0', 10);
  const size = parseInt(searchParams.get('size') || '30', 10);

  const { data: bundle, isLoading: isBundleLoading } = useBundle(bundleId);
  const { data: pageData, isLoading: isPageLoading } = usePaginateNodesInBundle(
    bundleId,
    page,
    size,
  );
  const { setPageHeader, clearPageHeader } = usePageHeader();
  const createWordbookInBundle = useCreateWordbookInBundle();

  const [createDialogOpen, setCreateDialogOpen] = useState(false);

  const form = useForm<CreateWordbookRequest>({
    resolver: zodResolver(CreateWordbookRequestSchema),
    defaultValues: {
      title: '',
    },
  });

  const isLoading = isBundleLoading || isPageLoading;

  useEffect(() => {
    if (bundle) {
      setPageHeader(
        bundle.title,
        '단어장 모음: 비슷한 단어장들을 묶어서 정리할 수 있어요.',
      );
    }
    return () => {
      clearPageHeader();
    };
  }, [bundle, setPageHeader, clearPageHeader]);

  const updateParams = (updates: Record<string, string | number | boolean>) => {
    const params = new URLSearchParams(searchParams.toString());

    Object.entries(updates).forEach(([key, value]) => {
      if (
        value === '' ||
        value === null ||
        value === undefined ||
        (key === 'page' && value === 0) ||
        (key === 'size' && value === 30)
      ) {
        params.delete(key);
      } else {
        params.set(key, String(value));
      }
    });

    const newUrl = params.toString()
      ? `?${params.toString()}`
      : window.location.pathname;
    router.replace(newUrl, { scroll: false });
  };

  const handlePageChange = (newPage: number) => {
    updateParams({ page: newPage });
  };

  const handleSizeChange = (newSize: number) => {
    updateParams({ page: 0, size: newSize });
  };

  const onSubmit = async (data: CreateWordbookRequest) => {
    createWordbookInBundle.mutate(
      { bundleId, request: data },
      {
        onSuccess: () => {
          form.reset();
          setCreateDialogOpen(false);
        },
      },
    );
  };

  const handleOpenChange = (newOpen: boolean) => {
    if (!newOpen) {
      form.reset();
    }
    setCreateDialogOpen(newOpen);
  };

  if (isLoading) {
    return (
      <div className="text-center py-12 text-muted-foreground">로딩 중...</div>
    );
  }

  if (!bundle) {
    return (
      <div className="text-center py-12 text-muted-foreground">
        단어장 모음을 찾을 수 없습니다.
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <Button
          variant="ghost"
          size="sm"
          onClick={() => router.push('/dashboard/my-words/wordbook')}
          className="gap-2"
        >
          <ArrowLeft className="w-4 h-4" />
          뒤로가기
        </Button>

        <Button onClick={() => setCreateDialogOpen(true)}>
          <Plus className="w-4 h-4 mr-2" />
          단어장 만들기
        </Button>
      </div>

      {pageData && pageData.content.length > 0 ? (
        <>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {pageData.content.map((node) => (
              <WordbookCard key={node.id} node={node} hideStar={true} />
            ))}
          </div>

          <PaginationBar
            itemsPerPageOptions={[15, 30, 45]}
            pageData={pageData}
            onPageChange={handlePageChange}
            onItemsPerPageChange={handleSizeChange}
          />
        </>
      ) : (
        <div className="text-center py-12 text-muted-foreground">
          이 단어장 모음에는 아직 단어장이 없습니다.
        </div>
      )}

      <Dialog open={createDialogOpen} onOpenChange={handleOpenChange}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>단어장 만들기</DialogTitle>
            <DialogDescription>
              {bundle?.title} 모음에 새로운 단어장을 추가합니다.
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
                      <Input
                        placeholder="단어장 제목을 입력하세요"
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
                  disabled={createWordbookInBundle.isPending}
                >
                  {createWordbookInBundle.isPending ? '생성 중...' : '생성'}
                </Button>
              </DialogFooter>
            </form>
          </Form>
        </DialogContent>
      </Dialog>
    </div>
  );
}

export default function BundleDetailPage() {
  return (
    <Suspense fallback={<div>로딩 중...</div>}>
      <BundleContent />
    </Suspense>
  );
}
