import { PrivateRoute } from '@/components/PrivateRoute';

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <PrivateRoute>
      <main className="p-6">
        <div className="w-full max-w-7xl mx-auto bg-black">{children}</div>
      </main>
    </PrivateRoute>
  );
}
