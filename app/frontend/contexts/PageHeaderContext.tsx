'use client';

import { createContext, useContext, useState, useCallback } from 'react';

interface PageHeaderContextValue {
  title: string;
  description: string;
  setPageHeader: (title: string, description: string) => void;
  clearPageHeader: () => void;
}

const PageHeaderContext = createContext<PageHeaderContextValue | undefined>(
  undefined,
);

export function PageHeaderProvider({ children }: { children: React.ReactNode }) {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');

  const setPageHeader = useCallback((newTitle: string, newDescription: string) => {
    setTitle(newTitle);
    setDescription(newDescription);
  }, []);

  const clearPageHeader = useCallback(() => {
    setTitle('');
    setDescription('');
  }, []);

  return (
    <PageHeaderContext.Provider
      value={{ title, description, setPageHeader, clearPageHeader }}
    >
      {children}
    </PageHeaderContext.Provider>
  );
}

export function usePageHeader() {
  const context = useContext(PageHeaderContext);
  if (context === undefined) {
    throw new Error('usePageHeader must be used within a PageHeaderProvider');
  }
  return context;
}
