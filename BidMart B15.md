# BidMart B15

# **B15 \- BidMart**

| NPM | Nama Anggota |  |
| :---: | ----- | ----- |
| 2406495590 | Arya Novalino Pratama |  |
| 2406435805 | Harish Azka Firdaus |  |
| 2406437331 | Keisha Vania Laurent |  |
| 2406361694 | M Naufal Zhafran Rabiul Batara |  |
| 2406401792 | Sean Marcello Maheron |  |

| Link |  |  |  |
| ----- | :---- | ----- | ----- |
| Figma | [https://www.figma.com/design/R6GW6eT7p4gh18NosIxibH/Full-E-Commerce-Website-UI-UX-Design--Community-?node-id=1-3\&t=eRvPylPDB0p6zTSw-1](https://www.figma.com/design/R6GW6eT7p4gh18NosIxibH/Full-E-Commerce-Website-UI-UX-Design--Community-?node-id=1-3&t=eRvPylPDB0p6zTSw-1) |  |  |
| GitHub Organization | [https://github.com/advprog-2026-B15-project](https://github.com/advprog-2026-B3-project) |  |  |
| Auth (Repo Service) | [https://github.com/advprog-2026-B15-project/bidmart-authentication](https://github.com/advprog-2026-B3-project/palmery-auth) |  |  |
| Catalog (Repo Service) | [https://github.com/advprog-2026-B15-project/bidmart-catalog](https://github.com/advprog-2026-B3-project/palmery-manage) |  |  |
| Auction (Repo Service) | [https://github.com/advprog-2026-B15-project/bidmart-auction](https://github.com/advprog-2026-B3-project/palmery-payment) |  |  |
| Booking (Repo Service) | [https://github.com/advprog-2026-B15-project/bidmart-booking](https://palmery.my.id/) |  |  |
| Wallet (Repo Service) | [https://github.com/advprog-2026-B15-project/bidmart-wallet](https://github.com/advprog-2026-B15-project/bidmart-wallet.git) |  |  |

| Deployment |  |  |  |
| ----- | :---- | ----- | ----- |
| FrontEnd | [https://bidmart-b15.vercel.app/](https://bidmart-b15.vercel.app/) |  |  |
| Auth Service |  |  |  |
| Catalog Service |  |  |  |
| Auction Service | [https://3.224.190.70:8083](https://3.224.190.70:8083) |  |  |
| Booking Service |  |  |  |
| Wallet Service |  |  |  |

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

### **5.1 GET /api/bookings/me**

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

[image1]: <data:image/png;base64,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>

[image2]: <data:image/png;base64,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>

[image3]: <data:image/png;base64,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>