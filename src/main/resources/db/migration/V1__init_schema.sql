-- 1. Membuat tabel categories terlebih dahulu karena tidak bergantung pada tabel lain
CREATE TABLE categories (
                            id UUID PRIMARY KEY,
                            name VARCHAR(255) NOT NULL,
                            parent_id UUID,
                            CONSTRAINT fk_parent_category FOREIGN KEY (parent_id) REFERENCES categories(id)
);

-- 2. Membuat tabel listings yang memiliki relasi ke categories
CREATE TABLE listings (
                          id UUID PRIMARY KEY,
                          title VARCHAR(255) NOT NULL,
                          description VARCHAR(2000),
                          category_id UUID,
                          seller_id VARCHAR(255) NOT NULL,
                          starting_price DOUBLE PRECISION NOT NULL,
                          current_price DOUBLE PRECISION,
                          reserve_price DOUBLE PRECISION,
                          end_time TIMESTAMP NOT NULL,
                          status VARCHAR(50) DEFAULT 'DRAFT',
                          created_at TIMESTAMP,
                          updated_at TIMESTAMP,
                          CONSTRAINT fk_listing_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- 3. Membuat tabel listing_images yang bergantung pada listings
CREATE TABLE listing_images (
                                id UUID PRIMARY KEY,
                                image_url VARCHAR(255) NOT NULL,
                                is_primary BOOLEAN NOT NULL DEFAULT FALSE,
                                listing_id UUID NOT NULL,
                                CONSTRAINT fk_image_listing FOREIGN KEY (listing_id) REFERENCES listings(id) ON DELETE CASCADE
);