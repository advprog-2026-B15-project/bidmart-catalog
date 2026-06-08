-- Menggunakan blok DO untuk mendefinisikan konstanta dan menghindari duplikasi literal
DO $$
DECLARE
    ID_ELEKTRONIK CONSTANT UUID := '11111111-1111-1111-1111-111111111111';
    ID_FASHION CONSTANT UUID := '22222222-2222-2222-2222-222222222222';
BEGIN
    INSERT INTO categories (id, name, parent_id) VALUES
        (ID_ELEKTRONIK, 'Elektronik', NULL),
        (ID_FASHION, 'Fashion & Pakaian', NULL),
        ('33333333-3333-3333-3333-333333333333', 'Barang Koleksi', NULL),
        ('44444444-4444-4444-4444-444444444444', 'Otomotif', NULL),
        ('11111111-1111-1111-1111-222222222222', 'Laptop', ID_ELEKTRONIK),
        ('11111111-1111-1111-1111-333333333333', 'Smartphone', ID_ELEKTRONIK),
        ('22222222-2222-2222-2222-444444444444', 'Sepatu', ID_FASHION);
END $$;
