# BidMart B15

# **B15 \- BidMart**

| Link |  |  |  |
| ----- | :---- | ----- | ----- |
| Figma | [https://www.figma.com/design/R6GW6eT7p4gh18NosIxibH/Full-E-Commerce-Website-UI-UX-Design--Community-?node-id=1-3\&t=eRvPylPDB0p6zTSw-1](https://www.figma.com/design/R6GW6eT7p4gh18NosIxibH/Full-E-Commerce-Website-UI-UX-Design--Community-?node-id=1-3&t=eRvPylPDB0p6zTSw-1) |  |  |
| GitHub Organization | [https://github.com/advprog-2026-B15-project](https://github.com/advprog-2026-B3-project) |  |  |
| Auth (Repo Service) | [https://github.com/advprog-2026-B15-project/bidmart-authentication](https://github.com/advprog-2026-B3-project/palmery-auth) |  |  |
| Catalog (Repo Service) | [https://github.com/advprog-2026-B15-project/bidmart-catalog](https://github.com/advprog-2026-B3-project/palmery-manage) |  |  |
| Auction (Repo Service) | [https://github.com/advprog-2026-B15-project/bidmart-auction](https://github.com/advprog-2026-B3-project/palmery-payment) |  |  |
| Booking (Repo Service) | [https://github.com/advprog-2026-B15-project/bidmart-booking](https://palmery.my.id/) |  |  |
| Wallet (Repo Service) | [https://github.com/advprog-2026-B15-project/bidmart-wallet](https://github.com/advprog-2026-B15-project/bidmart-wallet.git) |  |  |

# **Microservices & Domain Blueprint**

| Domain Bisnis & Modul | Nama Service / Repo | Tech Stack (Fullstack) | PIC Utama | Fokus & Tanggung Jawab Utama |  |
| :---- | :---- | :---- | :---- | :---- | ----- |
| **Autentikasi dan Manajemen Pengguna** | bidmart-authentication | Spring Boot, PostgreSQL, | **Azka** | Mengurus registrasi, login (JWT), dan hierarki Role-Based Access Control (RBAC). |  |
| **Katalog dan Manajemen Listing** | bidmart-catalog | Spring Boot, PostgreSQL, | **Sean** | Manajemen siklus listing (DRAFT → ACTIVE), hierarki kategori, fitur pencarian & filtering, serta Caching untuk membaca daftar barang. |  |
| **Lelang dan Penawaran (Bidding)** | bidmart-auction | Spring Boot, PostgreSQL, | **Keisha** | Siklus lelang otomatis, aturan anti-sniping (ekstensi 2 menit), validasi hold saldo, penanganan konkurensi (Row-level locking), dan publish event penawaran. |  |
| **Dompet dan Manajemen Saldo** | bidmart-wallet | Spring Boot, PostgreSQL, | **Arya** | Sinkronisasi Hold/Release/Convert saldo dengan Bidding, topup/withdraw, proteksi idempotency, dan jejak audit (audit trail). |  |
| **Pemesanan dan Notifikasi** | bidmart-booking | Spring Boot, PostgreSQL, | **Rabiul** | Menjadi event consumer untuk mengotomatisasi siklus pesanan saat lelang ditutup, serta me-manage preferensi dan pengiriman Notifikasi Real-time. |  |
| **FrontEnd** | bidmart-frontend | Next.js |  |  |  |

# **Infrastructure**

| Kategori Sistem | Teknologi yang Digunakan | Implementasi & Fungsi |  |
| :---- | :---- | :---- | ----- |
| **Arsitektur Utama** | Microservices Architecture | Memisahkan beban server ke dalam 5 service mandiri untuk menjamin stabilitas sistem. |  |
| **Database & Cache** | PostgreSQL, Redis | Postgres (ACID) untuk konsistensi data transaksi. |  |
| **Message Broker** | RabbitMQ | Untuk komunikasi asinkronus antar-layanan. |  |
| **Real-time Engine** | WebSocket / SSE | Membuka koneksi persisten ke klien untuk mengirimkan pembaruan harga lelang dan notifikasi pesanan tanpa refresh halaman. |  |
| **Code Standardization** | Checkstyle Linter, ESLint, | Kode wajib rapi dan bersih dari peringatan (warnings) sebelum bisa di-merge. |  |
| **CI/CD & Security Scan** | GitHub Actions, SonarCloud | CI Pipeline otomatis menjalankan build tes & SonarCloud. CD Pipeline akan terblokir jika Quality Gate gagal. |  |

# **Non-Functional Requirements**

| Layer | Syarat & Tools | Deskripsi |  |
| :---- | :---- | :---- | ----- |
| **Linting & Formatting** | Checkstyle Linter, ESLint | Memastikan konsistensi penulisan kode antar anggota kelompok. Pipeline akan gagal jika ada pelanggaran standar gaya penulisan kode. |  |
| **Static Code Analysis** | SonarCloud | Pemindaian otomatis untuk code smells, bugs, dan security vulnerabilities di CI/CD Pipeline. |  |
| **Testing & Coverage** | JUnit, Mockito, & Jacoco | Wajib mengimplementasikan Unit Tests dan Integration Tests. Laporan coverage dicek langsung di CI Pipeline. |  |
| **Version Control & Review** | GitHub Branch Protection Rules | Setiap perubahan wajib melalui Pull Request (PR). Minimal harus ada 1 persetujuan (approval/review) dari anggota tim lain sebelum kode bisa di-merge ke branch utama (main). |  |
| **CI / CD Pipeline** | GitHub Actions | CI Script : Menjalankan build, lint, dan tes setiap ada Push/Pull Request. CD Script: Versi dummy yang secara otomatis terblokir jika kualitas dari SonarCloud tidak lulus. |  |

# Milestone

# **Milestone 25%** 

**Azka (Auth)**

* Registrasi user dengan hashed password  
* Mocking fitur verifikasi email  
* Login dan pembuatan JWT access token  
* Pembuatan API GET /api/users/me  
* Migrasi DB (users, verification\_tokens)  
* Unit & basic integration tests

**Sean (Catalog)**

* Fitur buat & ambil listing (DRAFT → ACTIVE)  
* Migrasi DB (listings, categories, images)  
* Validasi peran (RBAC) dasar via Auth  
* Implementasi paginasi sederhana  
* Unit & basic integration tests

**Keisha (Auction)**

* Siklus hidup lelang awal (DRAFT → ACTIVE)  
* Migrasi DB tabel auctions & bids  
* Logika penawaran dasar (tanpa validasi dompet)  
* Setup frontend UI  
* Unit & integration tests

**Arya (Wallet)**

* Pembuatan otomatis dompet per user  
* Migrasi DB tabel dompet & transaksi  
* API dasar: Top-up, withdraw, dan riwayat  
* Pencatatan log perubahan saldo  
* Unit & integration tests

**Rabiul (Booking)**

* Migrasi DB pesanan, pengiriman, & notifikasi  
* Event consumer MVP (AuctionClosed, WinnerDetermined)  
* Pembuatan otomatis pesanan bagi pemenang  
* API dasar baca pesanan & notifikasi  
* Unit & integration tests

# **Milestone 50%**

**Azka (Auth)**

* Refresh token (rotasi & secure storage)  
* Fitur logout & pencabutan sesi  
* Mock reset password  
* Pembuatan roles table & API Admin  
* Spek OpenAPI untuk endpoint Auth

**Sean (Catalog)**

* Fitur search & filtering (kategori, harga, waktu)  
* Manajemen hierarki kategori  
* Restriksi edit/delete sebelum ada bid  
* Spek OpenAPI untuk endpoint Katalog

**Keisha (Auction)**

* API Call sinkron menahan saldo   
* Validasi nominal bid harus lebih tinggi dari penawaran tertinggi  
* Eksekusi aturan anti-sniping (+2 menit) (ACTIVE → EXTENDED)  
* Publikasi event BidPlaced ke RabbitMQ  
* Spek OpenAPI untuk endpoint Auction

**Arya (Wallet)**

* API internal integrasi Bidding (Hold, Release, Convert)  
* Validasi saldo cukup untuk ditahan  
* Proteksi idempotency pada transaksi finansial

**Rabiul (Booking)**

* Manajemen siklus pesanan (CREATED → COMPLETED)  
* Update resi & konfirmasi penerimaan barang  
* Subscribe BidPlaced, dll untuk notifikasi baru  
* Spek OpenAPI untuk endpoint Booking

# **Milestone 75%**

**Azka (Auth)**

* Integrasi Autentikasi 2FA (TOTP)  
* Deteksi & penanganan penggunaan refresh-token ilegal  
* Rate-limiting pada fungsi login  
* Publikasi event siklus user ke Broker  
* CI mencakup tes keamanan tambahan

**Sean (Catalog)**

* Event handling asinkronus (BidPlaced, AuctionClosed)  
* Pembaruan harga aktual dengan idempotent consumer  
* Caching untuk pembacaan daftar listing  
* Penambahan coverage reporting di CI

**Keisha (Auction)**

* Penanganan konkurensi dengan *PostgreSQL row-level locking* Redis distributed lock  
* Otomasi penutupan lelang (CLOSED → WON/UNSOLD)  
* Publish event tutup lelang memicu pesanan  
* Reporting SonarCloud di CI

**Arya (Wallet)**

* Jejak audit penuh (immutable transaction log)  
* Publish event finansial ke Notifikasi  
* Integrasi pengecekan coverage di CI

**Rabiul (Booking)**

* Integrasi WebSocket / SSE untuk update real-time  
* Idempotent consumer dengan retry/dead-letter  
* Implementasi audit log perubahan status pesanan  
* Uji integrasi end-to-end

# **Milestone 100%**

**Azka (Auth)**

* Dokumentasi final (README & OpenAPI)  
* Security hardening checklist selesai  
* CD diverifikasi agar tidak bisa rilis jika CI gagal  
* Penyusunan design report

**Sean (Catalog)**

* Finalisasi siklus DRAFT → WON/UNSOLD  
* Optimasi database dengan indexing  
* Penambahan API statistik performa penjual  
* Dokumen final & full test coverage

**Keisha (Auction)**

* Finalisasi siklus & otorisasi ketat (Penjual & Pemenang)  
* Optimasi indexing pada kolom listing\_id & status  
* Dokumen final (README & OpenAPI)  
* Pengecekan akhir pipeline CI/CD

**Arya (Wallet)**

* Finalisasi integrasi dompet dengan Bidding  
* Optimasi kueri database  
* Security hardening & dokumen final  
* Full unit & integration test coverage

**Rabiul (Booking)**

* Implementasi fitur sengketa pasca-pengiriman (dispute)  
* Finalisasi manajemen preferensi notifikasi  
* Validasi otorisasi pembeli & penjual  
* Optimasi throughput event & persiapan demo

# Module

# **Autentikasi dan Manajemen Pengguna**

**PIC:** Azka

* Registrasi Akun & Login Pengguna \[25%\]  
* Logout & Pemulihan Password \[50%\]  
* Manajemen Hak Akses \[50%\]  
* Keamanan Akun (Autentikasi Dua Langkah / 2FA) \[75%\]

# **Katalog dan Manajemen Listing**

**PIC:** Sean

* Publikasi Barang Lelang Baru (Katalog) \[25%\]  
* Paginasi Sederhana \[25%\]  
* Fitur Pencarian & Filter Barang \[50%\]  
* Edit & Delete Barang (Saat DRAFT) \[50%\]  
* Update Harga Barang (Saat ACTIVE/EXTENDED) \[75%\]  
* Statistik Seller \[100%\]

# **Lelang dan Penawaran (Bidding)**

**PIC:** Keisha

* Sistem Penawaran Harga (Bidding) Dasar \[25%\]  
* Setup UI \[25%\]  
* Sistem Anti-Sniping (Perpanjangan waktu otomatis jika ada bid di detik akhir) \[50%\]  
* Integrasi Syarat Saldo (Tidak bisa bid kalau saldo kurang) \[50%\]  
* Penentuan Pemenang & Penutupan Lelang Otomatis \[75%\]

# **Dompet dan Manajemen Saldo**

**PIC:** Arya

* Aktivasi Dompet Digital Pengguna \[25%\]  
* Fitur Top-up (Isi Saldo) & Withdraw (Tarik Dana) \[25%\]  
* Sistem Saldo Terkunci (Saldo ditahan sementara saat ikut lelang) \[50%\]  
* Jejak Audit Keuangan (Bukti transaksi tak bisa diubah) \[75%\]

# **Pemesanan dan Notifikasi**

**PIC:** Rabiul

* Pembuatan Pesanan Otomatis & Notifikasi (Saat menang/kalah lelang) \[25%\]  
* Pelacakan & Update Status Pemesanan Barang \[50%\]  
* Preferensi Notifikasi \[50%\]  
* Notifikasi Real-time / Pop-up In-App \[75%\]  
* Pengaturan Preferensi Notifikasi & Pengajuan Masalah \[100%\]

# Model

# **\[MODULE\] Model**

Owner: \[SERVICE\]

| Versi | Pengarang | Tanggal | Notes |
| :---: | :---: | :---: | ----- |
|  |  |  |  |

| Nama Tabel | Deskripsi | Field / Kolom | Tipe Data | Deskripsi |
| ----- | ----- | ----- | ----- | ----- |
|  |  |  |  |  |

# API Docs

# **Bidmart \[MODULE\] API/Auth Contract**

Owner: \[SERVICE\]

| Versi | Pengarang | Tanggal | Notes |
| :---: | :---: | :---: | ----- |
|  |  |  |  |

# \[WALLET\] API Docs

# **Bidmart Wallet API/Auth Contract**

Owner: Bidmart Wallet

| Versi | Pengarang | Tanggal | Notes |
| :---: | :---: | :---: | ----- |
| 1.0 | Arya | 2026-03-06 |  First Version |
| 1.1 | Arya | 2026-04-17 | Updated error response standard |

## **1\) Tujuan**

Dokumen ini mendefinisikan kontrak API (internal dan external) untuk MVP Wallet

## **2\) Auth Strategy (MVP)**

Untuk milestone ini gunakan header sementara:

* X-User-Id: \<user-id\>

Contoh: X-User-Id: usr-3001

Catatan:

* Ini sementara untuk MVP.  
* Target berikutnya migrasi ke JWT (Authorization: Bearer \<token\>).  
* Semua endpoint wajib auth.


## **3\) Common Rules**

- Timestamp menggunakan format **ISO-8601**.  
- Semua response format JSON.  
- Ownership check, user hanya boleh akses data miliknya.

## **4\) Error Response Standard**

Kode HTTP:

* 400 BAD\_REQUEST → validasi gagal  
* 401 UNAUTHORIZED → tidak ada auth  
* 404 NOT\_FOUND → wallet tidak ditemukan  
* 409 CONFLICT → duplicate referenceId (idempotency)  
* 500 INTERNAL\_ERROR → server error

## **5\) Endpoint Contract**

### **5.1 GET /api/wallet/{userId}** 

Deskripsi:  
Mengambil informasi saldo user 

Headers:

* X-User-Id

### **5.2 POST /api/wallet/{userId}/topup** 

Deskripsi:  
Menambahkan saldo ke wallet

Headers:

* X-User-Id

### **5.3 POST /api/wallet/{userId}/withdraw** 

Deskripsi:  
Menarik saldo dari wallet

Headers:

* X-User-Id

### **5.4 GET /api/wallet/{userId}/transactions** 

Deskripsi:  
Melihat riwayat transaksi wallet 

### 

## **6\) Endpoint Contract (Internal API)** 

### **6.1 POST /internal/wallet/hold**  

Deskripsi:  
Menahan saldo saat user melakukan bid 

Behavior:

* Mengurangi availableBalance  
* Menambah heldBalance  
* Idempotent berdasarkan referenceId

Error:

* 400 jika saldo tidak cukup  
* 409 jika duplicate referenceId

### **6.2 POST /internal/wallet/release** 

Deskripsi:  
Mengembalikan dana jika user kalah lelang 

Behavior:

* Mengurangi heldBalance  
* Menambah availableBalance

### **6.3 POST /internal/wallet/convert** 

Deskripsi:  
Mengubah held balance menjadi pembayaran saat user menang 

Behavior:

* Mengurangi heldBalance  
* Tidak dikembalikan ke availableBalance (final payment)

## **7\) Interaksi Antar Modul** 

1) Saat user bid:  
   Auction → POST /internal/wallet/hold  
2) Saat user kalah:  
   Auction → POST /internal/wallet/release  
3) Saat user menang:  
   Auction → POST /internal/wallet/convert

# **8\) Catatan Penting**

* Semua operasi wallet bersifat atomic (@Transactional)  
* Sistem menjamin tidak ada double processing (idempotency)  
* Wallet tidak mengetahui konteks bisnis (auction), hanya referenceId (untuk mudah, saya ganti auct\_id)

# \[BOOKING\] API Docs

# **Bidmart Booking API Contract**

Owner: bidmart-booking

| Versi | Pengarang | Tanggal | Notes |
| :---: | :---: | :---: | ----- |
| 1.0 | Rabiul | 2026-03-04 | First version |

## **1\) Tujuan**

Dokumen ini mendefinisikan kontrak API dan autentikasi untuk MVP Booking dan Notification.

## **2\) Auth Strategy (MVP)**

Untuk milestone ini gunakan header sementara:

* X-User-Id: \<user-id\>

Contoh: X-User-Id: usr-3001

Catatan:

* Ini sementara untuk MVP.  
* Target berikutnya migrasi ke JWT (Authorization: Bearer \<token\>).  
* Semua endpoint wajib auth.

## **3\) Common Rules**

1. Semua response format JSON.  
2. Ownership check wajib: user hanya boleh akses data miliknya.  
3. Timestamp response pakai ISO-8601 UTC.  
4. Notifikasi diurutkan terbaru ke lama.

## **4\) Error Response Standard**

Contoh:

{

  "code": "NOT\_FOUND",

  "message": "Booking not found"

}

Kode error yang dipakai:

* UNAUTHORIZED (401)  
* FORBIDDEN (403)  
* NOT\_FOUND (404)  
* BAD\_REQUEST (400)  
* INTERNAL\_ERROR (500)

## **5\) Endpoint Contract**

### **5.1 GET /api/bookings/me
**

Deskripsi: Ambil daftar booking milik user login.

Headers:

* X-User-Id (required)

Query params (opsional):

* page (default 0)  
* size (default 10)

Success 200:

\[

  {

    "id": "bkg-1001",

    "auctionId": "auc-1001",

    "listingId": "lst-5001",

    "buyerUserId": "usr-3001",

    "sellerUserId": "usr-2001",

    "status": "CREATED",

    "totalAmount": 1750000,

    "currency": "IDR",

    "createdAt": "2026-03-04T10:00:00Z"

  }

\]

### **5.2 GET /api/bookings/{id}**

Deskripsi: Ambil detail booking berdasarkan ID.

Headers:

* X-User-Id (required)

Path params:

* id (booking id)

Success 200:

\[  
  {  
    "id": "bkg-1001",  
    "auctionId": "auc-1001",  
    "listingId": "lst-5001",  
    "buyerUserId": "usr-3001",  
    "sellerUserId": "usr-2001",  
    "status": "CREATED",  
    "totalAmount": 1750000,  
    "currency": "IDR",  
    "createdAt": "2026-03-04T10:00:00Z"  
  }  
\]

Error:

* 403 jika bukan pemilik booking  
* 404 jika booking tidak ditemukan

### **5.3 GET /api/notifications/me**

Deskripsi: Ambil daftar notifikasi user login.

Headers:

* X-User-Id (required)

Query params (opsional):

* page (default 0)  
* size (default 20)  
* unreadOnly (default false)

Success 200:

\[  
  {  
    "id": "ntf-9001",  
    "type": "WIN",  
    "title": "You won the auction",  
    "message": "You won auction auc-1001 with final price IDR 1,750,000",  
    "isRead": false,  
    "createdAt": "2026-03-04T10:05:00Z"  
  }  
\]

### **5.4 PATCH /api/notifications/{id}/read**

Deskripsi: Tandai notifikasi sebagai sudah dibaca.

Headers:

* X-User-Id (required)

Path params:

* id (notification id)

Request body:

{  
  "read": true  
}

Success 200:

{

  "id": "ntf-9001",

  "type": "WIN",

  "title": "You won the auction",

  "message": "You won auction auc-1001 with final price IDR 1,750,000",

  "isRead": true,

  "createdAt": "2026-03-04T10:05:00Z",

  "readAt": "2026-03-04T10:10:00Z"

}

Error:

* 403 jika notifikasi bukan milik user  
* 404 jika notifikasi tidak ditemukan

## **6\) Mapping ke Event Contract**

1. Event WinnerDetermined membuat booking \+ notifikasi WIN.  
2. Jika loserUserIds tersedia, buat notifikasi LOSE untuk setiap user kalah.  
3. finalPrice event dipetakan ke totalAmount booking.

## **7\) Open Points**

1. Kapan migrasi auth dari X-User-Id ke JWT.  
2. Final format pagination.  
3. Enum final untuk status booking, shipment, dan notification type.

# \[AUCTION\] API Docs

# **Bidmart Auction API/Auth Contract**

Owner: bidmart-auction

| Versi | Pengarang | Tanggal | Notes |
| :---: | :---: | :---: | ----- |
| 1.0 | Keisha | 2026-03-05 | First version |
| 1.1 | Keisha | 2026-04-14 | Menambahkan endpoint baru (5.5 dan 5.6 untuk bidding) |
| 1.2 | Keisha | 2026-04-14 | Penyesuaian atribut (reservePrice, minimumIncrement, endTime, listingId), penghapusan description, pergantian bidderUsername menjadi bidderId. |

## 

## **1\) Tujuan**

Dokumen ini mendefinisikan kontrak API untuk MVP modul **Auction** (Lelang).

## **2\) Auth Strategy (MVP)**

Untuk milestone ini gunakan header sementara:

* X-User-Id: \<user-id\>

Contoh: X-User-Id: usr-3001

Catatan:

* Ini sementara untuk MVP.  
* Target berikutnya migrasi ke JWT (Authorization: Bearer \<token\>).  
* Semua endpoint yang bersifat menulis data (write) seperti POST dan PATCH wajib menyertakan auth header ini.

## **3\) Common Rules**

1. Semua format response adalah **JSON**.  
2. **Ownership Check**: Untuk aktivasi lelang, hanya pemilik (*seller*) yang diizinkan.  
3. Timestamp menggunakan format **ISO-8601**.

## **4\) Error Response Standard**

Contoh:

{

  "message": "Auction not found"

}

Kode error yang dipakai:

* FORBIDDEN (403)  
* NOT\_FOUND (404)  
* BAD\_REQUEST (400)  
* SUCCESS (200 / 201\)

## **5\) Endpoint Contract**

### **5.1 GET /api/auctions**

Deskripsi: Ambil semua daftar lelang yang tersedia.

Success 200:

\[

  {

    "id": "7b2a9c11-e1f4-4d2a-a9e9-1234567890ab",

    "title": "Mechanical Keyboard Custom",

    "startingPrice": 1200000,

    "reservePrice": 1500000,

    "minimumIncrement": 50000,

    "currentPrice": 1200000,

    "status": "ACTIVE",

    "endTime": "2026-03-06T12:00:00Z",

    "listingId": "lst-9901",

    "sellerId": "usr-2406",

    "createdAt": "2026-03-05T12:00:00Z",

    "updatedAt": "2026-03-05T12:05:00Z"

  }

\]

### **5.2 GET /api/auctions/{id}**

Deskripsi: Ambil detail lelang berdasarkan ID.

Path params:

* id (auction ID)

Success 200:

{  
  "id": "7b2a9c11-e1f4-4d2a-a9e9-1234567890ab",  
  "title": "Mechanical Keyboard Custom",  
  "startingPrice": 1200000,  
  "reservePrice": 1500000,  
  "minimumIncrement": 50000,  
  "currentPrice": 1200000,  
  "status": "ACTIVE",  
  "endTime": "2026-03-06T12:00:00Z",  
  "listingId": "290f1ee-6c54-4b01-90e6-d701748f0851",  
  "sellerId": "usr-2406",  
  "createdAt": "2026-03-05T12:00:00Z",  
  "updatedAt": "2026-03-05T12:05:00Z"  
}

Error:

* 404 jika lelang tidak ditemukan

### **5.3 POST /api/auctions**

Deskripsi: Membuat draft lelang baru..

Headers:

* X-User-Id (required)

Request Body:

{

  "listingId": "290f1ee-6c54-4b01-90e6-d701748f0851",

  "title": "Mechanical Keyboard Custom",

  "startingPrice": 1200000,

  "reservePrice": 1500000,

  "minimumIncrement": 50000,

  "endTime": "2026-03-06T12:00:00Z"

}

Success 201:

{  
  "id": "7b2a9c11-e1f4-4d2a-a9e9-1234567890ab",  
  "title": "Mechanical Keyboard Custom",  
  "startingPrice": 1200000,  
  "reservePrice": 1500000,  
  "minimumIncrement": 50000,  
  "currentPrice": 1200000,  
  "status": "DRAFT",  
  "endTime": "2026-03-06T12:00:00Z",  
  "listingId": "290f1ee-6c54-4b01-90e6-d701748f0851",  
  "sellerId": "usr-2406",  
  "createdAt": "2026-03-05T12:00:00Z",  
  "updatedAt": "2026-03-05T12:00:00Z"  
}

### **Penjelasan Field:**

* ### id: UUID atau String unik yang dihasilkan sistem untuk mengidentifikasi lelang.

* ### currentPrice: Nilai ini awalnya akan sama dengan startingPrice karena belum ada bid yang masuk.

* ### status: Lelang yang baru dibuat akan berstatus DRAFT sebelum nantinya diaktifkan melalui endpoint /activate.

* ### sellerId: Diambil dari header X-User-Id yang dikirim saat melakukan request.

* listingId: Diambil sebagai rujukan barang pada modul Katalog.

### **5.4 PATCH /api/auctions/{id}/activate**

Deskripsi: Mengubah status lelang dari DRAFT menjadi ACTIVE.

Headers:

* X-User-Id (required, must be the owner)

Success 200:

{

  "id": "7b2a9c11-e1f4-4d2a-a9e9-1234567890ab",

  "title": "Mechanical Keyboard Custom",

  "startingPrice": 1200000,

  "reservePrice": 1500000,

  "minimumIncrement": 50000,

  "currentPrice": 1200000,

  "status": "ACTIVE",

  "endTime": "2026-03-06T12:00:00Z",

  "listingId": "290f1ee-6c54-4b01-90e6-d701748f0851",

  "sellerId": "usr-2406",

  "createdAt": "2026-03-05T12:00:00Z",

  "updatedAt": "2026-03-05T12:05:00Z"

}

Error:

* 403 jika lelang bukan milik user  
* 400 jika lelang bukan dalam status DRAFT

### **5.5 GET /api/auctions/{id}/bids**

Deskripsi: Ambil riwayat seluruh penawaran yang sudah masuk untuk lelang spesifik, diurutkan dari nilai penawaran tetinggi (Highest Bid) hingga ke yang terendah.

Path params:

* id (auction ID, contoh: 7b2a9c11-e1f4-4d2a-a9e9-1234567890ab)

Success 200:

\[    
  {    
    "id": "9f2a8c11-8ee2-4c4f-b6a1-9876543210ab",    
    "auctionId": "7b2a9c11-e1f4-4d2a-a9e9-1234567890ab",    
    "bidderId": "usr-3001",    
    "amount": 1500000,    
    "createdAt": "2026-03-05T12:45:00Z"    
  },  
  {    
    "id": "4e1b7d00-a1c2-43bb-a5a6-1122334455cd",    
    "auctionId": "7b2a9c11-e1f4-4d2a-a9e9-1234567890ab",    
    "bidderId": "usr-2005",    
    "amount": 1200000,    
    "createdAt": "2026-03-05T12:30:00Z"    
  }  
\]

Error:

* 404 jika lelang tidak ditemukan

### **5.6 POST /api/auctions/{id}/bids**

Deskripsi: Mengajukan penawaran baru (bid) pada sebuah lelang. Sistem memanggil Wallet API secara sinkron untuk memvalidasi dan menahan saldo pengguna. Sistem akan memperpanjang waktu berakhir sebesar 2 menit jika penawaran masuk pada saat-saat terakhir (Anti-Sniping).

Headers:

* X-User-Id (required, ID buyer)

Path params:

* id (auction ID, contoh: 7b2a9c11-e1f4-4d2a-a9e9-1234567890ab)

Request Body:

{  

  "amount": 1500000  

}

Success 201:

{  

  "id": "9f2a8c11-8ee2-4c4f-b6a1-9876543210ab",  

  "auctionId": "7b2a9c11-e1f4-4d2a-a9e9-1234567890ab",  

  "bidderId": "usr-3001",  

  "amount": 1500000,  

  "createdAt": "2026-03-05T12:45:00Z"  

}

Error:

* 404 jika lelang tidak ditemukan.  
* 400 jika lelang tidak dalam status ACTIVE atau EXTENDED.  
* 400 jika nominal penawaran gagal melampaui currentPrice \+ minimumIncrement.  
* 403 / 500 (tergantung response Wallet) jika saldo pengguna tidak cukup / API Wallet Error.

## **6\) Mapping & Logic**

1. Lifecycle: Lelang dimulai dengan status DRAFT dan tidak bisa di-bid sebelum di-activate.  
2. Validation: Jumlah bid harus lebih tinggi dari currentPrice atau startingPrice.  
3. User Handling: Seller ID diambil dari header X-User-Id saat pembuatan lelang.

## **7\) Open Points**

1. Kapan migrasi auth dari X-User-Id ke JWT.  
2. 

# \[CATALOG\] API Docs

# **Bidmart Catalog API/Auth Contract**

Owner: bidmart-catalog

| Versi | Pengarang | Tanggal | Notes |
| :---: | :---: | :---: | ----- |
| 1.0 | Sean | 2026-03-05 | First version |

## **1\) Tujuan**

Dokumen ini mendefinisikan kontrak API internal (antar-microservice) dan eksternal untuk MVP Catalog, Listing Management, dan integrasi Media (Gambar).

## **2\) Auth Strategy (MVP)**

Untuk milestone ini, gunakan header sementara:  
X-User-Id: \<user-id\>  
Contoh: X-User-Id: usr-2001

Catatan:  
\- Target berikutnya migrasi ke JWT (Authorization: Bearer \<token\>).  
\- Endpoint berjenis GET (Katalog Publik) bersifat terbuka (tidak wajib auth).  
\- Endpoint mutasi data (POST, PATCH) wajib menyertakan header auth dan dilakukan pengecekan peran (Hanya SELLER yang dapat membuat listing).

## **3\) Common Rules**

1\. Semua response menggunakan format JSON (kecuali endpoint antarmuka Thymeleaf).  
2\. Ownership Check: Penjual hanya boleh memodifikasi/mempublikasi listing miliknya sendiri.  
3\. Timestamp: Menggunakan standar ISO-8601 UTC.  
4\. Paginasi: Daftar listing dikembalikan dalam format Page dari Spring Data (memiliki content, totalPages, totalElements).  
5\. Media: URL gambar yang dikembalikan berupa path relatif atau absolut yang merujuk pada direktori statis /uploads/.

## **4\) Error Response Standard**

Contoh JSON Error:  
{  
  "code": "NOT\_FOUND",  
  "message": "Listing with ID 1st-5001 not found"  
}

Kode HTTP yang digunakan:  
\- 400 (BAD\_REQUEST): Validasi gagal (misal: harga bid baru lebih rendah dari harga saat ini).  
\- 401 (UNAUTHORIZED): Tidak ada header kredensial.  
\- 403 (FORBIDDEN): Akses ditolak (bukan pemilik listing atau bukan seller).  
\- 404 (NOT\_FOUND): Data listing atau category tidak ditemukan.  
\- 500 (INTERNAL\_ERROR): Kesalahan server atau gagal menyimpan file gambar.

\-------------------------------------------------------------------

**5\) Endpoint Contract (REST API)**

**5.1 GET /api/listings**  
Deskripsi: Mengambil daftar listing lelang yang berstatus ACTIVE (untuk tampilan katalog publik). Menggunakan Batch Fetching untuk optimasi query.  
Headers: Tidak wajib  
Query params (opsional):  
  \- page (default: 0\)  
  \- size (default: 10\)

Success 200:  
{

  "content": \[

    {

      "id": "123e4567-e89b-12d3-a456-426614174000",

      "title": "MacBook Pro M2 2023",

      "category": {

        "id": "cat-001",

        "name": "Elektronik"

      },

      "sellerId": "dummy-seller-id-123",

      "currentPrice": 15000000.0,

      "endTime": "2026-03-10T10:00:00Z",

      "status": "ACTIVE",

      "images": \[

        {

          "id": "img-111",

          "imageUrl": "/uploads/uuid\_filename1.jpg",

          "isPrimary": true

        }

      \]

    }

  \],

  "pageable": {

    "pageNumber": 0,

    "pageSize": 10

  },

  "totalElements": 1,

  "totalPages": 1

}

**5.2 GET /api/listings/{id}**  
Deskripsi: Mengambil detail listing secara spesifik. Digunakan oleh Modul Lelang untuk memvalidasi ketersediaan barang sebelum menerima bid.  
Headers: Tidak wajib  
Path params: id (UUID dari listing)

Success 200:  
{

  "id": "123e4567-e89b-12d3-a456-426614174000",

  "title": "MacBook Pro M2 2023",

  "description": "Kondisi mulus 99%, garansi resmi.",

  "category": {

    "id": "cat-001",

    "name": "Elektronik"

  },

  "sellerId": "dummy-seller-id-123",

  "startingPrice": 15000000.0,

  "currentPrice": 15000000.0,

  "reservePrice": 16000000.0,

  "endTime": "2026-03-10T10:00:00Z",

  "status": "ACTIVE",

  "createdAt": "2026-03-05T10:00:00Z",

  "images": \[

    {

      "id": "img-111",

      "imageUrl": "/uploads/uuid\_filename1.jpg",

      "isPrimary": true

    },

    {

      "id": "img-222",

      "imageUrl": "/uploads/uuid\_filename2.jpg",

      "isPrimary": false

    }

  \]

}

**5.3 PATCH /api/listings/{id}/current-price**  
Deskripsi: Memperbarui harga terkini (currentPrice). Dipanggil secara sinkronus oleh Modul Lelang ketika ada bid baru yang dinyatakan sah.  
Headers: X-User-Id (Internal Service Auth)  
Path params: id (UUID dari listing)

![][image1]

Success 200: Mengembalikan JSON objek detail listing yang harganya telah diperbarui.  
Error:  
\- 400 BAD\_REQUEST jika newPrice \<= currentPrice.

**5.4 GET /api/categories**  
Deskripsi: Mengambil daftar seluruh kategori yang tersedia. Digunakan untuk mengisi opsi dropdown di frontend.  
Headers: Tidak wajib

Success 200:  
![][image2]

\-------------------------------------------------------------------

**6\) Endpoint Contract (Web/UI Forms \- Thymeleaf)**  
(Catatan: Endpoint ini mengembalikan HTML, bukan JSON. Disediakan selama masa MVP UI rendering).

\- GET /listings/create: Menampilkan form pembuatan lelang beserta integrasi pratinjau JavaScript.  
\- POST /listings/create: Menerima data lelang baru. Mendukung payload multipart/form-data untuk unggah gambar ganda. Mengembalikan redirect 302\.  
\- POST /listings/{id}/publish: Mengubah status dari DRAFT menjadi ACTIVE.

**7\) Mapping ke Event Contract & Interaksi Antarmodul**

*1\. Pengecekan Status:*   
Modul Lelang wajib memanggil GET /api/listings/{id} untuk memastikan status \== 'ACTIVE' dan waktu saat ini belum melewati endTime sebelum menerima bid.

*2\. Pembaruan Harga:*   
Jika bid berhasil memotong saldo di Modul Dompet (Wallet), Modul Lelang memanggil PATCH /api/listings/{id}/current-price.

*3\. Penutupan Lelang: (Rencana ke depan)*   
Ketika scheduler di Modul Lelang mendeteksi waktu habis, Modul Lelang akan menerbitkan Event (via Kafka/RabbitMQ) yang akan ditangkap oleh Modul Catalog untuk mengubah status listing menjadi CLOSED.

\#masih on progress

gk jago adpro 🙁

sedang menunggu hasil gemini...

![][image3]

# \[AUTH\] API Docs

# **Bidmart Authentication API Contract**

Owner: bidmart-authentication

| Versi | Pengarang | Tanggal | Notes |
| :---: | :---: | :---: | ----- |
| 1.0 | Azka | 2026-03-06 | First version |

## **1\) Tujuan**

Dokumen ini mendefinisikan kontrak API untuk service Authentication BidMart. Service ini bertanggung jawab untuk registrasi user, verifikasi email, login, dan pengambilan info user terautentikasi.

## **2\) Auth Strategy (MVP)**

Semua endpoint di luar /api/auth/\*\* menggunakan JWT Bearer Token:

- Authorization: Bearer \<access\_token\>

Token diperoleh dari endpoint POST /api/auth/login. Token bersifat stateless (tidak ada session).

Catatan:

- /api/auth/register, /api/auth/verify-email, /api/auth/login adalah public (tidak perlu token).  
- GET /api/users/me wajib menyertakan Bearer Token.


## **3\) Common Rules**

1. Semua response format JSON.  
2. Password di-hash dengan BCrypt — tidak pernah dikembalikan dalam response.  
3. Timestamp response pakai ISO-8601 UTC.  
4. Email bersifat unik per user; registrasi dengan email yang sama akan ditolak (409).

## **4\) Error Response Standard**

Contoh:

{

  "error": "Token is invalid or has expired"

}

Kode error yang dipakai:

* CONFLICT (409) — email sudah terdaftar  
* BAD\_REQUEST (400) — token verifikasi tidak valid  
* GONE (410) — token verifikasi sudah kadaluarsa  
* UNAUTHORIZED (401) — kredensial salah / token JWT tidak valid  
* FORBIDDEN (403) — akun belum diverifikasi

## **5\) Endpoint Contract**

### **5.1 POST /api/auth/register**

Deskripsi: Mendaftarkan user baru. Token verifikasi email dikembalikan di body (untuk dev/test; di production dikirim via email).

Headers: (tidak diperlukan)

Request body:

{

  "email": "user@example.com",

  "username": "johndoe",

  "password": "secretpass"

}

### Validasi:

* ### email wajib dan harus format email valid

* ### username wajib, 3–50 karakter

* ### password wajib, minimal 8 karakter

### Success 201:

{

  "message": "Registration successful. Please verify your email.",

  "verificationToken": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"

}

### Error:

* ### 409 jika email sudah terdaftar

* ### 400 jika validasi gagal

### 

### **5.2 POST /api/auth/verify-email**

Deskripsi: Memverifikasi email user menggunakan token yang diterima saat registrasi.

Headers: (tidak diperlukan)

Query params:

* token (required) — verification token UUID

Contoh: POST /api/auth/verify-email?token=xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx

Success 200:

{  
  "message": "Email verified successfully"  
}  
Error:

* 400 jika token tidak valid / tidak ditemukan  
* 410 jika token sudah kadaluarsa

### **5.3 POST /api/auth/login**

Deskripsi: Login user dan mendapatkan JWT access token.

Headers: (tidak diperlukan)

Request body:

{  
  "email": "user@example.com",  
  "password": "secretpass"  
}

Success 200:

{  
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",  
  "tokenType": "Bearer"  
}

Error:

* 401 jika email/password salah  
* 403 jika akun belum diverifikasi (email belum dikonfirmasi)

### **5.4 GET /api/users/me**

Deskripsi: Mengambil informasi user yang sedang login berdasarkan JWT token.

Headers:

* Authorization: Bearer \<access\_token\> (required)

Success 200:

{  
  "id": "usr-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",  
  "email": "user@example.com",  
  "username": "johndoe",  
  "role": "BUYER",  
  "enabled": true  
}

Error:

* 401 jika token tidak disertakan atau tidak valid

## **6\) Open Points**

1. Role selain BUYER kapan dan bagaimana user bisa menjadi SELLER?  
2. Token expiry berapa lama JWT berlaku? Apakah ada refresh token?  
3. Migrasi email verifikasi dari response body ke pengiriman email asli di production.

# Event Contract

# **Bidmart Event Contract \- General Guidelines**

| Versi | Pengarang | Tanggal | Notes |
| :---: | :---: | :---: | ----- |
| 1.0 | Rabiul | 2026-03-04 | First version |

## **1\) Tujuan**

Dokumen ini mendefinisikan format umum, aturan pemrosesan, dan struktur dasar (*envelope*) dari event yang dikirimkan agar integrasi stabil dan tidak ambigu.

## **2\) Event Envelope (Wajib untuk semua event)**

| Field | Type | Required | Example | Notes |
| :---- | :---- | :---- | :---- | :---- |
| eventId | string (UUID) | Yes | evt-4f4d5f8a-6a2d-4c26-9f78-8f93c31f47bc | Unik per event, dipakai untuk idempotency |
| eventType | string | Yes | WinnerDetermined | Berisi nama event yang dikirim (Enum: WinnerDetermined, AuctionClosed) |
| eventVersion | integer | Yes | 1 | Versi kontrak event |
| occurredAt | string (ISO-8601 UTC) | Yes | 2026-03-04T09:20:00Z | Waktu event terjadi |
| source | string | Yes | bidmart-auction | Service pengirim |
| payload | object | Yes | {...} | Isi bisnis event |

Contoh *envelope* dasar:

{  
  "eventId": "evt-4f4d5f8a-6a2d-4c26-9f78-8f93c31f47bc",  
  "eventType": "TIPE\_EVENT",  
  "eventVersion": 1,  
  "occurredAt": "2026-03-04T09:20:00Z",  
  "source": "bidmart-auction",  
  "payload": {}  
}

## **3\) Processing Rules Umum** 

1. *Idempotency* wajib berdasarkan eventId.  
2. Event dengan field wajib tidak lengkap harus di-reject dan di-log.  
3. Timestamp wajib menggunakan format UTC ISO-8601.

## **4\) Versioning Rules**

1. Penambahan field non-wajib \= *backward-compatible*.  
2. Rename/hapus field wajib \= *breaking change*, naikkan eventVersion.  
3. Consumer harus mengabaikan field yang tidak dikenal (*unknown fields*).

## **5\) Open Points Umum**

1. 

# WinnerDetermined

# **Bidmart Event Spec \- WinnerDetermined**

| Versi | Pengarang | Tanggal | Notes |
| :---: | :---: | :---: | ----- |
| 1.0 | Rabiul | 2026-03-04 | First version |
| 1.1 | Keisha | 2026-05-04 | Menambahkan consumer baru (bidmart-wallet) |

Event Type: WinnerDetermined

Producer: bidmart-auction

Consumer: bidmart-booking, bidmart-wallet

## **1\) Deskripsi & Aturan Bisnis**

Event ini dipublikasikan ketika sebuah siklus lelang selesai dan berhasil mendapatkan pemenang. 

Pada modul **booking**, event ini akan memicu:

1. Pembuatan pesanan (*booking*) otomatis.  
2. Pengiriman notifikasi WIN untuk winnerUserId.  
3. Pengiriman notifikasi LOSE untuk semua loserUserIds (jika tersedia).

Pada modul **wallet**, event ini akan memicu:

1. Perubahan status saldo "hold" pada winnerUserId menjadi "paid/convert"

## **2\) Payload Schema**

| Field | Type | Required | Example | Notes |
| :---- | :---- | :---- | :---- | :---- |
| auctionId | string | Yes | auc-1001 | ID lelang |
| listingId | string | Yes | lst-5001 | ID listing/catalog |
| sellerUserId | string | Yes | usr-2001 | User penjual |
| winnerUserId | string | Yes | usr-3001 | User pemenang |
| finalPrice | integer (int64) | Yes | 1750000 | Harga akhir dalam rupiah (tanpa desimal) |
| currency | string | Yes | IDR | Default IDR |
| itemName | string | Yes | Mechanical Keyboard | Nama item |
| quantity | integer (int32) | Yes | 1 | Minimum 1 |
| loserUserIds | array\<string\> | No | \["usr-3002","usr-3003"\] | Untuk notif kalah |

## 

## **3\) Contoh Event**

## **{**

##   **"eventId": "evt-4f4d5f8a-6a2d-4c26-9f78-8f93c31f47bc",**

##   **"eventType": "WinnerDetermined",**

##   **"eventVersion": 1,**

##   **"occurredAt": "2026-03-04T09:20:00Z",**

##   **"source": "bidmart-auction",**

##   **"payload": {**

##     **"auctionId": "auc-1001",**

##     **"listingId": "lst-5001",**

##     **"sellerUserId": "usr-2001",**

##     **"winnerUserId": "usr-3001",**

##     **"finalPrice": 1750000,**

##     **"currency": "IDR",**

##     **"itemName": "Mechanical Keyboard",**

##     **"quantity": 1,**

##     **"loserUserIds": \[**

##       **"usr-3002",**

##       **"usr-3003"**

##     **\]**

##   **}**

## **}**

## **4\) Open Points Khusus Event Ini**

1. Memastikan loserUserIds dapat ditarik (*query*) dan disediakan oleh modul Auction sebelum event di-*publish*.  
2. Broker/topic final yang dipakai (misalnya: auction.events).  
3. Jaminan *publish* event setelah transaksi database di modul auction *commit*.

# BidPlaced

# **Bidmart Event Spec \- BidPlaced**

| Versi | Pengarang | Tanggal | Notes |
| :---: | :---: | :---: | ----- |
| 1.0 | Keisha | 2026-04-14 | First version |
| 1.1 | Keisha | 2026-05-04 | Menambahkan consumer baru (bidmart-wallet) |

Event Type: BidPlaced

Producer: bidmart-auction

Consumer: bidmart-catalog, bidmart-booking, bidmart-wallet

## **1\) Deskripsi & Aturan Bisnis**

*Event* ini dipublikasikan setelah sebuah penawaran (*bid*) baru berhasil divalidasi dan disimpan di modul Auction. Alur ini memicu aksi pada sisi konsumen:

1. **Modul catalog**: Memperbarui kolom currentPrice dan menambah bidCount pada entitas Listing agar sinkron dengan penawaran terbaru.  
2. **Modul notifikasi/booking**: Mengirim pesan *real-time* kepada previousBidderUserId yang harga tawarnya baru saja terlampaui, mengirim pemberitahuan kepada seluruh partisipan bahwa ada harga baru pada lelang yang mereka ikuti. (Catatan: Jika belum ada orang yang menawar, nilai previousBidderUserId akan otomatis diatur menjadi null dan modul notifikasi/booking cukup mengabaikan pesan tersebut.)  
3. **Modul wallet**: Melepas/release saldo orang yang baru saja tersalip (previousBidderUserId).

## **2\) Payload Schema**

| Field | Type | Required | Example | Notes |
| :---- | :---- | :---- | :---- | :---- |
| bidId | string (uuid) | Yes | 9f2a8c11-8ee2-4c4f-b6a1-9876543210ab | ID unik penawaran terbaru |
| auctionId | string (uuid) | Yes | 7b2a9c11-e1f4-4d2a-a9e9-1234567890ab | ID lelang |
| listingId | string (uuid) | Yes | d290f1ee-6c54-4b01-90e6-d701748f0851 | ID barang rujukan di modul Katalog |
| sellerUserId | string (uuid) | Yes | e1a4d8c2-3f7b-4d9a-8e2c-5b6f1a4d8c2e | ID penjual |
| bidderUserId | string (uuid) | Yes | e1a4d8c2-3f7b-4d9a-8e2c-5b6f1a4d8c2e | ID penawar tertinggi saat ini |
| previousBidderUserId | string (uuid) | No (Nullable) | c5b19d42-8e3f-4a7c-9d1b-2f6e4a8c5b19 | ID penawar yang posisinya baru tergeser. Bernilai null jika ini adalah *bid* pertama. |
| bidAmount | integer (int64) | Yes | 1600000 | Harga penawaran baru dalam Rupiah |
| itemName | string | Yes | "Mechanical Keyboard" | Nama item (dari judul lelang) |

## **3\) Contoh Event**

## **{**

##   **"eventId": "f3b19d42-8e3f-4a7c-9d1b-2f6e4a8c5b19",**

##   **"eventType": "BidPlaced",**

##   **"eventVersion": 1,**

##   **"occurredAt": "2026-04-14T13:25:00Z",**

##   **"source": "bidmart-auction",**

##   **"payload": {**

##     **"bidId": "9f2a8c11-8ee2-4c4f-b6a1-9876543210ab",**

##     **"auctionId": "7b2a9c11-e1f4-4d2a-a9e9-1234567890ab",**

##     **"listingId": "d290f1ee-6c54-4b01-90e6-d701748f0851",**

##     **"sellerUserId": "e1a4d8c2-3f7b-4d9a-8e2c-5b6f1a4d8c2e",**

##     **"bidderUserId": "e1a4d8c2-3f7b-4d9a-8e2c-5b6f1a4d8c2e",**

##     **"previousBidderUserId": "c5b19d42-8e3f-4a7c-9d1b-2f6e4a8c5b19",**

##     **"bidAmount": 1600000,**

##     **"itemName": "Mechanical Keyboard Custom"**

##   **}**

## **}**

## **4\) Open Points Khusus Event Ini**

1. Memastikan modul catalog menangani urutan pesan (*out-of-order events*) dengan mengecek occurredAt atau amount, agar harga tidak tertimpa oleh pesan lama yang terlambat.  
2. Memastikan modul notifikasi/booking terdapat tabel di *database* untuk mencatat peserta yang mem-*follow* atau menawar di sebuah lelang. Modul auction tidak mengirimkan daftar lengkap partisipan di dalam payload *event* ini.  
3. Kesepakatan nama broker/topic dan *routing key* final yang dipakai (misalnya: bid.placed)

# AuctionClosed

# **Bidmart Event Spec \- AuctionClosed**

| Versi | Pengarang | Tanggal | Notes |
| :---: | :---: | :---: | ----- |
| 1.0 | Keisha | 2026-05-03 | First version |

Event Type: AuctionClosed

Producer: bidmart-auction

Consumer: bidmart-catalog, bidmart-booking, bidmart-wallet

## **1\) Deskripsi & Aturan Bisnis**

Event ini dipublikasikan ketika sebuah siklus lelang berakhir tanpa pemenang (status \= UNSOLD).Kondisi ini terjadi apabila:

1. Tidak ada penawaran (bid) yang masuk sama sekali, atau  
2. Seluruh penawaran yang masuk tidak memenuhi harga cadangan (reserve\_price).  
   Catatan penting: Event ini tidak dipublikasikan saat lelang berhasil menemukan pemenang. Untuk kasus tersebut, gunakan event WinnerDetermined. Setiap penutupan lelang dijamin hanya menghasilkan satu event, baik WinnerDetermined (WON) maupun AuctionClosed (UNSOLD).

Pada sisi consumer, event ini akan memicu:

1. **bidmart-booking**: Pencatatan (log) penutupan lelang dan pengiriman notifikasi kepada seluruh peserta bahwa lelang berakhir tanpa pemenang.  
2. **bidmart-catalog:** Pembaruan status listing kembali menjadi tersedia (tidak terjual).  
3. **bidmart-wallet**: Pelepasan (release) dana yang ditahan untuk seluruh peserta lelang (allBidderIds).

## **2\) Payload Schema**

| Field | Type | Required | Example | Notes |
| :---- | :---- | :---- | :---- | :---- |
| auctionId | string (uuid) | Yes | 7b2a9c11-e1f4-4d2a-a9e9-1234567890ab | ID lelang |
| listingId | string (uuid) | Yes | d290f1ee-6c54-4b01-90e6-d701748f0851 | ID barang rujukan di modul Katalog |
| sellerUserId | string (uuid) | Yes | e1a4d8c2-3f7b-4d9a-8e2c-5b6f1a4d8c2e | ID penjual |
| closedAt | string (ISO-8601 UTC) | Yes | 2026-05-03T09:20:00Z | 2026-05-03T09:20:00Z |
| allBidderIds | 	array\<string\> | No | \["usr-3002","usr-3003"\] | Seluruh bidder yang pernah bid, digunakan wallet untuk release hold. Kosong jika tidak ada bid sama sekali. |

## **3\) Contoh Event**

## {

##   "eventId": "evt-9c3e2a1b-7f4d-4b88-ae12-3c47f920d5e1",

##   "eventType": "AuctionClosed",

##   "eventVersion": 1,

##   "occurredAt": "2026-05-03T09:20:00Z",

##   "source": "bidmart-auction",

##   "payload": {

##     "auctionId": "auc-1001",

##     "listingId": "lst-5001",

##     "sellerUserId": "usr-2001",

##     "closedAt": "2026-05-03T09:20:00Z",

##     "allBidderIds": \[

##       "usr-3002",

##       "usr-3003"

##     \]

##   }

## **}**

## **4\) Open Points Khusus Event Ini**

1. allBidderIds harus di-query dari tabel bids (semua bidder\_id distinct) sebelum event di-publish. Pastikan query dilakukan dalam transaksi yang sama dengan perubahan status auction.  
2. Broker/topic final yang dipakai (misalnya: auction.events dengan routing key auction.event.auction-closed).  
3. Jaminan publish event setelah transaksi database di modul auction commit (transactional outbox pattern atau post-commit hook).  
4. Consumer bidmart-wallet perlu memastikan idempotency, release tidak boleh dieksekusi lebih dari sekali per auctionId.

# Rubrik Penilaian

1. ## **Rubrik Penilaian**

     
   **Penilaian Progress \- Proyek Akhir (15%, dinilai oleh asdos)**

   Ada 5 periode penilaian: Persiapan, Kemajuan 25%, Kemajuan 50%, Kemajuan 75%, dan Final (100%).

| Kelompok Nilai | Persentase |
| :---- | :---- |
| Individu | 50% |
| Kelompok | 50% (akan menjadi 0 jika komponen individu bernilai 0\) |

	

	**Rubrik Penilaian Progress \- Individu**

| Skala | Deskripsi |
| :---: | ----- |
| 0 | Tidak ada implementasi. |
| 1 | \<= 2 commit push selama periode penilaian progress. |
| 2 | \>2 commit push selama periode penilaian progress, namun belum ada pekerjaan yang di-*merge*. |
| 3 | Kuantitas pekerjaan kurang sesuai dengan task yang diberikan per minggu, dan pekerjaan sudah di-*merge*. |
| 4 | Kuantitas pekerjaan sesuai dengan task yang diberikan per minggu, dan pekerjaan sudah di-*merge*. |

	**Rubrik Penilaian Progress \- Kelompok**

| Skala | Deskripsi |
| :---: | ----- |
| 0 | Tidak ada implementasi. |
| 1 | Kelompok berhasil menyelesaikan \< 50% dari target *milestone* yang dinilai. |
| 2 | Kelompok berhasil menyelesaikan dan mengintegrasikan \>= 50% dari target *milestone* yang dinilai. |
| 3 | Kelompok berhasil menyelesaikan dan mengintegrasikan \>= 75% dari target *milestone* yang dinilai. |
| 4 | Kelompok berhasil menyelesaikan dan mengintegrasikan 100% dari target *milestone* yang dinilai. |

**Catatan:** Definisi “integrasi” adalah:

- Fungsionalitas sudah berjalan baik,  
- Data bisa mengalir dengan baik antar fitur,  
- *Frontend* sudah bisa digunakan dan terhubung dengan *backend*, dan  
- Standar penerapan kualitas perangkat lunak terpenuhi (*unit testing*, *clean code*, *secure coding*).

**Rubrik Penilaian Progress khusus Final \- Kelompok**

| Skala | Deskripsi |
| :---: | ----- |
| 0 | Tidak ada implementasi. |
| 1 | Kelompok berhasil menyelesaikan \< 100% dari target *milestone* yang dinilai. |
| 2 | Kelompok berhasil menyelesaikan dan mengintegrasikan seluruh fitur pada target *milestone* yang dinilai, dan memenuhi semua kriteria berikut: Ada implementasi CI/CD yang berjalan dengan baik (sudah *green* dan berhasil *deploy*). Sudah menggunakan *tools* untuk pengujian kualitas kode. *Report* pada *tools* bisa diakses asdos dan *report* sudah berhasil menampilkan data kualitas kode. *Unit testing* dan *functional testing* sudah dilakukan untuk semua fitur. *Functional testing* menggunakan Serenity atau *tools* sejenis. *Report artifacts* bisa diakses secara publik. |
| 3 | Memenuhi semua kriteria berikut: Ada implementasi CI/CD yang berjalan dengan baik (sudah *green* dan berhasil *deploy*). Sudah menggunakan *tools* untuk pengujian kualitas kode. *Report* pada *tools* bisa diakses asdos dan *report* sudah berhasil menampilkan data kualitas kode dengan nilai \>= 90%. *Unit testing* dan *functional testing* sudah dilakukan untuk semua fitur. *Functional testing* menggunakan Serenity atau *tools* sejenis. *Report artifacts* bisa diakses secara publik, memenuhi *coverage* \>= 90%. |
| 4 | Memenuhi semua kriteria berikut: Ada perbandingan *before* dan *after commit* yang menunjukkan peningkatan performa berdasarkan semua hasil pengujian berikut: *Profiling* kode, Metrik APDEX, *Performance testing* Lighthouse, dan Clarity untuk *usability testing*. *Monitoring* sudah berjalan dengan baik, berhasil menampilkan *observability* performa aplikasi dan performa *database*. |

**Penilaian Akhir \- Proyek Akhir (15%, dinilai oleh dosen)**

| Kelompok Nilai | Komponen | Persentase |
| :---- | :---- | :---- |
| **Individu** | Pemahaman Materi Perkuliahan | 30% |
|  | Kontribusi Pemahaman terhadap Pengerjaan Tugas | 30% |
| **Kelompok** (nilai otomatis menjadi 0 untuk individu yang tidak berkontribusi sama sekali) | *Software Design (SOLID Principles, Maintainability, Design Patterns)* | 10% |
|  | *Software Quality (Clean Code, Secure Coding, Testing, Profiling)* | 10% |
|  | *Software Architecture (Architecture, Concurrency, Asynchronous, High-Level Networking)* | 10% |
|  | *Software Deployment (CI/CD, Deployment Strategies, Containerization, Monitoring, Infrastructure as Code)* | 10% |

**Rubrik Penilaian Akhir \- Individu**

1. **Pemahaman Materi Perkuliahan**

| Skala | Deskripsi |
| :---: | ----- |
| 0 | Tidak bisa menjawab pertanyaan apapun. |
| 1 | Memahami sebagian (minimal 50%) definisi standar sesuai dengan *Learning Objective* untuk 2 modul yang dipilih secara *random*. |
| 2 | Memahami seluruh (100%) definisi standar sesuai dengan *Learning Objective* untuk 2 modul yang dipilih secara *random*. |
| 3 | Memahami penerapan yang tepat untuk seluruh (100%) *Learning Objective* untuk 2 modul yang dipilih secara *random*, terhadap proyek akhir kelompok. |
| 4 | Mampu menganalisis/mengevaluasi secara mendalam terhadap penerapan materi *Learning Objective* untuk suatu modul terhadap proyek akhir kelompok, untuk mendapatkan hasil yang terbaik. Mampu menjawab pertanyaan/*challenge* dengan lancar dan memiliki kemampuan komunikasi yang sangat baik. Dosen akan menilai secara subjektif berdasarkan pemaparan, kualitas kerja lebih, dan kreatifitas dari mahasiswa. |

2. **Kontribusi terhadap Pengerjaan Tugas**

| Skala | Deskripsi |
| :---: | ----- |
| 0 | Tidak ada implementasi yang fungsional (bermakna sesuai fitur). |
| 1 | Semua implementasi secara individu tidak bisa digunakan (*error*, tidak bisa di-*deploy*, tidak di-*merge*, tidak terintegrasi *front-end* dan *back-end* untuk fitur yang sama, dst.) |
| 2 | Ada (minimal 1 fitur) implementasi secara individu sudah bisa digunakan, namun belum terintegrasi dengan kelompok atau ada *minor error* (*error* yang tidak membuat aplikasinya *crash*) ketika digunakan. Implementasi hanya berjalan di *local*. |
| 3 | Semua implementasi secara individu yang sudah terintegrasi dengan pekerjaan kelompok lainnya, bisa dijalankan melalui satu project kelompok yang utuh. |
| 4 | Semua implementasi secara individu sudah ter-*deploy* secara otomatis lewat CI/CD (wajib), dan sudah mengaplikasikan usaha lebih lanjut untuk menjaga kualitas implementasi, misal: analisis *profiling*, *monitoring*, atau *security*. |

   **Rubrik Penilaian Akhir \- Kelompok**

1. ***Software Design***

| Skala | Deskripsi |
| :---: | ----- |
| 0 | Tidak menggunakan prinsip *software design* sama sekali. |
| 1 | Pemahaman terhadap prinsip dasar *software design* (seperti SOLID Principle) kurang sesuai dengan panduan/referensi. Penggunaan *design pattern* dengan alasan yang tidak sesuai dengan panduan/referensi. |
| 2 | Pemahaman terhadap prinsip dasar *software design* (seperti SOLID Principle) sesuai dengan panduan/referensi. Menggunakan kurang dari 3 *design pattern* secara sesuai dengan panduan/referensi dan deskripsi tugas kelompok. |
| 3 | Menggunakan minimal 3 *design pattern* secara sesuai dengan panduan/referensi dan deskripsi tugas kelompok. |
| 4 | Melakukan usaha lebih mendalam untuk meningkatkan aspek kualitas (*non-functional requirements*) ketika menyusun *software design* dan menjaga standar kualitas untuk seluruh anggota kelompok ketika implementasi. Mampu menunjukkan kondisi sebelum dan sesudah memperbaiki *software design*. |

   

2. ***Software Quality***

| Skala | Deskripsi |
| :---: | ----- |
| 0 | Tidak ada hal yang dilakukan untuk menjaga *software quality*. |
| 1 | Mengimplementasikan sebagian (minimal 3\) teknik dasar *quality* yang diminta (*clean code*, *unit testing*, *functional testing*, *regression testing*, *secure coding*, *profiling*), dengan menerapkan *tools*, seperti Selenium, CodeScene, SonarQube, OSSF Scorecard, atau *tools* lainnya yang terkait. |
| 2 | Mengimplementasikan semua teknik dasar *quality* yang diminta (*clean code*, *unit testing*, *functional testing*, *regression testing*, *secure coding*, *profiling*), dengan menerapkan *tools*, seperti Selenium, CodeScene, SonarQube, OSSF Scorecard, atau *tools* lainnya yang terkait. |
| 3 | Laporan untuk kriteria-kriteria yang disebutkan pada poin 2, berhasil dicapai dengan nilai \>= 90%. |
| 4 | *Profiling* telah dilakukan untuk fungsi-fungsi yang memerlukan performa tinggi/*critical*, dan dapat menjustifikasi kebutuhan performa tersebut. Hasil *profiling* ditindaklanjuti dengan optimasi kode dan berhasil menunjukkan *improvement* minimal 50% dibandingkan dengan implementasi sebelumnya. |

3. ***Software Architecture***

| Skala | Deskripsi |
| :---: | ----- |
| 0 | Tidak dapat implementasi. |
| 1 | Sudah ada upaya implementasi, terlihat ada *commit message* namun tidak berjalan baik.  |
| 2 | Menerapkan *architecture* yang diminta oleh spesifikasi *project* dengan penerapan bagus dan berjalan baik. |
| 3 | Menerapkan *architecture* tambahan selain yang diminta oleh spesifikasi *project* dan dapat menjelaskan manfaatnya bagi *project*. |
| 4 | Mensimulasikan manfaat *architecture* tambahan dengan metode *testing* lanjutan seperti *load testing* atau *security testing*. |

4. ***Software Deployment***

| Skala | Deskripsi |
| :---: | ----- |
| 0 | *Deployment* hanya berjalan di komputer pribadi (localhost) dan tidak ada usaha untuk melakukan otomasi proses *deployment*. Ditambah dengan belum ada usaha untuk menerapkan *observability* maupun menyiapkan sistem *monitoring*. |
| 1 | Sudah ada lingkungan *deployment*, namun persiapannya (*provisioning*) serta prosedur *deployment* masih dilakukan secara manual. Selain itu, sudah ada inisiatif menerapkan *observability* walaupun belum disesuaikan dengan kebutuhan proyek atau datanya belum berhasil masuk ke sistem *monitoring*. |
| 2 | Menerapkan CI/CD dan otomasi lainnya (misal: *automated database migration* atau Infrastructure-as-Code) dalam *provisioning* dan *deployment*, dengan tetap menjaga proses penjaminan mutu (contoh: *guard* berdasarkan hasil *testing* dan *linter*). Selain itu, *metrics* sudah berhasil diambil dari *observability endpoints* dan dapat diolah di sistem *monitoring* dalam bentuk visualisasi *dashboard*. |
| 3 | Menerapkan strategi *deployment* (seperti: Blue/Green, Canary, Feature Flags, dsb.) dan mampu menjelaskan justifikasi mengenai pilihan strategi *deployment* yang digunakan. Selain itu, *observability metrics* yang diambil dan diolah ke dalam *dashboard* adalah *metrics-metrics* yang relevan serta memudahkan pemahaman kondisi sistem saat ini. |
| 4 | Menerapkan prosedur *deployment* lanjutan (seperti: *rollback* *deployment*, *load balancing*, *disaster recovery*, atau prosedur lainnya), dan mampu menunjukkan bahwa prosedur tersebut berjalan dengan baik. |

Source: [BRP](https://docs.google.com/document/d/1SZjjda_9BacM_Nnf5jHznuP-6zra-fS1jS7iBwgLT1M/edit?tab=t.0)

# Design System

Link figma: [https://www.figma.com/design/R6GW6eT7p4gh18NosIxibH/Full-E-Commerce-Website-UI-UX-Design--Community-?node-id=1-3\&t=eRvPylPDB0p6zTSw-1](https://www.figma.com/design/R6GW6eT7p4gh18NosIxibH/Full-E-Commerce-Website-UI-UX-Design--Community-?node-id=1-3&t=eRvPylPDB0p6zTSw-1)

# RabbitMQ (CloudAMQP)

RABBITMQ\_HOST=woodpecker.rmq.cloudamqp.com  
RABBITMQ\_PORT=5671  
RABBITMQ\_USERNAME=iravcdks  
RABBITMQ\_PASSWORD=QhExmsRg1AQyUdzbCLO7-RbMffiDSloN  
RABBITMQ\_VHOST=iravcdks  
RABBITMQ\_SSL\_ENABLED=true

kalo url

* amqps://iravcdks:QhExmsRg1AQyUdzbCLO7-RbMffiDSloN@woodpecker.rmq.cloudamqp.com/iravcdks