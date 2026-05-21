'use client';
import { useState, useEffect } from 'react';
import AccountSideNav from '@/components/AccountSideNav';
import Button from '@/components/ui/Button';
import { Check, AlertTri, Truck, Star } from '@/components/icons';
import { fmtRp } from '@/lib/data';
import type { Order, TrackingEvent } from '@/types';
import { bookingApi } from '@/lib/api';
import { useAuth } from '@/store/auth-context';

function statusLabel(s: string) {
  return {
    CREATED:   { lbl: 'Menunggu Pengiriman', cls: 'bm-status-wait' },
    SHIPPED:   { lbl: 'Dikirim',             cls: 'bm-status-ship' },
    DELIVERED: { lbl: 'Diterima',            cls: 'bm-status-recv' },
    COMPLETED: { lbl: 'Selesai',             cls: 'bm-status-done' },
    DISPUTED:  { lbl: 'Sengketa',            cls: 'bm-status-disp' },
  }[s] || { lbl: s, cls: 'bm-status-done' };
}

function OrderCard({ order, active, onClick, role }: { order: Order; active: boolean; onClick: () => void; role: string }) {
  const st = statusLabel(order.status);
  return (
    <div className={`bm-order-card ${active ? 'active' : ''}`} onClick={onClick}>
      <div className="bm-order-thumb"><div className={order.item.art}/></div>
      <div className="bm-order-body">
        <div className="bm-order-title">{order.item.title}</div>
        <div className="bm-order-meta">
          <span className="price">{fmtRp(order.price)}</span>
          <span style={{ color: 'var(--ink-4)' }}>·</span>
          <span>{role === 'buyer' ? `Penjual: ${order.counterpart}` : `Pembeli: ${order.counterpart}`}</span>
          <span style={{ color: 'var(--ink-4)' }}>·</span>
          <span>{order.when}</span>
        </div>
      </div>
      <div className="bm-order-right">
        <span className={`bm-status ${st.cls}`}>{st.lbl}</span>
        <span style={{ fontSize: 11, color: 'var(--ink-3)', fontFamily: 'var(--font-mono)' }}>{order.id}</span>
      </div>
    </div>
  );
}

function OrderDetail({ order, role, trackingNum, setTrackingNum }: { order: Order; role: string; trackingNum: string; setTrackingNum: (v: string) => void }) {
  if (!order) return <div style={{ padding: 24, textAlign: 'center', color: 'var(--ink-3)' }}>Pilih pesanan untuk melihat detail.</div>;
  
  const steps = [
    { key: 0, lbl: 'Won',       when: order.when },
    { key: 1, lbl: 'Packaging', when: order.status === 'CREATED' ? 'Menunggu' : 'Ok' },
    { key: 2, lbl: 'Shipped',   when: ['SHIPPED', 'DELIVERED', 'COMPLETED'].includes(order.status) ? 'Ok' : '—' },
    { key: 3, lbl: 'Delivered', when: ['DELIVERED', 'COMPLETED'].includes(order.status) ? 'Ok' : '—' },
    { key: 4, lbl: 'Confirmed', when: order.status === 'COMPLETED' ? 'Ok' : '—' },
  ];
  return (
    <div className="bm-orderdetail">
      <div className="bm-order-hero">
        <div className="bm-order-hero-thumb"><div className={order.item.art}/></div>
        <div>
          <h3 className="bm-order-hero-title">{order.item.title}</h3>
          <div className="bm-order-hero-meta">
            Order <span style={{ fontFamily: 'var(--font-mono)', color: 'var(--ink)' }}>#{order.id}</span>
            <span style={{ margin: '0 8px', color: 'var(--ink-4)' }}>·</span>
            {role === 'buyer' ? 'Dijual oleh' : 'Dibeli oleh'} <b style={{ color: 'var(--ink)' }}>{order.counterpart}</b>
          </div>
        </div>
        <div style={{ textAlign: 'right' }}>
          <div style={{ fontSize: 11, color: 'var(--ink-3)', fontWeight: 600, letterSpacing: '0.05em', textTransform: 'uppercase' }}>Winning bid</div>
          <div className="bm-order-hero-price">{fmtRp(order.price)}</div>
        </div>
      </div>

      <div>
        <div style={{ fontSize: 13, fontWeight: 600, marginBottom: 14, color: 'var(--ink)' }}>Status pesanan</div>
        <div className="bm-timeline">
          {steps.map((s, i) => {
            const state = i < order.step ? 'done' : i === order.step ? 'current' : '';
            return (
              <div key={s.key} className={`bm-tlstep ${state}`}>
                <div className="dot">
                  {state === 'done' ? <Check width={14} height={14}/> : <span style={{ fontSize: 11, fontWeight: 700 }}>{i + 1}</span>}
                </div>
                <div className="lbl">{s.lbl}</div>
                <div className="when">{s.when}</div>
              </div>
            );
          })}
        </div>
      </div>

      {order.status === 'SHIPPED' && order.tracking && (
        <>
          <div>
            <div style={{ fontSize: 13, fontWeight: 600, marginBottom: 10 }}>Informasi pengiriman</div>
            <div className="bm-tracking-row">
              <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                <Truck width={20} height={20} style={{ color: 'var(--blue-600)' }}/>
                <span className="courier">{order.tracking.courier}</span>
              </div>
              <span className="num">{order.tracking.num}</span>
              <span style={{ flex: 1, textAlign: 'right', color: 'var(--ink-3)', fontSize: 12 }}>{order.tracking.lastUpd}</span>
            </div>
          </div>
        </>
      )}

      <div style={{ display: 'flex', gap: 10, justifyContent: 'flex-end', paddingTop: 4 }}>
        {role === 'buyer' && order.status === 'SHIPPED' && (
          <><Button variant="ghost" size="md">Buka sengketa</Button><Button variant="primary" size="md" leftIcon={<Check width={16} height={16}/>}>Konfirmasi Barang Diterima</Button></>
        )}
        {role === 'buyer' && order.status === 'CREATED' && (
          <><Button variant="ghost" size="md">Batalkan pesanan</Button><Button variant="secondary" size="md">Pesan penjual</Button></>
        )}
      </div>
    </div>
  );
}

export default function PesananPage() {
  const { user } = useAuth();
  const [tab, setTab] = useState<'active' | 'completed' | 'disputes'>('active');
  const [orders, setOrders] = useState<Order[]>([]);
  const [openOrder, setOpenOrder] = useState<Order | null>(null);
  const [loading, setLoading] = useState(true);
  const [trackingNum, setTrackingNum] = useState('');
  const [role, setRole] = useState<'buyer' | 'seller'>('buyer');

  useEffect(() => {
    if (user) {
      const userId = user.id;
      async function fetchOrders() {
        try {
          const data = await bookingApi.getBookings(userId);
          const mapped = data.map((o: any) => ({
            id: o.id,
            role: o.buyerUserId === userId ? 'buyer' : 'seller',
            status: o.status,
            item: { title: `Listing ${o.listingId}`, art: 'bm-art-elec' }, // Should fetch from catalog if needed
            counterpart: o.buyerUserId === userId ? o.sellerUserId : o.buyerUserId,
            price: o.totalAmount,
            when: new Date(o.createdAt).toLocaleDateString(),
            step: o.status === 'CREATED' ? 1 : o.status === 'SHIPPED' ? 2 : 4,
          }));
          setOrders(mapped);
          if (mapped.length > 0) setOpenOrder(mapped[0]);
        } catch (err) {
          console.error('Failed to fetch orders:', err);
        } finally {
          setLoading(false);
        }
      }
      fetchOrders();
    }
  }, [user]);

  const filtered = orders.filter(o => {
    if (tab === 'active') return ['CREATED', 'SHIPPED'].includes(o.status);
    if (tab === 'completed') return ['DELIVERED', 'COMPLETED'].includes(o.status);
    if (tab === 'disputes') return o.status === 'DISPUTED';
    return true;
  });
  const counts = {
    active:    orders.filter(o => ['CREATED', 'SHIPPED'].includes(o.status)).length,
    completed: orders.filter(o => ['DELIVERED', 'COMPLETED'].includes(o.status)).length,
    disputes:  orders.filter(o => o.status === 'DISPUTED').length,
  };

  return (
    <div className="bm-page-wide">
      <div className="bm-app">
        <AccountSideNav active="orders"/>
        <div>
          <div className="bm-pg-head">
            <div>
              <h1>Pesanan saya</h1>
              <p>Pantau status, lacak pengiriman, dan kelola sengketa.</p>
            </div>
            <div className="bm-tabpills" style={{ margin: 0 }}>
              <button className={`bm-tabpill ${role === 'buyer' ? 'active' : ''}`} onClick={() => setRole('buyer')}>Sebagai Pembeli</button>
              <button className={`bm-tabpill ${role === 'seller' ? 'active' : ''}`} onClick={() => setRole('seller')}>Sebagai Penjual</button>
            </div>
          </div>

          <div className="bm-tabs">
            {(['active', 'completed', 'disputes'] as const).map(t => (
              <button key={t} className={`bm-tab ${tab === t ? 'active' : ''}`} onClick={() => setTab(t)}>
                {t === 'active' ? 'Active Orders' : t === 'completed' ? 'Completed' : 'Disputes'}
                <span className="count">{counts[t]}</span>
              </button>
            ))}
          </div>

          {loading ? (
            <div style={{ padding: 48, textAlign: 'center', color: 'var(--ink-3)' }}>Memuat pesanan…</div>
          ) : (
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1.4fr', gap: 24, alignItems: 'flex-start' }}>
              <div className="bm-orders-list">
                {filtered.map(o => (
                  <OrderCard key={o.id} order={o} active={openOrder?.id === o.id} onClick={() => setOpenOrder(o)} role={role}/>
                ))}
                {filtered.length === 0 && (
                  <div style={{ textAlign: 'center', color: 'var(--ink-3)', padding: '40px 20px', border: '1px dashed var(--border-strong)', borderRadius: 12 }}>
                    Tidak ada pesanan di kategori ini.
                  </div>
                )}
              </div>
              <OrderDetail order={openOrder!} role={role} trackingNum={trackingNum} setTrackingNum={setTrackingNum}/>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
