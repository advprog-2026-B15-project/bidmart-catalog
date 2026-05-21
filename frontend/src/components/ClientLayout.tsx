'use client';
import { usePathname } from 'next/navigation';
import { useState } from 'react';
import { AuctionProvider, useAuction } from '@/store/auction-context';
import { AuthProvider, useAuth } from '@/store/auth-context';
import { auctionApi } from '@/lib/api';
import TopNav from './TopNav';
import Footer from './Footer';
import Toaster from './Toaster';
import BidConfirmModal from './BidConfirmModal';

const NO_SHELL_ROUTES = ['/login'];

function InnerLayout({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();
  const { toasts, dismissToast, modal, closeModal, addToast } = useAuction();
  const { user } = useAuth();
  const [loading, setLoading] = useState(false);
  const noShell = NO_SHELL_ROUTES.includes(pathname);

  async function handleConfirm() {
    if (!user) {
      addToast({ tone: 'error', title: 'Belum masuk', desc: 'Silakan login untuk menawar.' });
      return;
    }
    if (!modal) return;

    setLoading(true);
    try {
      await auctionApi.placeBid(modal.item.id, modal.amount, user.id);
      closeModal();
      addToast({ tone: 'success', title: 'Tawaran berhasil!', desc: `Kamu menjadi penawar tertinggi.` });
    } catch (err) {
      addToast({ tone: 'error', title: 'Gagal menawar', desc: (err as Error).message });
    } finally {
      setLoading(false);
    }
  }

  return (
    <>
      {!noShell && <TopNav alerts={3}/>}
      {children}
      {!noShell && <Footer/>}
      <Toaster toasts={toasts} onDismiss={dismissToast}/>
      <BidConfirmModal
        open={!!modal}
        item={modal?.item ?? null}
        bidAmount={modal?.amount ?? 0}
        loading={loading}
        onClose={closeModal}
        onConfirm={handleConfirm}
      />
    </>
  );
}

export default function ClientLayout({ children }: { children: React.ReactNode }) {
  return (
    <AuthProvider>
      <AuctionProvider>
        <InnerLayout>{children}</InnerLayout>
      </AuctionProvider>
    </AuthProvider>
  );
}
