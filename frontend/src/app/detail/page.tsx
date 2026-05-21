'use client';
import { useSearchParams, useRouter } from 'next/navigation';
import { useState, useEffect } from 'react';
import { Suspense } from 'react';
import Badge from '@/components/ui/Badge';
import Button from '@/components/ui/Button';
import StarRating from '@/components/StarRating';
import { BlocksCountdown, CompactCountdown, useCountdown } from '@/components/Countdown';
import { Heart, Shield, Truck, Refresh, Lock, Settings } from '@/components/icons';
import { fmtRp, mapListingToAuctionItem } from '@/lib/data';
import { catalogApi, auctionApi } from '@/lib/api';
import { useAuction } from '@/store/auction-context';
import type { AuctionItem, Listing, Bid } from '@/types';

function DetailContent() {
  const searchParams = useSearchParams();
  const router = useRouter();
  const { activeItem, openModal } = useAuction();
  const id = searchParams.get('id');
  
  const [it, setIt] = useState<AuctionItem | null>(activeItem || null);
  const [bids, setBids] = useState<any[]>([]);
  const [loading, setLoading] = useState(!activeItem);
  const [thumb, setThumb] = useState(0);
  const [tab, setTab] = useState<'history' | 'desc' | 'shipping'>('history');
  const [bidVal, setBidVal] = useState('');

  useEffect(() => {
    if (id) {
      async function fetchData() {
        try {
          // Ensure id is a string for TypeScript
          const listingId = id as string;
          const listing = await catalogApi.getListing(listingId);
          const mappedItem = mapListingToAuctionItem(listing);
          setIt(mappedItem);
          setBidVal(String(mappedItem.price + 50_000));
          
          try {
            const auctionBids = await auctionApi.getAuction(listingId).then(a => a.bids || []);
            setBids(auctionBids.map((b: any) => ({
              bidder: b.bidderId,
              amount: b.amount,
              time: new Date(b.createdAt).toLocaleString(),
              you: false, // Should check against logged in user
              top: false
            })));
          } catch (e) {
            console.warn('Auction service not available for bid history');
          }
        } catch (err) {
          console.error('Failed to fetch listing detail:', err);
        } finally {
          setLoading(false);
        }
      }
      fetchData();
    }
  }, [id]);

  if (loading) return <div style={{ padding: 48 }}>Memuat…</div>;
  if (!it) return <div style={{ padding: 48 }}>Item tidak ditemukan.</div>;

  const cd = useCountdown(it.ends);
  const safe = cd.total > 60 * 60 * 1000;
  const urgent = cd.total < 2 * 60 * 1000 && cd.total > 0;
  const minNext = it.price + 50_000;

  return (
    <div className="bm-page-wide">
      <nav className="bm-bread">
        <a onClick={() => router.push('/')}>Beranda</a>
        <span className="sep">/</span>
        <a>{it.cat || 'Kategori'}</a>
        <span className="sep">/</span>
        <span className="here">{it.title.split(' ').slice(0, 3).join(' ')}…</span>
      </nav>

      <div className="bm-detail">
        <div className="bm-gallery">
          <div className="bm-gallery-main">
            <div className="bm-gallery-main-art"/>
            <div className={`bm-gallery-main-art ${it.art}`} style={{ position: 'absolute' }}/>
            <span className="bm-listing-badge bm-listing-badge-red"
              style={{ top: 16, left: 16, fontSize: 11, padding: '5px 10px' }}>
              LIVE · {cd.total > 0 ? <CompactCountdown end={it.ends}/> : 'Berakhir'}
            </span>
          </div>
          <div className="bm-gallery-thumbs">
            {[0, 1, 2, 3].map(i => (
              <button key={i} className={`bm-gallery-thumb ${thumb === i ? 'active' : ''}`} onClick={() => setThumb(i)}>
                <div className={`bm-gallery-thumb-art ${it.art}`} style={{ opacity: 0.9 - i * 0.12 }}/>
              </button>
            ))}
          </div>
          <dl className="bm-spec-table" style={{ marginTop: 18 }}>
            <dt>Kondisi</dt>           <dd>Bekas — Sangat Baik</dd>
            <dt>Brand</dt>             <dd>—</dd>
            <dt>Model</dt>             <dd>—</dd>
            <dt>Konektivitas</dt>      <dd>—</dd>
            <dt>Kelengkapan</dt>       <dd>—</dd>
            <dt>Lokasi</dt>            <dd>Indonesia</dd>
            <dt>Garansi</dt>           <dd>—</dd>
          </dl>
        </div>

        <div className="bm-detail-info">
          <div>
            <Badge tone="green">Verified seller</Badge>
          </div>
          <h1 className="bm-detail-title">{it.title}</h1>

          <div className="bm-seller-row">
            <span className="bm-avatar">{it.seller.slice(0, 2).toUpperCase()}</span>
            <div style={{ flex: 1, minWidth: 0 }}>
              <div className="bm-seller-name">{it.seller}</div>
              <div className="bm-seller-stats">
                <StarRating rating={it.rating} count={it.ratingCount}/>
              </div>
            </div>
            <a style={{ fontSize: 13, color: 'var(--blue-600)', cursor: 'pointer' }}>Lihat profil →</a>
          </div>

          <div className="bm-bid-panel">
            <div className="bm-bid-row-base">
              <span className="bm-bid-lbl">Tawaran tertinggi</span>
              <span className="bm-bid-sub">{it.bids} bid</span>
            </div>
            <div className="bm-bid-row-base" style={{ alignItems: 'flex-end' }}>
              <div className="bm-bid-price-big">{fmtRp(it.price)}</div>
            </div>

            <div className="bm-bid-divider"/>

            <div className="bm-bid-row-base">
              <span className="bm-bid-lbl">Sisa waktu</span>
              <span className="bm-bid-sub" style={{
                color: safe ? 'var(--green-700)' : urgent ? 'var(--red-600)' : 'var(--ink-2)',
                fontWeight: 600
              }}>
                {safe ? 'Masih lama' : urgent ? 'Hampir berakhir!' : 'Akan berakhir'}
              </span>
            </div>
            <BlocksCountdown end={it.ends}/>

            <div className="bm-bid-divider"/>

            <div>
              <label className="bm-bid-lbl" style={{ display: 'block', marginBottom: 8 }}>Tawaran kamu</label>
              <div className="bm-bid-input-row">
                <div className="bm-prefix-input" style={{ flex: 1 }}>
                  <span className="px">Rp</span>
                  <input
                    type="text"
                    value={Number(bidVal.replace(/\D/g, '')).toLocaleString('id-ID')}
                    onChange={e => setBidVal(e.target.value.replace(/\D/g, ''))}
                  />
                </div>
              </div>
              <div className="bm-bid-hint" style={{ marginTop: 8 }}>
                Minimum tawaran berikutnya: <b style={{ color: 'var(--ink)' }}>{fmtRp(minNext)}</b> · Kelipatan {fmtRp(50_000)}
              </div>
            </div>

            <Button
              variant="primary" size="lg"
              onClick={() => openModal(it, parseInt(bidVal.replace(/\D/g, ''), 10) || minNext)}
              style={{ width: '100%' }}
            >
              Tawar Sekarang
            </Button>

            <a style={{ fontSize: 13, color: 'var(--blue-600)', cursor: 'pointer', textAlign: 'center', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 6 }}>
              <Settings width={14} height={14}/>
              Atur Auto-bid (Proxy Bid)
            </a>

            <div className="bm-bid-divider"/>

            <div className="bm-row" style={{ gap: 8 }}>
              <button className="bm-bid-watch" style={{ flex: 1 }}>
                <Heart width={16} height={16}/>Watchlist
              </button>
              <button className="bm-bid-watch" style={{ flex: 1 }}>
                <Shield width={16} height={16}/>Buyer Protection
              </button>
            </div>
          </div>

          <div className="bm-trust-row">
            <div className="bm-trust-item"><Truck width={16} height={16}/><span>Gratis ongkir Jabodetabek</span></div>
            <div className="bm-trust-item"><Refresh width={16} height={16}/><span>Refund 14 hari</span></div>
            <div className="bm-trust-item"><Lock width={16} height={16}/><span>Pembayaran aman</span></div>
          </div>
        </div>
      </div>

      <div className="bm-detail-tabs">
        <button className={`bm-detail-tab ${tab === 'history' ? 'active' : ''}`} onClick={() => setTab('history')}>
          Riwayat Tawaran <span style={{ color: 'var(--ink-3)', marginLeft: 4 }}>({bids.length})</span>
        </button>
        <button className={`bm-detail-tab ${tab === 'desc' ? 'active' : ''}`} onClick={() => setTab('desc')}>Deskripsi</button>
        <button className={`bm-detail-tab ${tab === 'shipping' ? 'active' : ''}`} onClick={() => setTab('shipping')}>
          Pengiriman &amp; Pembayaran
        </button>
      </div>

      <div className="bm-detail-body" style={{ maxWidth: '100%', padding: '24px 0 48px' }}>
        {tab === 'history' && (
          <div className="bm-table-wrap" style={{ maxWidth: 880 }}>
            <table className="bm-table">
              <thead>
                <tr>
                  <th>Penawar</th><th>Jumlah</th><th>Waktu</th>
                  <th style={{ textAlign: 'right' }}>Status</th>
                </tr>
              </thead>
              <tbody>
                {bids.length === 0 ? (
                  <tr><td colSpan={4} style={{ textAlign: 'center', padding: 24, color: 'var(--ink-3)' }}>Belum ada tawaran.</td></tr>
                ) : bids.map((b, i) => (
                  <tr key={i} style={{ background: b.you ? 'rgba(54,101,243,0.03)' : 'transparent' }}>
                    <td>
                      <div className="item-cell">
                        <span className="bm-avatar sm" style={b.you ? { background: 'var(--blue-600)', color: '#fff' } : {}}>
                          {b.opener ? '—' : b.you ? 'AR' : b.bidder.slice(0, 2).toUpperCase()}
                        </span>
                        <span style={{ fontWeight: b.you ? 600 : 500, color: b.you ? 'var(--blue-700)' : 'var(--ink)' }}>
                          {b.bidder}
                        </span>
                      </div>
                    </td>
                    <td style={{ fontWeight: 600 }}>{fmtRp(b.amount)}</td>
                    <td style={{ color: 'var(--ink-3)' }}>{b.time}</td>
                    <td style={{ textAlign: 'right' }}>
                      {i === 0 ? <Badge tone="green">Tertinggi</Badge> :
                       b.opener ? <Badge tone="gray">Bid awal</Badge> :
                       <span style={{ color: 'var(--ink-3)', fontSize: 12 }}>Disalip</span>}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
        {tab === 'desc' && (
          <div style={{ maxWidth: 760, color: 'var(--ink-2)', lineHeight: 1.7 }}>
            <p>{it.title}</p>
            <p style={{ marginTop: 12 }}>Detail barang akan ditampilkan di sini berdasarkan deskripsi katalog.</p>
          </div>
        )}
        {tab === 'shipping' && (
          <div style={{ maxWidth: 760 }}>
            <dl className="bm-spec-table" style={{ gridTemplateColumns: '200px 1fr' }}>
              <dt>Lokasi pengiriman</dt>  <dd>Indonesia</dd>
              <dt>Kurir tersedia</dt>     <dd>JNE Express · J&amp;T · SiCepat · Anteraja</dd>
              <dt>Estimasi sampai</dt>    <dd>1–5 hari kerja</dd>
              <dt>Metode pembayaran</dt>  <dd>BidMart Wallet · Transfer Bank · E-Wallet</dd>
              <dt>Refund</dt>             <dd>14 hari setelah barang diterima</dd>
            </dl>
          </div>
        )}
      </div>
    </div>
  );
}

export default function DetailPage() {
  return (
    <Suspense fallback={<div style={{ padding: 48 }}>Memuat…</div>}>
      <DetailContent/>
    </Suspense>
  );
}
