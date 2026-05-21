'use client';
import { useState, useEffect } from 'react';
import AccountSideNav from '@/components/AccountSideNav';
import Button from '@/components/ui/Button';
import { Wallet, Clock, ArrowUp, Plus, Filter } from '@/components/icons';
import { fmtRp } from '@/lib/data';
import { Gavel, ArrowDown, CreditCard, Refresh } from '@/components/icons';
import { walletApi } from '@/lib/api';
import { useAuth } from '@/store/auth-context';

function typeBadge(t: string) {
  const map: Record<string, { tone: string; icon: React.ReactNode }> = {
    'Top Up':   { tone: 'green',  icon: <ArrowDown width={12} height={12}/> },
    'Bid Hold': { tone: 'amber',  icon: <Gavel width={12} height={12}/> },
    'Payment':  { tone: 'blue',   icon: <CreditCard width={12} height={12}/> },
    'Refund':   { tone: 'gray',   icon: <Refresh width={12} height={12}/> },
    'Withdraw': { tone: 'gray',   icon: <ArrowUp width={12} height={12}/> },
  };
  const m = map[t] || { tone: 'gray', icon: null };
  return <span className={`bm-badge bm-badge-${m.tone}`}>{m.icon} {t}</span>;
}

export default function WalletPage() {
  const { user } = useAuth();
  const [wallet, setWallet] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [typeFilter, setTypeFilter] = useState('All');
  const [page, setPage] = useState(1);
  const perPage = 8;

  useEffect(() => {
    if (user) {
      async function fetchWallet() {
        try {
          const data = await walletApi.getWallet(user.id);
          setWallet(data);
        } catch (err) {
          console.error('Failed to fetch wallet:', err);
        } finally {
          setLoading(false);
        }
      }
      fetchWallet();
    }
  }, [user]);

  if (loading) return <div style={{ padding: 48 }}>Memuat dompet…</div>;
  if (!wallet) return <div style={{ padding: 48 }}>Dompet tidak tersedia.</div>;

  const available = wallet.availableBalance || 0;
  const onHold = wallet.heldBalance || 0;
  const transactions = wallet.transactions || [];

  const filtered = typeFilter === 'All' ? transactions : transactions.filter((t: any) => t.type === typeFilter);
  const totalPages = Math.max(1, Math.ceil(filtered.length / perPage));
  const slice = filtered.slice((page - 1) * perPage, page * perPage);

  return (
    <div className="bm-page-wide">
      <div className="bm-app">
        <AccountSideNav active="wallet"/>
        <div>
          <div className="bm-pg-head">
            <div>
              <h1>Dompet BidMart</h1>
              <p>Kelola saldo, top up, dan pantau riwayat transaksi kamu.</p>
            </div>
            <div className="bm-row" style={{ gap: 10 }}>
              <Button variant="secondary" size="md" leftIcon={<ArrowUp width={16} height={16}/>}>Tarik dana</Button>
              <Button variant="primary" size="md" leftIcon={<Plus width={16} height={16}/>}>Top Up</Button>
            </div>
          </div>

          <div className="bm-walletcards">
            <div className="bm-walletcard green">
              <div className="bm-walletcard-ico"><Wallet width={26} height={26}/></div>
              <div className="bm-walletcard-body">
                <div className="bm-walletcard-lbl">Saldo Tersedia</div>
                <div className="bm-walletcard-val">{fmtRp(available)}</div>
                <div className="bm-walletcard-sub">Data diperbarui secara real-time</div>
              </div>
            </div>
            <div className="bm-walletcard amber">
              <div className="bm-walletcard-ico"><Clock width={26} height={26}/></div>
              <div className="bm-walletcard-body">
                <div className="bm-walletcard-lbl">Sedang Ditahan</div>
                <div className="bm-walletcard-val">{fmtRp(onHold)}</div>
                <div className="bm-walletcard-sub">
                  Dana otomatis kembali jika kamu disalip atau lelang berakhir.
                </div>
              </div>
            </div>
          </div>

          <div className="bm-pg-head" style={{ marginBottom: 14, alignItems: 'flex-end' }}>
            <div>
              <h3 style={{ fontSize: 18, margin: 0 }}>Riwayat Transaksi</h3>
              <p style={{ color: 'var(--ink-3)', fontSize: 13, marginTop: 4 }}>
                Menampilkan <b style={{ color: 'var(--ink)' }}>{filtered.length}</b> transaksi
              </p>
            </div>
            <div className="bm-filterbar" style={{ margin: 0 }}>
              <select className="bm-select" value={typeFilter} onChange={e => { setTypeFilter(e.target.value); setPage(1); }}>
                <option value="All">Semua tipe</option>
                <option value="Top Up">Top Up</option>
                <option value="Bid Hold">Bid Hold</option>
                <option value="Payment">Payment</option>
                <option value="Refund">Refund</option>
                <option value="Withdraw">Withdraw</option>
              </select>
              <Button variant="ghost" size="md" leftIcon={<Filter width={14} height={14}/>}>Filter lain</Button>
            </div>
          </div>

          <div className="bm-table-wrap">
            <table className="bm-table">
              <thead>
                <tr>
                  <th style={{ width: 160 }}>Tanggal</th>
                  <th style={{ width: 130 }}>Tipe</th>
                  <th>Deskripsi</th>
                  <th style={{ width: 160, textAlign: 'right' }}>Jumlah</th>
                  <th style={{ width: 140, textAlign: 'right' }}>Saldo</th>
                </tr>
              </thead>
              <tbody>
                {slice.length === 0 ? (
                  <tr><td colSpan={5} style={{ textAlign: 'center', padding: 24, color: 'var(--ink-3)' }}>Belum ada transaksi.</td></tr>
                ) : slice.map((t: any, i: number) => (
                  <tr key={i}>
                    <td style={{ color: 'var(--ink-2)' }}>{new Date(t.createdAt).toLocaleString()}</td>
                    <td>{typeBadge(t.type)}</td>
                    <td style={{ color: 'var(--ink)' }}>{t.description}</td>
                    <td style={{ textAlign: 'right' }} className={t.amount >= 0 ? 'pos' : 'neg'}>
                      {t.amount >= 0 ? '+' : '−'}{fmtRp(Math.abs(t.amount))}
                    </td>
                    <td style={{ textAlign: 'right', color: 'var(--ink)' }}>{fmtRp(t.balanceAfter)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {totalPages > 1 && (
            <div className="bm-pagination">
              <span>Menampilkan {(page - 1) * perPage + 1}–{Math.min(page * perPage, filtered.length)} dari {filtered.length}</span>
              <div className="pages">
                <button className="pg" disabled={page === 1} onClick={() => setPage(p => Math.max(1, p - 1))}>‹</button>
                {Array.from({ length: totalPages }).map((_, i) => (
                  <button key={i} className={`pg ${page === i + 1 ? 'active' : ''}`} onClick={() => setPage(i + 1)}>{i + 1}</button>
                ))}
                <button className="pg" disabled={page === totalPages} onClick={() => setPage(p => Math.min(totalPages, p + 1))}>›</button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
