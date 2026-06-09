-- Indexing for performance optimization (Milestone 100)
CREATE INDEX idx_listings_seller_id ON listings(seller_id);
CREATE INDEX idx_listings_status ON listings(status);
CREATE INDEX idx_listings_category_id ON listings(category_id);
CREATE INDEX idx_listings_end_time ON listings(end_time);

-- Optimization for search by title
CREATE INDEX idx_listings_title ON listings(title);

-- Optimization for price range filter
CREATE INDEX idx_listings_current_price ON listings(current_price);
