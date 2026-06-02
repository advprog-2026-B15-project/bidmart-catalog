# Bidmart Event Spec - ListingPublished

| Versi | Pengarang | Tanggal | Notes |
| :---: | :---: | :---: | ----- |
| 1.0 | Sean | 2026-06-02 | First version |

Event Type: ListingPublished

Producer: bidmart-catalog

Consumer: bidmart-auction, bidmart-booking

## **1) Deskripsi & Aturan Bisnis**

Event ini dipublikasikan ketika sebuah listing baru diaktifkan (status berubah dari `DRAFT` menjadi `ACTIVE`) melalui endpoint `/api/listings/{id}/publish`.

Pada sisi consumer, event ini memicu:
1. **bidmart-auction**: Membuat entitas lelang (auction) baru secara otomatis berdasarkan data listing yang aktif, atau menandai lelang terkait sebagai siap dimulai.
2. **bidmart-booking / notification**: (Opsional) Mengirim notifikasi kepada pengikut seller bahwa ada barang baru yang sedang dilelang.

## **2) Payload Schema**

| Field | Type | Required | Example | Notes |
| :---- | :---- | :---- | :---- | :---- |
| listingId | string (uuid) | Yes | 123e4567-e89b-12d3-a456-426614174000 | ID unik listing di Catalog |
| title | string | Yes | "MacBook Pro M2 2023" | Judul barang |
| sellerId | string | Yes | usr-2406 | ID penjual |
| startingPrice | number (double) | Yes | 15000000.0 | Harga buka lelang |
| reservePrice | number (double) | No | 16000000.0 | Harga cadangan (opsional) |
| endTime | string (ISO-8601) | Yes | 2026-06-10T10:00:00Z | Waktu berakhirnya lelang |

## **3) Contoh Event**

```json
{
  "eventId": "evt-7a2d8b4c-9e1f-4d3a-b2c5-e6f7g8h9i0j1",
  "eventType": "ListingPublished",
  "eventVersion": 1,
  "occurredAt": "2026-06-02T23:45:00Z",
  "source": "bidmart-catalog",
  "payload": {
    "listingId": "123e4567-e89b-12d3-a456-426614174000",
    "title": "MacBook Pro M2 2023",
    "sellerId": "usr-2406",
    "startingPrice": 15000000.0,
    "reservePrice": 16000000.0,
    "endTime": "2026-06-10T10:00:00Z"
  }
}
```

## **4) Open Points Khusus Event Ini**

1. **Routing Key**: Event ini dipublikasikan ke exchange `auction.events` dengan routing key `auction.event.listing-published`.
2. **Idempotency**: Consumer wajib mengecek `listingId` atau `eventId` untuk mencegah pembuatan lelang ganda jika terjadi pengiriman ulang pesan.
3. **Waktu**: Pastikan `endTime` yang dikirim dari Catalog sesuai dengan zona waktu UTC agar sinkron dengan Scheduler di modul Auction.
