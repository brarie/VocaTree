interface PageHeaderProps {
  title: string;
  description: string;
}

export function PageHeader({ title, description }: PageHeaderProps) {
  return (
    <>
      <div className="mb-3">
        <h1 className="text-2xl font-bold text-white mb-1">{title}</h1>
        <p className="text-gray-400 text-sm">{description}</p>
      </div>
      <div className="border-t border-white/10 mb-3" />
    </>
  );
}
