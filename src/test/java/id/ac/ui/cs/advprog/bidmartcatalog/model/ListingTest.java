package id.ac.ui.cs.advprog.bidmartcatalog.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ListingTest {

    private Listing listing;

    @BeforeEach
    void setUp() {
        // Inisialisasi objek kosong sebelum setiap pengujian
        listing = new Listing();
    }

    @Test
    void testListingDefaultValues() {
        // Memastikan inisialisasi default dari Java dan @Builder.Default Lombok berjalan
        assertNotNull(listing.getImages(), "Daftar gambar tidak boleh null");
        assertTrue(listing.getImages().isEmpty(), "Daftar gambar harus kosong saat pertama kali dibuat");
        assertEquals(ListingStatus.DRAFT, listing.getStatus(), "Status awal harus DRAFT");
    }

    @Test
    void testListingGettersAndSetters() {
        UUID id = UUID.randomUUID();
        String title = "MacBook Pro M2";
        String description = "Kondisi mulus";
        Category category = new Category();
        String sellerId = "seller-123";
        Double startingPrice = 15000000.0;
        Double currentPrice = 15000000.0;
        Double reservePrice = 16000000.0;
        LocalDateTime endTime = LocalDateTime.now().plusDays(7);
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        // Menguji Setter
        listing.setId(id);
        listing.setTitle(title);
        listing.setDescription(description);
        listing.setCategory(category);
        listing.setSellerId(sellerId);
        listing.setStartingPrice(startingPrice);
        listing.setCurrentPrice(currentPrice);
        listing.setReservePrice(reservePrice);
        listing.setEndTime(endTime);
        listing.setCreatedAt(createdAt);
        listing.setUpdatedAt(updatedAt);
        listing.setStatus(ListingStatus.ACTIVE);

        // Menguji Getter
        assertEquals(id, listing.getId());
        assertEquals(title, listing.getTitle());
        assertEquals(description, listing.getDescription());
        assertEquals(category, listing.getCategory());
        assertEquals(sellerId, listing.getSellerId());
        assertEquals(startingPrice, listing.getStartingPrice());
        assertEquals(currentPrice, listing.getCurrentPrice());
        assertEquals(reservePrice, listing.getReservePrice());
        assertEquals(endTime, listing.getEndTime());
        assertEquals(createdAt, listing.getCreatedAt());
        assertEquals(updatedAt, listing.getUpdatedAt());
        assertEquals(ListingStatus.ACTIVE, listing.getStatus());
    }

    @Test
    void testListingBuilder() {
        UUID id = UUID.randomUUID();
        LocalDateTime endTime = LocalDateTime.now().plusDays(3);

        // Menguji pembuatan objek menggunakan pola Builder
        Listing builtListing = Listing.builder()
                .id(id)
                .title("iPad Pro")
                .sellerId("seller-456")
                .startingPrice(10000000.0)
                .endTime(endTime)
                .build();

        assertEquals(id, builtListing.getId());
        assertEquals("iPad Pro", builtListing.getTitle());
        assertEquals("seller-456", builtListing.getSellerId());
        assertEquals(10000000.0, builtListing.getStartingPrice());
        assertEquals(endTime, builtListing.getEndTime());

        // Memastikan default values tetap teraplikasi saat menggunakan Builder
        assertNotNull(builtListing.getImages());
        assertEquals(ListingStatus.DRAFT, builtListing.getStatus());
    }

    @Test
    void testAddImageToListing() {
        ListingImage image = new ListingImage();
        image.setImageUrl("/uploads/test.jpg");

        listing.getImages().add(image);

        assertEquals(1, listing.getImages().size());
        assertEquals("/uploads/test.jpg", listing.getImages().get(0).getImageUrl());
    }
}