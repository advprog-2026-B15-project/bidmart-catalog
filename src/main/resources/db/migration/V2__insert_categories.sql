-- Mengisi data kategori default dengan UUID statis agar bisa dipakai
INSERT INTO categories (id, name, parent_id) VALUES
                                                 ('11111111-1111-1111-1111-111111111111', 'Elektronik', NULL),
                                                 ('22222222-2222-2222-2222-222222222222', 'Fashion & Pakaian', NULL),
                                                 ('33333333-3333-3333-3333-333333333333', 'Barang Koleksi', NULL),
                                                 ('44444444-4444-4444-4444-444444444444', 'Otomotif', NULL);