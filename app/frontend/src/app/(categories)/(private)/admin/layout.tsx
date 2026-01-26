import { PrivateRoute } from '@/components/auth/PrivateRoute';

export default function AdminLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <PrivateRoute roles={['ADMIN']}>
      <main className="p-6">
        <div className="w-full max-w-7xl mx-auto bg-black">{children}</div>
      </main>
    </PrivateRoute>
  );
}
