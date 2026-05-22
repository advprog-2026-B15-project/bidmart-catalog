import http from 'k6/http';
import { check, sleep } from 'k6';

/**
 * SKENARIO LOAD TEST - BIDMART CATALOG
 * 
 * Skenario ini menyimulasikan pengguna yang:
 * 1. Melihat daftar katalog (GET /api/listings)
 * 2. Mencari barang berdasarkan judul (GET /api/listings?title=...)
 * 3. Melihat daftar kategori (GET /api/categories)
 */

export const options = {
  stages: [
    { duration: '30s', target: 20 },  // Naik ke 20 pengguna dalam 30 detik
    { duration: '1m', target: 50 },   // Naik ke 50 pengguna dalam 1 menit
    { duration: '30s', target: 0 },   // Turun kembali ke 0
  ],
  thresholds: {
    // 95% request harus di bawah 500ms
    http_req_duration: ['p(95)<500'],
    // Tingkat kegagalan harus di bawah 1%
    http_req_failed: ['rate<0.01'],
  },
};

const BASE_URL = 'http://54.164.111.51:8082';

export default function () {
  // Melihat katalog utama
  const resListings = http.get(`${BASE_URL}/api/listings`);
  check(resListings, {
    'listings status is 200': (r) => r.status === 200,
    'listings has content': (r) => r.json().content !== undefined,
  });
  sleep(1);

  // Mencari barang spesifik
  const resSearch = http.get(`${BASE_URL}/api/listings?title=MacBook`);
  check(resSearch, {
    'search status is 200': (r) => r.status === 200,
  });
  sleep(1);

  // Melihat kategori
  const resCategories = http.get(`${BASE_URL}/api/categories`);
  check(resCategories, {
    'categories status is 200': (r) => r.status === 200,
  });
  sleep(2);
}
