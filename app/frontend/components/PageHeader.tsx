interface PageHeaderProps {
  title: string;
  description: string;
  children?: React.ReactNode;
}

export function PageHeader({ title, description, children }: PageHeaderProps) {
  return (
    <>
      <div className="flex items-start justify-between gap-4 mb-3">
        <div className="flex-1 min-w-0">
          <h1 className="text-2xl font-bold text-white mb-1">{title}</h1>
          <p className="text-gray-400 text-sm">{description}</p>
        </div>
        {children && <div className="flex gap-2">{children}</div>}
      </div>
      <div className="border-t border-white/10 mb-3" />
    </>
  );
}
