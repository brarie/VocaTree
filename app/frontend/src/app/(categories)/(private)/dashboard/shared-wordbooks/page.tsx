'use client';

import { Suspense, useState } from 'react';
import { PageHeader } from '@/components/PageHeader';
import { PaginationBar } from '@/components/PaginationBar';
import { usePageParams } from '@/hooks/usePageParams';
import {
  useMySharedWordbooks,
  useDeleteMySharedWordbook,
} from '@/hooks/library/queries';
import { SharedWordbookListView, SharedState } from '@/types/api/library';
import { ShareWordbookDialog } from './_components/ShareWordbookDialog';
import { MakeNewVersionDialog } from './_components/MakeNewVersionDialog';
import { EditSharedWordbookDialog } from './_components/EditSharedWordbookDialog';
import { Button } from '@/components/ui/button';
import {
  Plus,
  RefreshCw,
  Trash2,
  ExternalLink,
  Pencil,
  ArrowLeft,
  Layers,
} from 'lucide-react';
import Link from 'next/link';

const STATE_LABEL: Record<SharedState, string> = {
  PUBLIC: '공개',
  DELETED_BY_OWNER: '삭제됨',
  BANNED: '차단됨',
};

const STATE_COLOR: Record<SharedState, string> = {
  PUBLIC: 'text-emerald-400 bg-emerald-400/10 border-emerald-400/20',
  DELETED_BY_OWNER: 'text-zinc-500 bg-zinc-500/10 border-zinc-500/20',
  BANNED: 'text-red-400 bg-red-400/10 border-red-400/20',
};

interface VersionDialogState {
  id: number;
  version: number;
}

interface EditDialogState {
  id: number;
  slug: string;
}

function SharedWordbooksContent() {
  const { pageParams, setPage, setSize } = usePageParams({ defaultSize: 10 });
  const { data, isLoading } = useMySharedWordbooks(
    pageParams.page,
    pageParams.size,
  );
  const deleteWordbook = useDeleteMySharedWordbook();

  const [shareOpen, setShareOpen] = useState(false);
  const [versionDialog, setVersionDialog] = useState<VersionDialogState | null>(
    null,
  );
  const [editDialog, setEditDialog] = useState<EditDialogState | null>(null);

  function handleDelete(item: SharedWordbookListView) {
    if (!confirm(`"${item.title}" 공유를 삭제할까요?`)) return;
    deleteWordbook.mutate(item.id);
  }

  return (
    <div className="space-y-6">
      <PageHeader
        title="내 공유 단어장"
        description="라이브러리에 공유한 단어장을 관리하세요."
      >
        <Link href="/library">
          <Button
            variant="outline"
            className="bg-transparent border-zinc-700 text-zinc-300 hover:bg-zinc-800 hover:text-white mt-3"
          >
            <ArrowLeft className="w-4 h-4 mr-2" />
            라이브러리
          </Button>
        </Link>
        <Link href="/dashboard/shared-bundles">
          <Button
            variant="outline"
            className="bg-transparent border-zinc-700 text-zinc-300 hover:bg-zinc-800 hover:text-white mt-3"
          >
            <Layers className="w-4 h-4 mr-2" />내 번들
          </Button>
        </Link>
        <Button
          onClick={() => setShareOpen(true)}
          className="bg-white text-zinc-900 hover:bg-zinc-200 mt-3"
        >
          <Plus className="w-4 h-4 mr-2" />
          새로 공유하기
        </Button>
      </PageHeader>

      {/* 로딩 스켈레톤 */}
      {isLoading && (
        <div className="space-y-3">
          {[...Array(4)].map((_, i) => (
            <div
              key={i}
              className="h-20 bg-zinc-900 rounded-lg border border-zinc-800 animate-pulse"
            />
          ))}
        </div>
      )}

      {/* 빈 상태 */}
      {!isLoading && data && data.content.length === 0 && (
        <div className="flex flex-col items-center justify-center py-20 bg-white/5 rounded-lg border border-white/10">
          <p className="text-sm font-medium text-gray-400">
            공유한 단어장이 없습니다.
          </p>
          <p className="text-xs text-gray-500 mt-1">
            내 단어장을 라이브러리에 공유해보세요.
          </p>
          <Button
            onClick={() => setShareOpen(true)}
            className="mt-4 bg-white text-zinc-900 hover:bg-zinc-200"
            size="sm"
          >
            <Plus className="w-4 h-4 mr-2" />
            공유하기
          </Button>
        </div>
      )}

      {/* 목록 */}
      {!isLoading && data && data.content.length > 0 && (
        <div className="space-y-3">
          {data.content.map((item) => (
            <div
              key={item.id}
              className="bg-zinc-900 rounded-lg border border-zinc-800 p-4 flex items-center justify-between gap-4"
            >
              <div className="flex-1 min-w-0">
                <div className="flex items-center gap-2 flex-wrap">
                  <p className="text-sm font-medium text-white truncate">
                    {item.title}
                  </p>
                  <span
                    className={`text-[10px] px-2 py-0.5 rounded border font-medium ${STATE_COLOR[item.sharedState]}`}
                  >
                    {STATE_LABEL[item.sharedState]}
                  </span>
                </div>
                <p className="text-xs text-zinc-500 mt-0.5">
                  최신 버전: v{item.latestVersion} ·{' '}
                  {item.createdAt.slice(0, 10).replace(/-/g, '.')}
                </p>
              </div>

              <div className="flex items-center gap-2 shrink-0">
                {item.sharedState === SharedState.PUBLIC && (
                  <Link href={`/library/wordbook/${item.slug}`} target="_blank">
                    <Button
                      variant="outline"
                      size="sm"
                      className="bg-transparent border-zinc-700 text-zinc-400 hover:text-white hover:bg-zinc-800"
                    >
                      <ExternalLink className="w-3.5 h-3.5" />
                    </Button>
                  </Link>
                )}

                <Button
                  variant="outline"
                  size="sm"
                  onClick={() =>
                    setEditDialog({ id: item.id, slug: item.slug })
                  }
                  disabled={item.sharedState === SharedState.BANNED}
                  className="bg-transparent border-zinc-700 text-zinc-400 hover:text-white hover:bg-zinc-800 disabled:opacity-30"
                >
                  <Pencil className="w-3.5 h-3.5" />
                </Button>

                <Button
                  variant="outline"
                  size="sm"
                  onClick={() =>
                    setVersionDialog({
                      id: item.id,
                      version: item.latestVersion,
                    })
                  }
                  disabled={item.sharedState === SharedState.BANNED}
                  className="bg-transparent border-zinc-700 text-zinc-400 hover:text-white hover:bg-zinc-800 disabled:opacity-30"
                >
                  <RefreshCw className="w-3.5 h-3.5 mr-1.5" />
                  버전 업데이트
                </Button>

                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => handleDelete(item)}
                  disabled={
                    item.sharedState === SharedState.DELETED_BY_OWNER ||
                    deleteWordbook.isPending
                  }
                  className="bg-transparent border-zinc-700 text-zinc-400 hover:text-red-400 hover:border-red-400/50 hover:bg-red-400/5 disabled:opacity-30"
                >
                  <Trash2 className="w-3.5 h-3.5" />
                </Button>
              </div>
            </div>
          ))}

          <PaginationBar
            pageData={data}
            onPageChange={setPage}
            onItemsPerPageChange={setSize}
            className="pt-2"
          />
        </div>
      )}

      {/* 다이얼로그들 */}
      <ShareWordbookDialog open={shareOpen} onOpenChange={setShareOpen} />

      {editDialog && (
        <EditSharedWordbookDialog
          open={editDialog !== null}
          onOpenChange={(open) => {
            if (!open) setEditDialog(null);
          }}
          id={editDialog.id}
          slug={editDialog.slug}
        />
      )}

      {versionDialog && (
        <MakeNewVersionDialog
          open={versionDialog !== null}
          onOpenChange={(open) => {
            if (!open) setVersionDialog(null);
          }}
          sharedWordbookId={versionDialog.id}
          currentVersion={versionDialog.version}
        />
      )}
    </div>
  );
}

export default function SharedWordbooksPage() {
  return (
    <Suspense
      fallback={
        <div className="text-center py-12 text-gray-400">로딩 중...</div>
      }
    >
      <SharedWordbooksContent />
    </Suspense>
  );
}
