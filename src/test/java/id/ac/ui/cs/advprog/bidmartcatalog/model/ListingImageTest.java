package id.ac.ui.cs.advprog.bidmartcatalog.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
                () -> assertEquals(newId, builtImage.getId(), "ListingImage ID should match the built value"),
                () -> assertEquals(newUrl, builtImage.getImageUrl(), "ListingImage URL should match the built value"),
                () -> assertFalse(builtImage.isPrimary(), "ListingImage primary flag should be false"),
                () -> assertEquals(mockListing, builtImage.getListing(), "Listing relationship should be correctly assigned")
        );
    }

    @Test
    @DisplayName("Test Getter and Setter for Image URL")
    void testGetAndSetImageUrl() {
        String updatedUrl = "https://example.com/images/updated-gear.png";
        listingImage.setImageUrl(updatedUrl);
        assertEquals(updatedUrl, listingImage.getImageUrl(), "Getter should return the value set by the setter");
    }

    @Test
    @DisplayName("Test Primary Image Flag")
    void testIsPrimary() {
        listingImage.setPrimary(false);
        boolean firstCheck = listingImage.isPrimary();

        listingImage.setPrimary(true);
        boolean secondCheck = listingImage.isPrimary();

        assertAll("Verify primary flag setting",
            () -> assertFalse(firstCheck, "ListingImage primary flag should be false after setting it to false"),
            () -> assertTrue(secondCheck, "ListingImage primary flag should be true after setting it to true")
        );
    }

    @Test
    @DisplayName("Test Relationship with Listing")
    void testListingRelationship() {
        Listing anotherListing = new Listing();
        listingImage.setListing(anotherListing);

        assertAll("Verify listing relationship",
                () -> assertEquals(anotherListing, listingImage.getListing(), "Listing relationship should match the assigned listing"),
                () -> assertNotNull(listingImage.getListing(), "Listing relationship should not be null")
        );
    }

    @Test
    @DisplayName("Test Constructors (NoArgs & AllArgs)")
    void testConstructors() {
        // Test NoArgsConstructor
        ListingImage emptyImage = new ListingImage();

        // Test AllArgsConstructor
        UUID randomId = UUID.randomUUID();
        ListingImage fullImage = new ListingImage(randomId, "https://url.com", true, mockListing);

        assertAll("Verify constructors",
                () -> assertNull(emptyImage.getImageUrl(), "No-args constructor should initialize imageUrl as null"),
                () -> assertEquals(randomId, fullImage.getId(), "All-args constructor should correctly assign ID"),
                () -> assertTrue(fullImage.isPrimary(), "All-args constructor should correctly assign primary flag"),
                () -> assertEquals(mockListing, fullImage.getListing(), "All-args constructor should correctly assign listing")
        );
    }
}