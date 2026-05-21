'use client';
import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import Button from '@/components/ui/Button';
import Switch from '@/components/ui/Switch';
import { Upload, Plus, Check, Chevron, Gavel } from '@/components/icons';
import { CAT_TREE, fmtRp } from '@/lib/data';
import { catalogApi } from '@/lib/api';
import type { Category } from '@/types';

export default function BuatLelangPage() {
  const router = useRouter();
  const [imgs, setImgs] = useState<(string | null)[]>(['bm-art-elec', 'bm-art-elec', 'bm-art-music', null, null, null]);
  const [title, setTitle] = useState('');
  const [desc, setDesc] = useState('');
  const [startPrice, setStartPrice] = useState('0');
  const [reserve, setReserve] = useState('0');
  const [increment, setIncrement] = useState('50000');
  const [days, setDays] = useState(7);
  const [antiSnipe, setAntiSnipe] = useState(true);
  
  const [categories, setCategories] = useState<any[]>([]);
  const [selectedCatId, setSelectedCatId] = useState<string | null>(null);
  const [cat1, setCat1] = useState('');
  const [cat2, setCat2] = useState('');
  const [cat3, setCat3] = useState('');

  const [loading, setLoading] = useState(false);

  useEffect(() => {
    async function fetchCats() {
      try {
        const data = await catalogApi.getCategories();
        setCategories(data);
        if (data.length > 0) {
          setCat1(data[0].name);
          if (data[0].subCategories?.length > 0) {
            setCat2(data[0].subCategories[0].name);
            if (data[0].subCategories[0].subCategories?.length > 0) {
              setCat3(data[0].subCategories[0].subCategories[0].name);
              setSelectedCatId(data[0].subCategories[0].subCategories[0].id);
            } else {
              setSelectedCatId(data[0].subCategories[0].id);
            }
          } else {
            setSelectedCatId(data[0].id);
          }
        }
      } catch (err) {
        console.error('Failed to fetch categories:', err);
      }
    }
    fetchCats();
  }, []);

  const [mountTime] = useState<number>(() => Date.now());
  const onlyDigits = (v: string) => v.replace(/\D/g, '');
  const fmtField = (v: string) => Number(onlyDigits(v) || '0').toLocaleString('id-ID');
  const endDateTime = new Date(mountTime + days * 86400000);
  const endDateStr = endDateTime.toLocaleString('en-GB', { day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' });

  async function handleSubmit() {
    setLoading(true);
    try {
      const payload = {
        title,
        description: desc,
        startingPrice: parseFloat(startPrice),
        reservePrice: parseFloat(reserve),
        categoryId: selectedCatId,
        endTime: endDateTime.toISOString(),
      };
      
      // Mocking user for now
      const sellerId = 'usr-2001';
      const role = 'SELLER';
      
      const result = await catalogApi.createListing(payload, sellerId, role);
      console.log('Listing created:', result);
      router.push(`/detail?id=${result.id}`);
    } catch (err) {
      alert('Gagal membuat lelang: ' + (err as Error).message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="bm-page-wide">
      <nav className="bm-bread">
        <a onClick={() => router.push('/')}>Beranda</a>
        <span className="sep">/</span>
        <a>Dashboard penjual</a>
        <span className="sep">/</span>
        <span className="here">Buat lelang baru</span>
      </nav>

      <div className="bm-pg-head">
        <div>
          <h1>Buat lelang baru</h1>
          <p>Isi detail barang kamu. Lelang akan tampil setelah dipublikasikan.</p>
        </div>
        <Button variant="ghost" size="md">Simpan sebagai draft</Button>
      </div>

      <div className="bm-create-layout">
        <div className="bm-create-form">
          {/* Photos */}
          <div className="bm-section-block">
            <h3><span className="step-num">1</span> Foto produk</h3>
            <div className="bm-upload">
              <Upload width={28} height={28}/>
              <div className="t">Tarik foto ke sini atau klik untuk pilih</div>
              <div className="s">JPG, PNG, atau WEBP — maksimum 12 foto, ukuran 8 MB per foto</div>
            </div>
            <div className="bm-upload-thumbs">
              {imgs.map((art, i) => (
                <div key={i} className={`bm-upload-thumb ${i === 0 && art ? 'main' : ''}`}>
                  {art ? (
                    <>
                      <div className={art} style={{ width: '100%', height: '100%' }}/>
                      <button className="x" onClick={() => { const next = [...imgs]; next[i] = null; setImgs(next); }}>×</button>
                    </>
                  ) : (
                    <div style={{ width: '100%', height: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', gap: 4, color: 'var(--ink-4)', background: 'var(--surface-2)' }}>
                      <Plus width={16} height={16}/>
                      <span style={{ fontSize: 10 }}>Foto {i + 1}</span>
                    </div>
                  )}
                </div>
              ))}
            </div>
          </div>

          {/* Details */}
          <div className="bm-section-block">
            <h3><span className="step-num">2</span> Detail barang</h3>
            <div className="bm-field">
              <label>Judul listing</label>
              <input value={title} onChange={e => setTitle(e.target.value)} maxLength={120} placeholder="Contoh: Sony WH-1000XM5 Wireless Headset"/>
              <span className="hint">{title.length}/120 karakter</span>
            </div>
            <div className="bm-field">
              <label>Deskripsi</label>
              <textarea rows={6} value={desc} onChange={e => setDesc(e.target.value)} placeholder="Jelaskan kondisi barang Anda..."/>
              <span className="hint">{desc.length}/4000 karakter</span>
            </div>
            <div className="bm-field" style={{ marginBottom: 6 }}><label>Kategori</label></div>
            <div className="bm-cat-cascade">
              <div className="bm-cat-col">
                {categories.map(k => (
                  <div key={k.id} className={`it ${cat1 === k.name ? 'active' : ''}`}
                    onClick={() => { setCat1(k.name); setCat2(''); setCat3(''); setSelectedCatId(k.id); }}>
                    <span>{k.name}</span><Chevron width={12} height={12}/>
                  </div>
                ))}
              </div>
              <div className="bm-cat-col">
                {(categories.find(c => c.name === cat1)?.subCategories || []).map((k: any) => (
                  <div key={k.id} className={`it ${cat2 === k.name ? 'active' : ''}`}
                    onClick={() => { setCat2(k.name); setCat3(''); setSelectedCatId(k.id); }}>
                    <span>{k.name}</span><Chevron width={12} height={12}/>
                  </div>
                ))}
              </div>
              <div className="bm-cat-col">
                {(categories.find(c => c.name === cat1)?.subCategories.find((s: any) => s.name === cat2)?.subCategories || []).map((k: any) => (
                  <div key={k.id} className={`it ${cat3 === k.name ? 'active' : ''}`} 
                    onClick={() => { setCat3(k.name); setSelectedCatId(k.id); }}>
                    <span>{k.name}</span>
                    {cat3 === k.name && <Check width={14} height={14} style={{ color: 'var(--blue-600)' }}/>}
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* Pricing & Rules */}
          <div className="bm-section-block">
            <h3><span className="step-num">3</span> Harga &amp; aturan lelang</h3>
            <div className="bm-grid-3">
              <div className="bm-field">
                <label>Harga awal (start)</label>
                <div className="bm-prefix-input"><span className="px">Rp</span><input value={fmtField(startPrice)} onChange={e => setStartPrice(onlyDigits(e.target.value))}/></div>
              </div>
              <div className="bm-field">
                <label>Reserve price</label>
                <div className="bm-prefix-input"><span className="px">Rp</span><input value={fmtField(reserve)} onChange={e => setReserve(onlyDigits(e.target.value))}/></div>
              </div>
              <div className="bm-field">
                <label>Kelipatan bid minimum</label>
                <div className="bm-prefix-input"><span className="px">Rp</span><input value={fmtField(increment)} onChange={e => setIncrement(onlyDigits(e.target.value))}/></div>
              </div>
            </div>
            <div className="bm-field" style={{ marginTop: 6 }}>
              <label>Durasi lelang</label>
              <div style={{ display: 'flex', gap: 8 }}>
                {[1, 3, 5, 7, 10, 14].map(n => (
                  <button key={n} onClick={() => setDays(n)} style={{ padding: '9px 16px', borderRadius: 8, border: '1px solid ' + (days === n ? 'var(--ink)' : 'var(--border)'), background: days === n ? 'var(--ink)' : 'var(--surface)', color: days === n ? '#fff' : 'var(--ink)', fontSize: 13, fontWeight: 500, cursor: 'pointer' }}>
                    {n} hari
                  </button>
                ))}
              </div>
            </div>
          </div>

          <div style={{ display: 'flex', gap: 10, justifyContent: 'flex-end', padding: '20px 0 0' }}>
            <Button variant="ghost" size="lg" disabled={loading}>Pratinjau</Button>
            <Button variant="primary" size="lg" loading={loading} onClick={handleSubmit} leftIcon={<Gavel width={16} height={16}/>}>Publikasikan Lelang</Button>
          </div>
        </div>

        <aside className="bm-preview-sticky">
          <div className="bm-preview-h">Pratinjau listing</div>
          <div className="bm-preview-card">
            <div className="bm-listing-image" style={{ borderRadius: 8 }}>
              <div className={`bm-listing-image-fill ${imgs[0] || 'bm-art-elec'}`}/>
              <span className="bm-listing-badge bm-listing-badge-amber">BARU</span>
            </div>
            <div className="bm-listing-meta" style={{ padding: '12px 4px 0' }}>
              <div className="bm-listing-title">{title || 'Judul lelang kamu'}</div>
              <div className="bm-listing-price">{fmtRp(Number(startPrice) || 0)}</div>
              <div className="bm-listing-sub"><span>{days}h tersisa</span><span> · 0 bid</span></div>
            </div>
          </div>
        </aside>
      </div>
    </div>
  );
}
