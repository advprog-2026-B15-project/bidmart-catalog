package id.ac.ui.cs.advprog.bidmartcatalog.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListingRequestDTOTest {

    @Test
    @DisplayName("Test ListingRequestDTO Getters and Setters")
    void testGettersAndSetters() {
        ListingRequestDTO dto = new ListingRequestDTO();
        dto.setTitle("Test Title");
        dto.setDescription("Test Description");
        dto.setStartingPrice(100.0);
        dto.setReservePrice(150.0);
        UUID catId = UUID.randomUUID();
        dto.setCategoryId(catId);
        LocalDateTime now = LocalDateTime.now();
        dto.setEndTime(now);

        assertEquals("Test Title", dto.getTitle());
        assertEquals("Test Description", dto.getDescription());
        assertEquals(100.0, dto.getStartingPrice());
        assertEquals(150.0, dto.getReservePrice());
        assertEquals(catId, dto.getCategoryId());
        assertEquals(now, dto.getEndTime());
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

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    @DisplayName("Test ListingRequestDTO ToString")
    void testToString() {
        ListingRequestDTO dto = new ListingRequestDTO();
        dto.setTitle("Test");
        String toString = dto.toString();
        assertTrue(toString.contains("title=Test"));
    }
}
