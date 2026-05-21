# [CATALOG] API Docs

# **Bidmart Catalog API Contract**

Owner: bidmart-catalog

| Versi | Pengarang | Tanggal | Notes |
| :---: | :---: | :---: | ----- |
| 1.0 | Sean | 2026-03-05 | First version |
| 1.1 | Sean | 2026-05-21 | Finalized endpoints and removed Thymeleaf references as frontend is now React/Next.js |

## **1) Tujuan**

Dokumen ini mendefinisikan kontrak API internal (antar-microservice) dan eksternal untuk modul Catalog dan Listing Management.

## **2) Auth Strategy (MVP)**

Untuk milestone ini, gunakan header sementara:
* `X-User-Id`: `<user-id>` (Contoh: `usr-2406`)
* `X-User-Role`: `<user-role>` (Contoh: `SELLER` atau `BUYER`)

Catatan:
* Endpoint berjenis GET (Katalog Publik) bersifat terbuka (tidak wajib auth).
* Endpoint mutasi data (POST, PATCH, PUT, DELETE) wajib menyertakan header auth dan akan divalidasi otorisasinya.

## **3) Common Rules**

1. Semua format response adalah **JSON**.
2. **Ownership Check**: Penjual hanya boleh memodifikasi/mempublikasi listing miliknya sendiri.
3. Timestamp menggunakan format **ISO-8601 UTC**.
4. Paginasi dikembalikan dalam format Page dari Spring Data (`content`, `totalPages`, `totalElements`).

## **4) Error Response Standard**

Kode HTTP yang digunakan:
* **200/201 (SUCCESS)**: Operasi berhasil.
* **400 (BAD_REQUEST)**: Validasi gagal (misal: payload tidak lengkap).
* **401 (UNAUTHORIZED)**: Tidak ada kredensial.
* **403 (FORBIDDEN)**: Akses ditolak (bukan pemilik listing atau bukan seller).
* **404 (NOT_FOUND)**: Data listing atau kategori tidak ditemukan.
* **500 (INTERNAL_ERROR)**: Kesalahan server.

---

## **5) Endpoint Contract (REST API)**

### **5.1 GET /api/listings**
Deskripsi: Mengambil daftar listing lelang. Digunakan untuk katalog publik dan pencarian.
Headers: (Opsional)
Query params:
* `title` (opsional)
* `categoryId` (opsional)
* `minPrice` (opsional)
* `maxPrice` (opsional)
* `status` (opsional, default untuk publik biasanya ACTIVE)
* `page` (opsional, default 0)
* `size` (opsional, default 10)

Success 200:
```json
{
  "content": [
    {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "title": "MacBook Pro M2 2023",
      "category": { "id": "cat-001", "name": "Elektronik" },
      "sellerId": "usr-2406",
      "currentPrice": 15000000.0,
      "status": "ACTIVE",
      "endTime": "2026-03-10T10:00:00Z"
    }
  ],
  "pageable": { "pageNumber": 0, "pageSize": 10 },
  "totalElements": 1,
  "totalPages": 1
}
```

### **5.2 POST /api/listings**
Deskripsi: Membuat draf listing baru.
Headers: 
* `X-User-Id` (required)
* `X-User-Role` (required, must be SELLER)

Request Body:
```json
{
  "title": "Mechanical Keyboard Custom",
  "description": "Kondisi baru",
  "startingPrice": 1200000,
  "reservePrice": 1500000,
  "categoryId": "cat-001",
  "endTime": "2026-03-06T12:00:00Z"
}
```
Success 201: Mengembalikan objek Listing dengan status `DRAFT`.

### **5.3 GET /api/listings/{id}**
Deskripsi: Mengambil detail listing secara spesifik. Digunakan oleh Frontend dan Modul Auction.
Path params: `id` (UUID)

Success 200:
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "title": "MacBook Pro M2 2023",
  "currentPrice": 15000000.0,
  "status": "ACTIVE"
}
```

### **5.4 PUT /api/listings/{id}**
Deskripsi: Mengubah detail listing secara menyeluruh (Hanya jika belum ada bid).
Headers: `X-User-Id`, `X-User-Role`

### **5.5 DELETE /api/listings/{id}**
Deskripsi: Menghapus listing (Hanya jika belum ada bid).
Headers: `X-User-Id`, `X-User-Role`

### **5.6 PATCH /api/listings/{id}/publish**
Deskripsi: Mengubah status listing dari `DRAFT` menjadi `ACTIVE`.
Headers: `X-User-Id` (Wajib pemilik listing)

Success 200: Mengembalikan objek Listing dengan status `ACTIVE`.

### **5.7 PATCH /api/listings/{id}/current-price**
Deskripsi: Memperbarui harga terkini. **Dipanggil oleh Modul Auction** ketika ada bid sah.
Request Body:
```json
{
  "newPrice": 1600000.0
}
```

### **5.8 GET /api/listings/{id}/validate**
Deskripsi: Digunakan oleh Modul Auction untuk mengecek apakah suatu listing masih valid untuk di-bid (Status ACTIVE dan waktu belum habis).
Success 200:
```json
{
  "isValid": true,
  "reason": "Valid"
}
```
(Jika false, reason akan menjelaskan penyebabnya misal "Auction time has ended").

### **5.9 GET /api/listings/seller/{sellerId}/stats**
Deskripsi: Mengambil statistik performa penjualan (jumlah listing, dll).
Path params: `sellerId`

### **5.10 GET /api/categories**
Deskripsi: Mengambil daftar seluruh kategori (termasuk sub-kategori) untuk dropdown frontend.

---

## **6) Event Consumption Contract**
Modul Catalog mendengarkan event dari RabbitMQ (dikirim oleh Modul Auction):

* **BidPlacedEvent**: Memperbarui `currentPrice` dan `bidCount`.
* **AuctionClosedEvent**: (Draft) Mengubah status listing menjadi `CLOSED` jika tidak terjual.
