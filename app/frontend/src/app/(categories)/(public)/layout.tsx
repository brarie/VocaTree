export default function PublicLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <main className="p-6">
      <div className="w-full max-w-7xl mx-auto bg-black">{children}</div>
    </main>
  );
}
