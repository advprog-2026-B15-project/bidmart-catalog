package id.ac.ui.cs.advprog.bidmartcatalog.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListingRequestDTOTest {

    private static final String TEST_TITLE = "Test Title";
    private static final String TEST_DESCRIPTION = "Test Description";

    @Test
    @DisplayName("Test ListingRequestDTO Getters and Setters")
    void testGettersAndSetters() {
        ListingRequestDTO dto = new ListingRequestDTO();
        dto.setTitle(TEST_TITLE);
        dto.setDescription(TEST_DESCRIPTION);
        dto.setStartingPrice(100.0);
        dto.setReservePrice(150.0);
        UUID catId = UUID.randomUUID();
        dto.setCategoryId(catId);
        LocalDateTime now = LocalDateTime.now();
        dto.setEndTime(now);

        assertAll("Verify all getters and setters",
            () -> assertEquals(TEST_TITLE, dto.getTitle(), "Title should match"),
            () -> assertEquals(TEST_DESCRIPTION, dto.getDescription(), "Description should match"),
            () -> assertEquals(100.0, dto.getStartingPrice(), "Starting price should match"),
            () -> assertEquals(150.0, dto.getReservePrice(), "Reserve price should match"),
            () -> assertEquals(catId, dto.getCategoryId(), "Category ID should match"),
            () -> assertEquals(now, dto.getEndTime(), "End time should match")
        );
    }

    @Test
    @DisplayName("Test ListingRequestDTO Equals and HashCode")
    void testEqualsAndHashCode() {
        UUID catId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        ListingRequestDTO dto1 = new ListingRequestDTO();
        dto1.setTitle("Title");
        dto1.setCategoryId(catId);
        dto1.setEndTime(now);

        ListingRequestDTO dto2 = new ListingRequestDTO();
        dto2.setTitle("Title");
        dto2.setCategoryId(catId);
        dto2.setEndTime(now);

        assertAll("Verify equals and hashCode",
            () -> assertEquals(dto1, dto2, "DTOs with same values should be equal"),
            () -> assertEquals(dto1.hashCode(), dto2.hashCode(), "DTOs with same values should have same hashCode")
        );
    }

    @Test
    @DisplayName("Test ListingRequestDTO ToString")
    void testToString() {
        ListingRequestDTO dto = new ListingRequestDTO();
        dto.setTitle(TEST_TITLE);
        String toString = dto.toString();
        assertTrue(toString.contains("title=" + TEST_TITLE), "toString should contain the title");
    }
}
