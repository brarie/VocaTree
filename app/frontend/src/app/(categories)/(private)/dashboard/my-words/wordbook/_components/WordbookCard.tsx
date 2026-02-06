'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import {
  Folder,
  BookOpen,
  Star,
  MoreVertical,
  Edit,
  Trash,
  FolderInput,
} from 'lucide-react';
import { NodeResponseWithCountView, NodeType } from '@/types/api/wordbook';
import {
  useToggleBundleStarred,
  useToggleWordbookStarred,
} from '@/hooks/wordbook/queries';
import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { cn } from '@/lib/utils';
import { EditDialog } from './EditDialog';
import { DeleteDialog } from './DeleteDialog';
import { MoveDialog } from './MoveDialog';

interface WordbookCardProps {
  node: NodeResponseWithCountView;
  hideStar?: boolean;
}

export function WordbookCard({ node, hideStar = false }: WordbookCardProps) {
  const router = useRouter();
  const toggleBundleStarred = useToggleBundleStarred();
  const toggleWordbookStarred = useToggleWordbookStarred();

  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [moveDialogOpen, setMoveDialogOpen] = useState(false);

  const isBundle = node.nodeType === NodeType.BUNDLE;

  const handleClick = () => {
    if (isBundle) {
      router.push(`/dashboard/my-words/wordbook/bundle/${node.id}`);
    } else {
      router.push(`/dashboard/my-words/wordbook/${node.id}`);
    }
  };

  const handleToggleStar = (e: React.MouseEvent) => {
    e.stopPropagation();
    if (isBundle) {
      toggleBundleStarred.mutate(node.id);
    } else {
      toggleWordbookStarred.mutate(node.id);
    }
  };

  const handleEdit = (e: React.MouseEvent) => {
    e.stopPropagation();
    setEditDialogOpen(true);
  };

  const handleDelete = (e: React.MouseEvent) => {
    e.stopPropagation();
    setDeleteDialogOpen(true);
  };

  const handleMove = (e: React.MouseEvent) => {
    e.stopPropagation();
    setMoveDialogOpen(true);
  };

  if (isBundle) {
    return (
      <>
        <div
          onClick={handleClick}
          className="group relative rounded-xl bg-linear-to-br from-amber-500/10 to-orange-500/5 p-4 transition-all hover:from-amber-500/15 hover:to-orange-500/10 cursor-pointer ring-1 ring-amber-500/20 hover:ring-amber-500/40"
        >
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-gradient-to-br from-black-500 to-white-600 border-2">
              <Folder className="h-5 w-5 text-white" />
            </div>
            <h3 className="flex-1 min-w-0 font-semibold text-zinc-100 leading-tight break-words">
              {node.title}
            </h3>
            <div className="flex items-center shrink-0 gap-0">
              {!hideStar && (
                <button
                  type="button"
                  onClick={handleToggleStar}
                  className="p-1 transition-colors"
                >
                  <Star
                    className={cn(
                      'h-5 w-5 transition-all',
                      node.isStarred
                        ? 'fill-amber-400 text-amber-400'
                        : 'text-zinc-600 hover:text-amber-400/60',
                    )}
                  />
                </button>
              )}
              <DropdownMenu>
                <DropdownMenuTrigger
                  asChild
                  onClick={(e) => e.stopPropagation()}
                >
                  <Button variant="ghost" size="icon">
                    <MoreVertical className="w-4 h-4" />
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end">
                  <DropdownMenuItem onClick={handleEdit}>
                    <Edit className="w-4 h-4 mr-2" />
                    제목 수정
                  </DropdownMenuItem>
                  <DropdownMenuItem
                    onClick={handleDelete}
                    className="text-destructive"
                  >
                    <Trash className="w-4 h-4 mr-2" />
                    삭제
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
            </div>
          </div>
          <div className="mt-3 flex items-center justify-between">
            <span className="text-[11px] font-medium text-amber-400/80">
              단어장 모음
            </span>
            <span className="rounded-full bg-amber-500/20 px-2.5 py-1 text-xs font-medium text-amber-300">
              {node.count}개 단어장
            </span>
          </div>
        </div>

        <EditDialog
          open={editDialogOpen}
          onOpenChange={setEditDialogOpen}
          node={node}
        />

        <DeleteDialog
          open={deleteDialogOpen}
          onOpenChange={setDeleteDialogOpen}
          node={node}
        />
      </>
    );
  }

  return (
    <>
      <div
        onClick={handleClick}
        className="group relative rounded-xl bg-zinc-900 p-4 transition-all hover:bg-zinc-800/80 cursor-pointer ring-1 ring-zinc-800 hover:ring-zinc-700"
      >
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-gradient-to-br from-black-500 to-white-600 border-2">
            <BookOpen className="h-5 w-5 text-white" />
          </div>
          <h3 className="flex-1 min-w-0 font-medium text-zinc-100 leading-tight break-words">
            {node.title}
          </h3>
          <div className="flex items-center shrink-0 gap-0">
            {!hideStar && (
              <button
                type="button"
                onClick={handleToggleStar}
                className="p-1 transition-colors"
              >
                <Star
                  className={cn(
                    'h-5 w-5 transition-all',
                    node.isStarred
                      ? 'fill-yellow-400 text-yellow-400'
                      : 'text-zinc-600 hover:text-yellow-400/60',
                  )}
                />
              </button>
            )}
            <DropdownMenu>
              <DropdownMenuTrigger
                asChild
                onClick={(e) => e.stopPropagation()}
              >
                <Button variant="ghost" size="icon">
                  <MoreVertical className="w-4 h-4" />
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end">
                <DropdownMenuItem onClick={handleEdit}>
                  <Edit className="w-4 h-4 mr-2" />
                  제목 수정
                </DropdownMenuItem>
                <DropdownMenuItem onClick={handleMove}>
                  <FolderInput className="w-4 h-4 mr-2" />
                  이동
                </DropdownMenuItem>
                <DropdownMenuItem
                  onClick={handleDelete}
                  className="text-destructive"
                >
                  <Trash className="w-4 h-4 mr-2" />
                  삭제
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          </div>
        </div>
        <div className="mt-3 flex items-center justify-end">
          <span className="rounded-full bg-zinc-800 px-2.5 py-1 text-xs text-zinc-400">
            {node.count}개 단어
          </span>
        </div>
      </div>

      <EditDialog
        open={editDialogOpen}
        onOpenChange={setEditDialogOpen}
        node={node}
      />

      <DeleteDialog
        open={deleteDialogOpen}
        onOpenChange={setDeleteDialogOpen}
        node={node}
      />

      <MoveDialog
        open={moveDialogOpen}
        onOpenChange={setMoveDialogOpen}
        wordbookId={node.id}
        currentTitle={node.title}
      />
    </>
  );
}
