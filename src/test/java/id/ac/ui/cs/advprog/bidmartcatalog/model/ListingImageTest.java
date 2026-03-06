package id.ac.ui.cs.advprog.bidmartcatalog.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ListingImageTest {

    private ListingImage listingImage;
    private Listing mockListing;
    private UUID id;
    private String imageUrl;

    @BeforeEach
    void setUp() {
        this.id = UUID.randomUUID();
        this.imageUrl = "https://example.com/images/sport-gear.jpg";
        this.mockListing = new Listing();

        this.listingImage = new ListingImage();
        this.listingImage.setId(id);
        this.listingImage.setImageUrl(imageUrl);
        this.listingImage.setPrimary(true);
        this.listingImage.setListing(mockListing);
    }

    @Test
    @DisplayName("Test ListingImage Creation with Builder")
    void testListingImageBuilder() {
        UUID newId = UUID.randomUUID();
        String newUrl = "https://example.com/images/secondary.jpg";

        ListingImage builtImage = ListingImage.builder()
                .id(newId)
                .imageUrl(newUrl)
                .isPrimary(false)
                .listing(mockListing)
                .build();

        assertAll(
                () -> assertEquals(newId, builtImage.getId()),
                () -> assertEquals(newUrl, builtImage.getImageUrl()),
                () -> assertFalse(builtImage.isPrimary()),
                () -> assertEquals(mockListing, builtImage.getListing())
        );
    }

    @Test
    @DisplayName("Test Getter and Setter for Image URL")
    void testGetAndSetImageUrl() {
        String updatedUrl = "https://example.com/images/updated-gear.png";
        listingImage.setImageUrl(updatedUrl);
        assertEquals(updatedUrl, listingImage.getImageUrl());
    }

    @Test
    @DisplayName("Test Primary Image Flag")
    void testIsPrimary() {
        listingImage.setPrimary(false);
        assertFalse(listingImage.isPrimary());

        listingImage.setPrimary(true);
        assertTrue(listingImage.isPrimary());
    }

    @Test
    @DisplayName("Test Relationship with Listing")
    void testListingRelationship() {
        Listing anotherListing = new Listing();
        listingImage.setListing(anotherListing);

        assertEquals(anotherListing, listingImage.getListing());
        assertNotNull(listingImage.getListing());
    }

    @Test
    @DisplayName("Test Constructors (NoArgs & AllArgs)")
    void testConstructors() {
        // Test NoArgsConstructor
        ListingImage emptyImage = new ListingImage();
        assertNull(emptyImage.getImageUrl());

        // Test AllArgsConstructor
        UUID randomId = UUID.randomUUID();
        ListingImage fullImage = new ListingImage(randomId, "https://url.com", true, mockListing);

        assertAll(
                () -> assertEquals(randomId, fullImage.getId()),
                () -> assertTrue(fullImage.isPrimary()),
                () -> assertEquals(mockListing, fullImage.getListing())
        );
    }
}