'use client';
import { useState, useRef } from 'react';
import { useRouter } from 'next/navigation';
import Logo from '@/components/Logo';
import Button from '@/components/ui/Button';
import { Eye, EyeOff, Shield } from '@/components/icons';
import { authApi } from '@/lib/api';
import { useAuth } from '@/store/auth-context';

export default function LoginPage() {
  const router = useRouter();
  const { login: authLogin } = useAuth();
  const [mode, setMode] = useState<'signin' | 'register' | 'otp'>('signin');
  const [showPw, setShowPw] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Sign In State
  const [email, setEmail] = useState('aulia.r@gmail.com');
  const [password, setPassword] = useState('supersecret');

  // Register State
  const [regUsername, setRegUsername] = useState('');
  const [regEmail, setRegEmail] = useState('');
  const [regPassword, setRegPassword] = useState('');

  const [otp, setOtp] = useState(['', '', '', '', '', '']);
  const otpRefs = useRef<(HTMLInputElement | null)[]>([]);

  async function handleSignIn(e: React.FormEvent) {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      const result = await authApi.login({ email, password });
      await authLogin(result.accessToken);
      setMode('otp'); // In this demo, we go to OTP after login
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setLoading(false);
    }
  }

  async function handleRegister(e: React.FormEvent) {
    e.preventDefault();
    setLoading(true);
    setError('');
    try {
      await authApi.login({ username: regUsername, email: regEmail, password: regPassword }); // Using login as placeholder for register
      alert('Registrasi berhasil! Silakan login.');
      setMode('signin');
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setLoading(false);
    }
  }

  function handleOtpChange(i: number, v: string) {
    if (v && !/^\d$/.test(v)) return;
    const next = [...otp]; next[i] = v; setOtp(next);
    if (v && i < 5) otpRefs.current[i + 1]?.focus();
  }
  function handleOtpKey(i: number, e: React.KeyboardEvent) {
    if (e.key === 'Backspace' && !otp[i] && i > 0) otpRefs.current[i - 1]?.focus();
  }

  return (
    <div className="bm-auth-shell">
      <div className="bm-auth-logo" onClick={() => router.push('/')} style={{ cursor: 'pointer' }}><Logo size={30}/></div>
      <div className="bm-auth-card">
        {mode === 'otp' ? (
          <>
            <div style={{ textAlign: 'center', marginBottom: 6 }}>
              <div style={{ width: 56, height: 56, borderRadius: 999, background: 'var(--blue-50)', color: 'var(--blue-600)', display: 'inline-flex', alignItems: 'center', justifyContent: 'center' }}>
                <Shield width={28} height={28}/>
              </div>
            </div>
            <h1 style={{ textAlign: 'center', fontSize: 22, marginTop: 14 }}>Verifikasi dua langkah</h1>
            <p className="sub" style={{ textAlign: 'center' }}>
              Masukkan 6 digit kode dari aplikasi <b style={{ color: 'var(--ink)' }}>Google Authenticator</b> kamu.
            </p>
            <div className="bm-otp">
              {otp.map((v, i) => (
                <input
                  key={i}
                  ref={el => { otpRefs.current[i] = el; }}
                  type="text" inputMode="numeric" maxLength={1}
                  value={v} className={v ? 'filled' : ''}
                  onChange={e => handleOtpChange(i, e.target.value)}
                  onKeyDown={e => handleOtpKey(i, e)}
                />
              ))}
            </div>
            <div style={{ fontSize: 12, color: 'var(--ink-3)', textAlign: 'center', marginBottom: 18 }}>
              Kode akan kadaluarsa dalam <b style={{ color: 'var(--red-600)', fontVariantNumeric: 'tabular-nums' }}>00:42</b>
            </div>
            <Button variant="primary" size="lg" style={{ width: '100%' }} onClick={() => router.push('/')}>
              Verifikasi
            </Button>
            <div className="bm-auth-foot" style={{ justifyContent: 'center', marginTop: 16, gap: 4, flexWrap: 'wrap' }}>
              Tidak punya akses ke aplikasi? <a>Pakai SMS</a> · <a>Gunakan recovery code</a>
            </div>
            <div style={{ marginTop: 18, paddingTop: 16, borderTop: '1px solid var(--border)', textAlign: 'center' }}>
              <a style={{ fontSize: 13, color: 'var(--ink-2)', cursor: 'pointer' }} onClick={() => setMode('signin')}>
                ← Kembali ke Sign In
              </a>
            </div>
          </>
        ) : (
          <>
            <h1>{mode === 'signin' ? 'Masuk ke BidMart' : 'Buat akun BidMart'}</h1>
            <p className="sub">
              {mode === 'signin'
                ? 'Lanjutkan menawar di lelang yang sedang kamu pantau.'
                : 'Gratis untuk daftar. Mulai menawar dalam 30 detik.'}
            </p>
            <div className="bm-auth-tabs">
              <button className={`bm-auth-tab ${mode === 'signin' ? 'active' : ''}`} onClick={() => setMode('signin')}>Sign In</button>
              <button className={`bm-auth-tab ${mode === 'register' ? 'active' : ''}`} onClick={() => setMode('register')}>Register</button>
            </div>

            {error && <div style={{ padding: '10px 14px', background: 'var(--red-50)', color: 'var(--red-600)', borderRadius: 8, fontSize: 13, marginBottom: 16 }}>{error}</div>}

            {mode === 'signin' ? (
              <form onSubmit={handleSignIn}>
                <div className="bm-field">
                  <label>Email</label>
                  <input type="email" placeholder="kamu@email.com" value={email} onChange={e => setEmail(e.target.value)}/>
                </div>
                <div className="bm-field">
                  <label style={{ display: 'flex', justifyContent: 'space-between' }}>
                    Password
                    <a style={{ color: 'var(--blue-600)', fontSize: 12, fontWeight: 500, cursor: 'pointer' }}>Lupa password?</a>
                  </label>
                  <div style={{ position: 'relative', display: 'flex' }}>
                    <input type={showPw ? 'text' : 'password'} placeholder="••••••••••" value={password} onChange={e => setPassword(e.target.value)} style={{ flex: 1, paddingRight: 40 }}/>
                    <button type="button" onClick={() => setShowPw(p => !p)} style={{ position: 'absolute', right: 10, top: '50%', transform: 'translateY(-50%)', background: 'transparent', border: 0, color: 'var(--ink-3)', cursor: 'pointer', padding: 6 }}>
                      {showPw ? <EyeOff width={18} height={18}/> : <Eye width={18} height={18}/>}
                    </button>
                  </div>
                </div>
                <div className="bm-field-row">
                  <label><input type="checkbox" defaultChecked/>Ingat saya di perangkat ini</label>
                </div>
                <Button variant="primary" size="lg" style={{ width: '100%' }} type="submit" loading={loading}>Masuk</Button>
                <div style={{ display: 'flex', alignItems: 'center', gap: 10, margin: '18px 0', color: 'var(--ink-3)', fontSize: 12 }}>
                  <div style={{ flex: 1, height: 1, background: 'var(--border)' }}/>
                  <span>atau</span>
                  <div style={{ flex: 1, height: 1, background: 'var(--border)' }}/>
                </div>
                <Button variant="secondary" size="lg" style={{ width: '100%', marginBottom: 8 }}>
                  <svg width="18" height="18" viewBox="0 0 18 18">
                    <path fill="#4285F4" d="M17.64 9.2c0-.637-.057-1.251-.164-1.84H9v3.481h4.844a4.14 4.14 0 0 1-1.796 2.716v2.258h2.908c1.702-1.567 2.684-3.874 2.684-6.615z"/>
                    <path fill="#34A853" d="M9 18c2.43 0 4.467-.806 5.956-2.18l-2.908-2.259c-.806.54-1.837.86-3.048.86-2.344 0-4.328-1.584-5.036-3.711H.957v2.332A8.997 8.997 0 0 0 9 18z"/>
                    <path fill="#FBBC05" d="M3.964 10.71A5.41 5.41 0 0 1 3.682 9c0-.593.102-1.17.282-1.71V4.958H.957A8.996 8.996 0 0 0 0 9c0 1.452.348 2.827.957 4.042l3.007-2.332z"/>
                    <path fill="#EA4335" d="M9 3.58c1.321 0 2.508.454 3.44 1.345l2.582-2.58C13.463.891 11.426 0 9 0A8.997 8.997 0 0 0 .957 4.958L3.964 7.29C4.672 5.163 6.656 3.58 9 3.58z"/>
                  </svg>
                  Lanjutkan dengan Google
                </Button>
                <div className="bm-auth-foot">
                  <span>Belum punya akun?</span>
                  <a onClick={() => setMode('register')}>Daftar sekarang</a>
                </div>
              </form>
            ) : (
              <form onSubmit={handleRegister}>
                <div className="bm-field">
                  <label>Username</label>
                  <input placeholder="johndoe" value={regUsername} onChange={e => setRegUsername(e.target.value)}/>
                </div>
                <div className="bm-field"><label>Email</label><input type="email" placeholder="kamu@email.com" value={regEmail} onChange={e => setRegEmail(e.target.value)}/></div>
                <div className="bm-field">
                  <label>Password</label>
                  <input type="password" placeholder="Minimal 10 karakter" value={regPassword} onChange={e => setRegPassword(e.target.value)}/>
                  <span className="hint">Gunakan campuran huruf, angka, dan simbol.</span>
                </div>
                <Button variant="primary" size="lg" style={{ width: '100%' }} type="submit" loading={loading}>Buat Akun</Button>
                <div className="bm-auth-foot">
                  <span>Sudah punya akun?</span>
                  <a onClick={() => setMode('signin')}>Masuk</a>
                </div>
              </form>
            )}
          </>
        )}
      </div>
      <div style={{ fontSize: 12, color: 'var(--ink-3)', marginTop: 28, display: 'flex', gap: 14 }}>
        <a style={{ color: 'var(--ink-3)' }}>Privasi</a>
        <a style={{ color: 'var(--ink-3)' }}>Syarat</a>
        <a style={{ color: 'var(--ink-3)' }}>Bantuan</a>
        <a style={{ color: 'var(--ink-3)' }}>© 2026 BidMart Indonesia</a>
      </div>
    </div>
  );
}
