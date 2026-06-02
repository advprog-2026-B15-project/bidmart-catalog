package id.ac.ui.cs.advprog.bidmartcatalog.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertAll("Default values should be correctly initialized",
                () -> assertNotNull(listing.getImages(), "Daftar gambar tidak boleh null"),
                () -> assertTrue(listing.getImages().isEmpty(), "Daftar gambar harus kosong saat pertama kali dibuat"),
                () -> assertEquals(ListingStatus.DRAFT, listing.getStatus(), "Status awal harus DRAFT")
        );
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
        assertAll("Getters should return values set by setters",
                () -> assertEquals(id, listing.getId(), "ID should match"),
                () -> assertEquals(title, listing.getTitle(), "Title should match"),
                () -> assertEquals(description, listing.getDescription(), "Description should match"),
                () -> assertEquals(category, listing.getCategory(), "Category should match"),
                () -> assertEquals(sellerId, listing.getSellerId(), "Seller ID should match"),
                () -> assertEquals(startingPrice, listing.getStartingPrice(), "Starting price should match"),
                () -> assertEquals(currentPrice, listing.getCurrentPrice(), "Current price should match"),
                () -> assertEquals(reservePrice, listing.getReservePrice(), "Reserve price should match"),
                () -> assertEquals(endTime, listing.getEndTime(), "End time should match"),
                () -> assertEquals(createdAt, listing.getCreatedAt(), "Created at should match"),
                () -> assertEquals(updatedAt, listing.getUpdatedAt(), "Updated at should match"),
                () -> assertEquals(ListingStatus.ACTIVE, listing.getStatus(), "Status should match")
        );
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

        assertAll("Builder should correctly initialize fields",
                () -> assertEquals(id, builtListing.getId(), "ID should match built value"),
                () -> assertEquals("iPad Pro", builtListing.getTitle(), "Title should match built value"),
                () -> assertEquals("seller-456", builtListing.getSellerId(), "Seller ID should match built value"),
                () -> assertEquals(10000000.0, builtListing.getStartingPrice(), "Starting price should match built value"),
                () -> assertEquals(endTime, builtListing.getEndTime(), "End time should match built value"),
                () -> assertNotNull(builtListing.getImages(), "Images list should not be null"),
                () -> assertEquals(ListingStatus.DRAFT, builtListing.getStatus(), "Status should be DRAFT by default")
        );
    }

    @Test
    void testAddImageToListing() {
        ListingImage image = new ListingImage();
        image.setImageUrl("/uploads/test.jpg");

        listing.getImages().add(image);

        assertAll("Adding image to listing should update the images list",
                () -> assertEquals(1, listing.getImages().size(), "Images list should contain 1 element"),
                () -> assertEquals("/uploads/test.jpg", listing.getImages().get(0).getImageUrl(), "Image URL should match")
        );
    }
}